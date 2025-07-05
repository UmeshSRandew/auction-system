package lk.jiat.ee.auction.db;

import lk.jiat.ee.auction.model.AuctionItem;
import lk.jiat.ee.auction.model.Bid; // Added Bid model
import lk.jiat.ee.auction.model.Notification;
import lk.jiat.ee.auction.model.User;
import lk.jiat.ee.auction.util.FormatUtil;
import org.mindrot.jbcrypt.BCrypt; // For password hashing

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional; // Used for returning User/AuctionItem
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseUtil {
    // මේ lines ටික අලුතෙන් දාන්න
    private static final String DB_LOCATION = System.getProperty("user.home") + File.separator + "auction_app_db";
    private static final String DB_FILE_NAME = "auction_advanced.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_LOCATION + File.separator + DB_FILE_NAME;
    private static final Logger LOGGER = Logger.getLogger(DatabaseUtil.class.getName());

    // පරණ initDb() method එක අයින් කරලා, මේක දාන්න
    public static void initDb() {
        try {
            // Ensure the database directory exists
            File dbDir = new File(DB_LOCATION);
            if (!dbDir.exists()) {
                LOGGER.info("Creating database directory at: " + dbDir.getAbsolutePath());
                dbDir.mkdirs();
            }

            // Connect to the database (will create it if it doesn't exist)
            try (Connection conn = getConnection()) {
                LOGGER.info("Initializing database tables...");

                // Create users table
                createUsersTable(conn);

                // Create auction_items table
                createAuctionItemsTable(conn);

                // Create bids table
                createBidsTable(conn);

                // Create notifications table
                createNotificationsTable(conn);

                LOGGER.info("Database tables initialized successfully.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing database", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private static void createUsersTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password_hash TEXT NOT NULL," + // Added password_hash
                    "email TEXT UNIQUE NOT NULL," +   // Added email
                    "registered_at TEXT NOT NULL" +   // Added registered_at
                    ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            LOGGER.info("Users table created or verified.");
        }
    }

    private static void createAuctionItemsTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS auction_items (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "itemName TEXT NOT NULL," +
                    "description TEXT," +
                    "startingPrice REAL NOT NULL," +
                    "currentHighestBid REAL," +
                    "highestBidderId INTEGER," + // Will link to a simplified user concept
                    "sellerUserId INTEGER NOT NULL," + // Added seller user ID
                    "createdAt TEXT NOT NULL," +       // Added created at
                    "endTime TEXT NOT NULL," +         // Store as ISO8601 string or timestamp
                    "status TEXT NOT NULL, " +         // e.g., OPEN, CLOSED, PENDING
                    "FOREIGN KEY (highestBidderId) REFERENCES users(id)," +
                    "FOREIGN KEY (sellerUserId) REFERENCES users(id)" +
                    ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            LOGGER.info("Auction items table created or verified.");
        }
    }

    private static void createBidsTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS bids (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "itemId INTEGER NOT NULL," +
                    "userId INTEGER NOT NULL," +
                    "bidAmount REAL NOT NULL," +
                    "bidTime TEXT NOT NULL," +
                    "FOREIGN KEY (itemId) REFERENCES auction_items(id)," +
                    "FOREIGN KEY (userId) REFERENCES users(id)" +
                    ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            LOGGER.info("Bids table created or verified.");
        }
    }

    private static void createNotificationsTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS notifications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "message TEXT NOT NULL," +
                "event_type TEXT NOT NULL," +
                "item_id INTEGER," +
                "created_at TEXT NOT NULL," +
                "is_read INTEGER DEFAULT 0," +
                "FOREIGN KEY (user_id) REFERENCES users (id)," +
                "FOREIGN KEY (item_id) REFERENCES auction_items (id)" +
                ")";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            LOGGER.info("Notifications table created or verified.");
        }
    }

    private static void addSampleUserIfNotExists(Connection conn) {
        if (getUserByUsername("sampleuser").isEmpty()) {
            User sampleUser = new User();
            sampleUser.setUsername("sampleuser");
            sampleUser.setEmail("sample@example.com");
            String hashedPassword = BCrypt.hashpw("password123", BCrypt.gensalt()); // Hash the password
            sampleUser.setPasswordHash(hashedPassword); // Set the hashed password
            sampleUser.setRegisteredAt(LocalDateTime.now());

            String sql = "INSERT INTO users (username, password_hash, email, registered_at) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, sampleUser.getUsername());
                pstmt.setString(2, sampleUser.getPasswordHash());
                pstmt.setString(3, sampleUser.getEmail());
                pstmt.setString(4, FormatUtil.formatDateTimeForDB(sampleUser.getRegisteredAt()));
                pstmt.executeUpdate();
                LOGGER.info("Created sample user: sampleuser");
            } catch (SQLException e) {
                LOGGER.severe("Error creating sample user: " + e.getMessage());
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            LOGGER.severe("SQLite JDBC driver not found.");
            e.printStackTrace();
            throw new SQLException("SQLite JDBC driver not found.", e);
        }
        return DriverManager.getConnection(DB_URL);
    }

    // --- User Management ---
    public static boolean registerUser(User user) {
        // Check if username or email already exists
        if (getUserByUsername(user.getUsername()).isPresent() || getUserByEmail(user.getEmail()).isPresent()) {
            return false; // Username or email already taken
        }

        String sql = "INSERT INTO users (username, password_hash, email, registered_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            // User object already contains the hashed password from UserBean
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, FormatUtil.formatDateTimeForDB(LocalDateTime.now()));
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.severe("Error creating user: " + e.getMessage());
            return false;
        }
    }

    public static Optional<User> loginUser(String username, String password) {
        Optional<User> userOpt = getUserByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Check hashed password using BCrypt
            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty(); // Login failed
    }

    public static Optional<User> getUserByUsername(String username) {
        String sql = "SELECT id, username, password_hash, email, registered_at FROM users WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setEmail(rs.getString("email"));
                user.setRegisteredAt(FormatUtil.parseDateTime(rs.getString("registered_at")));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            LOGGER.severe("Error getting user by username: " + e.getMessage());
        }
        return Optional.empty();
    }

    public static Optional<User> getUserById(int userId) {
        String sql = "SELECT id, username, password_hash, email, registered_at FROM users WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setEmail(rs.getString("email"));
                user.setRegisteredAt(FormatUtil.parseDateTime(rs.getString("registered_at")));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            LOGGER.severe("Error getting user by id: " + e.getMessage());
        }
        return Optional.empty();
    }

    public static Optional<User> getUserByEmail(String email) {
        String sql = "SELECT id, username, password_hash, email, registered_at FROM users WHERE email = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setEmail(rs.getString("email"));
                user.setRegisteredAt(FormatUtil.parseDateTime(rs.getString("registered_at")));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            LOGGER.severe("Error getting user by email: " + e.getMessage());
        }
        return Optional.empty();
    }

    private static AuctionItem mapResultSetToAuctionItem(ResultSet rs) throws SQLException {
        AuctionItem item = new AuctionItem();
        item.setId(rs.getInt("id"));
        item.setItemName(rs.getString("itemName"));
        item.setDescription(rs.getString("description"));
        item.setStartingPrice(rs.getDouble("startingPrice"));
        item.setCurrentHighestBid(rs.getDouble("currentHighestBid"));
        // Handle null for highestBidderId
        Integer highestBidderId = (Integer) rs.getObject("highestBidderId");
        if (highestBidderId != null) {
            item.setHighestBidderId(highestBidderId);
            getUserById(highestBidderId).ifPresent(u -> item.setHighestBidderUsername(u.getUsername()));
        } else {
            item.setHighestBidderUsername("No bids yet");
        }
        item.setSellerUserId(rs.getInt("sellerUserId"));
        getUserById(item.getSellerUserId()).ifPresent(u -> item.setSellerUsername(u.getUsername()));
        item.setCreatedAt(FormatUtil.parseDateTime(rs.getString("createdAt")));
        item.setEndTime(FormatUtil.parseDateTime(rs.getString("endTime")));
        item.setStatus(rs.getString("status"));

        return item;
    }

    // --- Auction Management ---
    public static boolean createAuctionItem(AuctionItem item) {
        String sql = "INSERT INTO auction_items (itemName, description, startingPrice, currentHighestBid, sellerUserId, createdAt, endTime, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getItemName());
            pstmt.setString(2, item.getDescription());
            pstmt.setDouble(3, item.getStartingPrice());
            pstmt.setDouble(4, item.getStartingPrice()); // Initial highest bid is starting price
            pstmt.setInt(5, item.getSellerUserId());
            pstmt.setString(6, FormatUtil.formatDateTimeForDB(item.getCreatedAt()));
            pstmt.setString(7, FormatUtil.formatDateTimeForDB(item.getEndTime()));
            pstmt.setString(8, "OPEN"); // New auctions are OPEN
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.severe("Error creating auction item: " + e.getMessage());
            return false;
        }
    }

    public static List<AuctionItem> getAllOpenAuctionItems() {
        List<AuctionItem> items = new ArrayList<>();
        // Only show if end time hasn't passed and status is OPEN
        String sql = "SELECT * FROM auction_items WHERE status = 'OPEN' AND endTime > ? ORDER BY endTime ASC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, FormatUtil.formatDateTimeForDB(LocalDateTime.now()));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToAuctionItem(rs));
            }
        } catch (SQLException e) {
            LOGGER.severe("Error getting all open auction items: " + e.getMessage());
        }
        return items;
    }

    public static Optional<AuctionItem> getAuctionItemById(int itemId) {
        String sql = "SELECT * FROM auction_items WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToAuctionItem(rs));
            }
        } catch (SQLException e) {
            LOGGER.severe("Error getting auction item by id: " + e.getMessage());
        }
        return Optional.empty();
    }

    public static boolean placeBid(int itemId, int userId, double bidAmount) {
        Optional<AuctionItem> itemOpt = getAuctionItemById(itemId);
        if (itemOpt.isEmpty()) return false;

        AuctionItem item = itemOpt.get();
        if (!"OPEN".equals(item.getStatus()) || bidAmount <= item.getCurrentHighestBid() || LocalDateTime.now().isAfter(item.getEndTime())) {
            return false; // Auction not open, bid not high enough, or auction ended
        }

        String updateItemSql = "UPDATE auction_items SET currentHighestBid = ?, highestBidderId = ? WHERE id = ?";
        String insertBidSql = "INSERT INTO bids (itemId, userId, bidAmount, bidTime) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Start transaction
            boolean success = false;
            try (PreparedStatement updatePstmt = conn.prepareStatement(updateItemSql);
                 PreparedStatement insertPstmt = conn.prepareStatement(insertBidSql)) {

                updatePstmt.setDouble(1, bidAmount);
                updatePstmt.setInt(2, userId);
                updatePstmt.setInt(3, itemId);
                int itemRows = updatePstmt.executeUpdate();

                if (itemRows > 0) {
                    insertPstmt.setInt(1, itemId);
                    insertPstmt.setInt(2, userId);
                    insertPstmt.setDouble(3, bidAmount);
                    insertPstmt.setString(4, FormatUtil.formatDateTimeForDB(LocalDateTime.now()));
                    int bidRows = insertPstmt.executeUpdate();
                    if (bidRows > 0) {
                        success = true;
                    }
                }

                if (success) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
                return success;

            } catch (SQLException e) {
                conn.rollback();
                LOGGER.severe("Error placing bid: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            LOGGER.severe("Error placing bid: " + e.getMessage());
            return false;
        }
    }

    public static List<AuctionItem> getAuctionItemsBySellerId(int sellerUserId) {
        List<AuctionItem> items = new ArrayList<>();
        String sql = "SELECT * FROM auction_items WHERE sellerUserId = ? ORDER BY createdAt DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, sellerUserId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToAuctionItem(rs));
            }
        } catch (SQLException e) {
            LOGGER.severe("Error getting auction items by seller id: " + e.getMessage());
        }
        return items;
    }

    public static List<AuctionItem> getItemsUserBidOn(int userId) {
        List<AuctionItem> items = new ArrayList<>();
        // Get distinct items where the user has placed a bid
        String sql = "SELECT DISTINCT ai.* FROM auction_items ai " +
                "JOIN bids b ON ai.id = b.itemId " +
                "WHERE b.userId = ? ORDER BY ai.endTime DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToAuctionItem(rs));
            }
        } catch (SQLException e) {
            LOGGER.severe("Error getting items user bid on: " + e.getMessage());
        }
        return items;
    }

    public static List<Bid> getBidsByUserId(int userId) {
        List<Bid> bids = new ArrayList<>();
        // Get all bids made by a specific user
        String sql = "SELECT id, itemId, userId, bidAmount, bidTime FROM bids WHERE userId = ? ORDER BY bidTime DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Bid bid = new Bid();
                bid.setId(rs.getInt("id"));
                bid.setItemId(rs.getInt("itemId"));
                bid.setUserId(rs.getInt("userId"));
                bid.setBidAmount(rs.getDouble("bidAmount"));
                bid.setBidTime(FormatUtil.parseDateTime(rs.getString("bidTime")));
                getUserById(bid.getUserId()).ifPresent(u -> bid.setUsername(u.getUsername())); // Set username for display
                bids.add(bid);
            }
        } catch (SQLException e) {
            LOGGER.severe("Error getting bids by user id: " + e.getMessage());
        }
        return bids;
    }

    public static List<Bid> getBidsByItemId(int itemId) {
        List<Bid> bids = new ArrayList<>();
        // Get all bids for a specific auction item
        String sql = "SELECT id, itemId, userId, bidAmount, bidTime FROM bids WHERE itemId = ? ORDER BY bidTime DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Bid bid = new Bid();
                bid.setId(rs.getInt("id"));
                bid.setItemId(rs.getInt("itemId"));
                bid.setUserId(rs.getInt("userId"));
                bid.setBidAmount(rs.getDouble("bidAmount"));
                bid.setBidTime(FormatUtil.parseDateTime(rs.getString("bidTime")));
                getUserById(bid.getUserId()).ifPresent(u -> bid.setUsername(u.getUsername())); // Set username for display
                bids.add(bid);
            }
        } catch (SQLException e) {
            LOGGER.severe("Error getting bids by item id: " + e.getMessage());
        }
        return bids;
    }
    
    public static Optional<Bid> getBidById(int bidId) {
        String sql = "SELECT id, itemId, userId, bidAmount, bidTime FROM bids WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bidId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Bid bid = new Bid();
                bid.setId(rs.getInt("id"));
                bid.setItemId(rs.getInt("itemId"));
                bid.setUserId(rs.getInt("userId"));
                bid.setBidAmount(rs.getDouble("bidAmount"));
                bid.setBidTime(FormatUtil.parseDateTime(rs.getString("bidTime")));
                getUserById(bid.getUserId()).ifPresent(u -> bid.setUsername(u.getUsername())); // Set username for display
                return Optional.of(bid);
            }
        } catch (SQLException e) {
            LOGGER.severe("Error getting bid by id: " + e.getMessage());
        }
        return Optional.empty();
    }
    
    public static boolean updateBid(int bidId, double newAmount) {
        // First check if the bid exists and get the item ID
        Optional<Bid> bidOpt = getBidById(bidId);
        if (bidOpt.isEmpty()) {
            return false;
        }
        
        Bid bid = bidOpt.get();
        int itemId = bid.getItemId();
        int userId = bid.getUserId();
        
        // Get the auction item to check if it's still open
        Optional<AuctionItem> itemOpt = getAuctionItemById(itemId);
        if (itemOpt.isEmpty() || !"OPEN".equals(itemOpt.get().getStatus())) {
            return false; // Item doesn't exist or is not open
        }
        
        AuctionItem item = itemOpt.get();
        
        // Check if this is the highest bid for this item
        if (item.getHighestBidderId() != null && item.getHighestBidderId() == userId && 
            Math.abs(item.getCurrentHighestBid() - bid.getBidAmount()) < 0.001) {
            // This is the current highest bid, need to update the auction item too
            
            // Update the bid
            String updateBidSql = "UPDATE bids SET bidAmount = ?, bidTime = ? WHERE id = ?";
            // Update the auction item
            String updateItemSql = "UPDATE auction_items SET currentHighestBid = ? WHERE id = ?";
            
            try (Connection conn = getConnection()) {
                conn.setAutoCommit(false);
                try {
                    // Update the bid
                    try (PreparedStatement pstmt = conn.prepareStatement(updateBidSql)) {
                        pstmt.setDouble(1, newAmount);
                        pstmt.setString(2, FormatUtil.formatDateTimeForDB(LocalDateTime.now()));
                        pstmt.setInt(3, bidId);
                        pstmt.executeUpdate();
                    }
                    
                    // Update the auction item
                    try (PreparedStatement pstmt = conn.prepareStatement(updateItemSql)) {
                        pstmt.setDouble(1, newAmount);
                        pstmt.setInt(2, itemId);
                        pstmt.executeUpdate();
                    }
                    
                    conn.commit();
                    return true;
                } catch (SQLException e) {
                    conn.rollback();
                    LOGGER.severe("Error updating bid: " + e.getMessage());
                    return false;
                } finally {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                LOGGER.severe("Error updating bid: " + e.getMessage());
                return false;
            }
        } else {
            // This is not the highest bid, just update the bid record
            String updateBidSql = "UPDATE bids SET bidAmount = ?, bidTime = ? WHERE id = ?";
            try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(updateBidSql)) {
                pstmt.setDouble(1, newAmount);
                pstmt.setString(2, FormatUtil.formatDateTimeForDB(LocalDateTime.now()));
                pstmt.setInt(3, bidId);
                int rows = pstmt.executeUpdate();
                return rows > 0;
            } catch (SQLException e) {
                LOGGER.severe("Error updating bid: " + e.getMessage());
                return false;
            }
        }
    }

    public static List<AuctionItem> getOpenAuctionsPastEndTime() {
        List<AuctionItem> items = new ArrayList<>();
        String sql = "SELECT * FROM auction_items WHERE status = 'OPEN' AND endTime <= ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, FormatUtil.formatDateTimeForDB(LocalDateTime.now()));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToAuctionItem(rs));
            }
        } catch (SQLException e) {
            LOGGER.severe("Error getting open auctions past end time: " + e.getMessage());
        }
        return items;
    }

    public static boolean closeAuction(int itemId) {
        String sql = "UPDATE auction_items SET status = 'CLOSED' WHERE id = ? AND status = 'OPEN'";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.severe("Error closing auction: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean updateAuctionItem(int itemId, String itemName, String description, int sellerId) {
        // First check if the auction item exists and belongs to the seller
        Optional<AuctionItem> itemOpt = getAuctionItemById(itemId);
        if (itemOpt.isEmpty() || itemOpt.get().getSellerUserId() != sellerId) {
            return false; // Item doesn't exist or doesn't belong to the seller
        }
        
        AuctionItem item = itemOpt.get();
        
        // Only allow edits if the auction is still in OPEN or PENDING status
        if ("CLOSED".equals(item.getStatus())) {
            return false; // Cannot edit closed auctions
        }
        
        String sql = "UPDATE auction_items SET itemName = ?, description = ? WHERE id = ? AND sellerUserId = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemName);
            pstmt.setString(2, description);
            pstmt.setInt(3, itemId);
            pstmt.setInt(4, sellerId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.severe("Error updating auction item: " + e.getMessage());
            return false;
        }
    }

    // --- Notification Management ---
    /**
     * Create a new notification in the database
     */
    public static boolean createNotification(Notification notification) {
        String sql = "INSERT INTO notifications (user_id, message, event_type, item_id, created_at, is_read) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, notification.getUserId());
            pstmt.setString(2, notification.getMessage());
            pstmt.setString(3, notification.getEventType());
            
            if (notification.getItemId() != null) {
                pstmt.setInt(4, notification.getItemId());
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            pstmt.setString(5, notification.getCreatedAt().toString());
            pstmt.setInt(6, notification.isRead() ? 1 : 0);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    notification.setId(generatedKeys.getInt(1));
                    return true;
                }
            }
            
            return false;
        } catch (SQLException e) {
            LOGGER.severe("Error creating notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get notifications for a specific user
     */
    public static List<Notification> getNotificationsForUser(int userId, boolean includeRead) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? " + 
                     (includeRead ? "" : "AND is_read = 0 ") + 
                     "ORDER BY created_at DESC";
        
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Notification notification = new Notification();
                    notification.setId(rs.getInt("id"));
                    notification.setUserId(rs.getInt("user_id"));
                    notification.setMessage(rs.getString("message"));
                    notification.setEventType(rs.getString("event_type"));
                    
                    int itemId = rs.getInt("item_id");
                    if (!rs.wasNull()) {
                        notification.setItemId(itemId);
                    }
                    
                    notification.setCreatedAt(LocalDateTime.parse(rs.getString("created_at")));
                    notification.setRead(rs.getInt("is_read") == 1);
                    
                    notifications.add(notification);
                }
            }
            
            return notifications;
        } catch (SQLException e) {
            LOGGER.severe("Error getting notifications for user " + userId + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Mark a notification as read
     */
    public static boolean markNotificationAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, notificationId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.severe("Error marking notification " + notificationId + " as read: " + e.getMessage());
            return false;
        }
    }

    /**
     * Mark all notifications for a user as read
     */
    public static boolean markAllNotificationsAsRead(int userId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOGGER.severe("Error marking all notifications as read for user " + userId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete old notifications (e.g., older than 30 days)
     */
    public static boolean deleteOldNotifications(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        String sql = "DELETE FROM notifications WHERE created_at < ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cutoffDate.toString());
            
            int affectedRows = pstmt.executeUpdate();
            LOGGER.info("Deleted " + affectedRows + " old notifications.");
            return true;
        } catch (SQLException e) {
            LOGGER.severe("Error deleting old notifications: " + e.getMessage());
            return false;
        }
    }
}