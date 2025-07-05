package lk.jiat.ee.auction.jms;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents an auction system event for JMS messaging.
 * Using a structured object allows for more type-safe event handling
 * compared to simple text messages.
 */
public class AuctionEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum EventType {
        BID_PLACED,
        BID_UPDATED,
        AUCTION_CREATED,
        AUCTION_UPDATED,
        AUCTION_CLOSED,
        USER_REGISTERED,
        SYSTEM_EVENT
    }
    
    private EventType type = EventType.SYSTEM_EVENT; // Default type
    private int itemId;
    private String itemName = "";
    private int userId;
    private String username = "";
    private double amount;
    private LocalDateTime timestamp;
    private String message = "";
    
    public AuctionEvent() {
        this.timestamp = LocalDateTime.now();
    }
    
    public AuctionEvent(EventType type, String message) {
        this();
        this.type = type;
        this.message = message != null ? message : "";
    }
    
    // Getters and setters
    public EventType getType() {
        return type;
    }
    
    public void setType(EventType type) {
        this.type = type != null ? type : EventType.SYSTEM_EVENT;
    }
    
    public int getItemId() {
        return itemId;
    }
    
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName != null ? itemName : "";
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username != null ? username : "";
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message != null ? message : "";
    }
    
    @Override
    public String toString() {
        return "AuctionEvent{" +
                "type=" + type +
                ", itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", message='" + message + '\'' +
                '}';
    }
    
    /**
     * Factory method for bid placed events
     */
    public static AuctionEvent bidPlaced(int itemId, String itemName, int userId, String username, double bidAmount) {
        AuctionEvent event = new AuctionEvent();
        event.setType(EventType.BID_PLACED);
        event.setItemId(itemId);
        event.setItemName(itemName);
        event.setUserId(userId);
        event.setUsername(username);
        event.setAmount(bidAmount);
        event.setMessage(String.format("New bid on %s by %s for %.2f", 
            itemName != null ? itemName : "item #" + itemId, 
            username != null ? username : "user #" + userId, 
            bidAmount));
        return event;
    }
    
    /**
     * Factory method for bid updated events
     */
    public static AuctionEvent bidUpdated(int itemId, String itemName, int userId, String username, double newAmount) {
        AuctionEvent event = new AuctionEvent();
        event.setType(EventType.BID_UPDATED);
        event.setItemId(itemId);
        event.setItemName(itemName);
        event.setUserId(userId);
        event.setUsername(username);
        event.setAmount(newAmount);
        event.setMessage(String.format("Bid updated on %s by %s to %.2f", 
            itemName != null ? itemName : "item #" + itemId, 
            username != null ? username : "user #" + userId, 
            newAmount));
        return event;
    }
    
    /**
     * Factory method for auction created events
     */
    public static AuctionEvent auctionCreated(int itemId, String itemName, int sellerId, String sellerName) {
        AuctionEvent event = new AuctionEvent();
        event.setType(EventType.AUCTION_CREATED);
        event.setItemId(itemId);
        event.setItemName(itemName);
        event.setUserId(sellerId);
        event.setUsername(sellerName);
        event.setMessage(String.format("New auction created: %s by %s", 
            itemName != null ? itemName : "item #" + itemId, 
            sellerName != null ? sellerName : "user #" + sellerId));
        return event;
    }
    
    /**
     * Factory method for auction updated events
     */
    public static AuctionEvent auctionUpdated(int itemId, String itemName, int sellerId, String sellerName) {
        AuctionEvent event = new AuctionEvent();
        event.setType(EventType.AUCTION_UPDATED);
        event.setItemId(itemId);
        event.setItemName(itemName);
        event.setUserId(sellerId);
        event.setUsername(sellerName);
        event.setMessage(String.format("Auction updated: %s by %s", 
            itemName != null ? itemName : "item #" + itemId, 
            sellerName != null ? sellerName : "user #" + sellerId));
        return event;
    }
    
    /**
     * Factory method for auction closed events
     */
    public static AuctionEvent auctionClosed(int itemId, String itemName, Double finalPrice, String winnerUsername) {
        AuctionEvent event = new AuctionEvent();
        event.setType(EventType.AUCTION_CLOSED);
        event.setItemId(itemId);
        event.setItemName(itemName != null ? itemName : "");
        if (finalPrice != null) {
            event.setAmount(finalPrice);
        }
        if (winnerUsername != null) {
            event.setUsername(winnerUsername);
            event.setMessage(String.format("Auction closed: %s, won by %s for %.2f", 
                itemName != null ? itemName : "item #" + itemId, 
                winnerUsername, 
                finalPrice != null ? finalPrice : 0.0));
        } else {
            event.setMessage(String.format("Auction closed: %s with no bids", 
                itemName != null ? itemName : "item #" + itemId));
        }
        return event;
    }
} 