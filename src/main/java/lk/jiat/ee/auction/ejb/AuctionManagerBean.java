package lk.jiat.ee.auction.ejb;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton; // Changed to Singleton for Timer and state management if needed
import jakarta.ejb.Startup; // To start timer on deployment
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerService;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Topic;
import lk.jiat.ee.auction.db.DatabaseUtil;
import lk.jiat.ee.auction.jms.AuctionEvent;
import lk.jiat.ee.auction.jms.AuctionEventProducer;
import lk.jiat.ee.auction.model.AuctionItem;
import lk.jiat.ee.auction.model.Bid;
import lk.jiat.ee.auction.model.User;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton // Changed to Singleton to manage the timer
@Startup   // Initialize on application startup
public class AuctionManagerBean implements AuctionManagerRemote {

    private static final Logger LOGGER = Logger.getLogger(AuctionManagerBean.class.getName());

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/AuctionTopic")
    private Topic auctionTopic;
    
    @Inject
    private AuctionEventProducer eventProducer;

    @Resource
    private TimerService timerService; // For scheduling auction closing

    @PostConstruct
    @Override
    public void init() {
        LOGGER.info("AuctionManagerBean (Singleton) initializing...");
        try {
            // DatabaseUtil.initDb(); // Moved to ServletContextListener for app-wide init
            LOGGER.info("AuctionManagerBean initialized. Timer will be set up.");
            // Set up a programmatic timer if needed, or rely on @Schedule
            scheduleAuctionClosingTimer(); // Programmatic timer setup
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during AuctionManagerBean initialization", e);
        }
    }

    // Programmatic timer setup (alternative or complementary to @Schedule)
    public void scheduleAuctionClosingTimer() {
        // Check if a timer with this name already exists to avoid duplicates
        boolean timerExists = false;
        for (Timer t : timerService.getTimers()) {
            if ("auctionClosingTimer".equals(t.getInfo())) {
                timerExists = true;
                break;
            }
        }
        if (!timerExists) {
            // Schedule to run every 5 minutes (adjust as needed)
            // timerService.createIntervalTimer(0, 5 * 60 * 1000, new TimerConfig("auctionClosingTimer", false));
            // For more precise @Schedule is often easier.
            // This example assumes @Schedule will handle it. If you use programmatic, remove @Schedule or ensure they don't conflict.
            LOGGER.info("Programmatic timer for auction closing could be set here if not using @Schedule.");
        } else {
            LOGGER.info("Auction closing timer already exists.");
        }
    }


    @Override
    public boolean createAuctionItem(AuctionItem item) {
        // Basic validation
        if (item.getSellerUserId() == null || item.getEndTime() == null || item.getItemName() == null) {
            LOGGER.warning("Auction creation failed: Missing seller, end time, or item name.");
            return false;
        }
        
        LOGGER.info("Creating auction item: " + item.getItemName() + " by user ID: " + item.getSellerUserId());
        boolean success = DatabaseUtil.createAuctionItem(item);
        
        if (success) {
            // Get the user info for the seller
            String sellerName = getUsernameById(item.getSellerUserId());
            
            // Send event notification
            AuctionEvent event = AuctionEvent.auctionCreated(
                item.getId(), 
                item.getItemName(), 
                item.getSellerUserId(), 
                sellerName
            );
            
            eventProducer.sendEvent(event);
        }
        
        return success;
    }

    @Override
    public List<AuctionItem> getAllOpenAuctionItems() {
        return DatabaseUtil.getAllOpenAuctionItems();
    }

    @Override
    public Optional<AuctionItem> getAuctionItemById(int itemId) {
        return DatabaseUtil.getAuctionItemById(itemId);
    }

    @Override
    public String getUsernameById(int userId) { // Consider moving to UserBean
        Optional<User> userOpt = DatabaseUtil.getUserById(userId);
        return userOpt.map(User::getUsername).orElse("Unknown User");
    }

    @Override
    public boolean placeBid(int itemId, int userId, double bidAmount) {
        Optional<User> userOpt = DatabaseUtil.getUserById(userId);
        if (userOpt.isEmpty()) {
            LOGGER.warning("Bid failed: User ID " + userId + " not found.");
            return false;
        }
        String username = userOpt.get().getUsername();

        Optional<AuctionItem> itemOpt = DatabaseUtil.getAuctionItemById(itemId);
        if (itemOpt.isEmpty()) {
            LOGGER.warning("Bid failed: Item ID " + itemId + " not found.");
            return false;
        }
        AuctionItem item = itemOpt.get();

        boolean success = DatabaseUtil.placeBid(itemId, userId, bidAmount);
        if (success) {
            LOGGER.info("Bid placed successfully for item " + itemId + " by user " + username + " (ID: " + userId + ") for amount " + bidAmount);
            
            // Create and send the event
            AuctionEvent event = AuctionEvent.bidPlaced(
                itemId,
                item.getItemName(),
                userId,
                username,
                bidAmount
            );
            
            eventProducer.sendEvent(event);
        } else {
            LOGGER.warning("Failed to place bid for item " + itemId + " by user " + username);
        }
        return success;
    }

    @Override
    public List<AuctionItem> getAuctionItemsBySellerId(int sellerUserId) {
        return DatabaseUtil.getAuctionItemsBySellerId(sellerUserId);
    }

    @Override
    public List<AuctionItem> getItemsUserBidOn(int userId) {
        return DatabaseUtil.getItemsUserBidOn(userId);
    }

    @Override
    public List<Bid> getBidsByItemId(int itemId) {
        return DatabaseUtil.getBidsByItemId(itemId);
    }
    
    @Override
    public Optional<Bid> getBidById(int bidId) {
        return DatabaseUtil.getBidById(bidId);
    }
    
    @Override
    public boolean editBid(int bidId, double newAmount, int userId) {
        // First check if the bid exists and belongs to the user
        Optional<Bid> bidOpt = DatabaseUtil.getBidById(bidId);
        if (bidOpt.isEmpty() || bidOpt.get().getUserId() != userId) {
            LOGGER.warning("Edit bid failed: Bid not found or doesn't belong to user " + userId);
            return false;
        }
        
        Bid bid = bidOpt.get();
        
        // Get the auction item info
        Optional<AuctionItem> itemOpt = DatabaseUtil.getAuctionItemById(bid.getItemId());
        if (itemOpt.isEmpty()) {
            LOGGER.warning("Edit bid failed: Item not found for bid " + bidId);
            return false;
        }
        
        AuctionItem item = itemOpt.get();
        
        boolean success = DatabaseUtil.updateBid(bidId, newAmount);
        if (success) {
            LOGGER.info("Bid updated successfully for item " + bid.getItemId() + " by user " + userId + " to new amount " + newAmount);
            
            // Create and send the event
            AuctionEvent event = AuctionEvent.bidUpdated(
                item.getId(),
                item.getItemName(),
                userId,
                getUsernameById(userId),
                newAmount
            );
            
            eventProducer.sendEvent(event);
        } else {
            LOGGER.warning("Failed to update bid " + bidId + " for user " + userId);
        }
        return success;
    }
    
    @Override
    public boolean updateAuctionItem(int itemId, String itemName, String description, int sellerId) {
        // Basic validation
        if (itemName == null || itemName.trim().isEmpty()) {
            LOGGER.warning("Update auction failed: Item name cannot be empty.");
            return false;
        }
        
        Optional<User> userOpt = DatabaseUtil.getUserById(sellerId);
        if (userOpt.isEmpty()) {
            LOGGER.warning("Update auction failed: User ID " + sellerId + " not found.");
            return false;
        }
        
        String username = userOpt.get().getUsername();
        
        boolean success = DatabaseUtil.updateAuctionItem(itemId, itemName, description, sellerId);
        if (success) {
            LOGGER.info("Auction item " + itemId + " updated successfully by seller " + username + " (ID: " + sellerId + ")");
            
            // Create and send the event
            AuctionEvent event = AuctionEvent.auctionUpdated(
                itemId,
                itemName,
                sellerId,
                username
            );
            
            eventProducer.sendEvent(event);
        } else {
            LOGGER.warning("Failed to update auction item " + itemId + " by seller " + username);
        }
        return success;
    }

    // Automatic Timer using @Schedule
    // Runs every 5 minutes. Adjust cron expression as needed.
    // Example: "0 */5 * * * ?" means "at second 0 of every 5th minute"
    @Schedule(minute = "*/5", hour = "*", persistent = false) // Runs every 5 minutes
    public void scheduledAuctionCheck() {
        LOGGER.info("Scheduled auction check running...");
        checkAndCloseAuctions();
    }

    @Timeout // This method is called when a programmatic timer expires (if you created one with TimerService)
    public void programmaticTimeout(Timer timer) {
        LOGGER.info("Programmatic Timer event: " + timer.getInfo());
        if ("auctionClosingTimer".equals(timer.getInfo())) {
            checkAndCloseAuctions();
        }
    }


    @Override
    public void checkAndCloseAuctions() {
        LOGGER.info("Checking for auctions to close...");
        List<AuctionItem> itemsToClose = DatabaseUtil.getOpenAuctionsPastEndTime();
        if (itemsToClose.isEmpty()) {
            LOGGER.info("No auctions to close at this time.");
            return;
        }
        for (AuctionItem item : itemsToClose) {
            boolean closed = DatabaseUtil.closeAuction(item.getId());
            if (closed) {
                LOGGER.info("Auction ID: " + item.getId() + " ('" + item.getItemName() + "') has been automatically closed.");
                
                // Create and send auction closed event
                AuctionEvent event = AuctionEvent.auctionClosed(
                    item.getId(),
                    item.getItemName(),
                    item.getCurrentHighestBid(),
                    item.getHighestBidderUsername()
                );
                
                eventProducer.sendEvent(event);
            } else {
                LOGGER.warning("Failed to close auction ID: " + item.getId());
            }
        }
    }
}