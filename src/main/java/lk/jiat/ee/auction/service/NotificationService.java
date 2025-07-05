package lk.jiat.ee.auction.service;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import lk.jiat.ee.auction.db.DatabaseUtil;
import lk.jiat.ee.auction.jms.AuctionEvent;
import lk.jiat.ee.auction.model.AuctionItem;
import lk.jiat.ee.auction.model.Notification;
import lk.jiat.ee.auction.model.User;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service for processing auction event notifications.
 * Handles various types of notifications such as emails, database records, etc.
 */
@Singleton
public class NotificationService {
    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());
    
    /**
     * Process a bid placed notification
     */
    public void processBidPlacedNotification(AuctionEvent event) {
        LOGGER.info("Processing bid placed notification: " + event.getMessage());
        
        try {
            // Create notification for the seller
            Optional<AuctionItem> itemOpt = DatabaseUtil.getAuctionItemById(event.getItemId());
            if (itemOpt.isPresent()) {
                AuctionItem item = itemOpt.get();
                // Notify the seller
                createNotificationForUser(
                    item.getSellerUserId(),
                    event.getMessage(),
                    event.getType().name(),
                    event.getItemId()
                );
                
                // Notify previous highest bidder (if exists and different from current bidder)
                Integer highestBidderId = item.getHighestBidderId();
                if (highestBidderId != null && highestBidderId != event.getUserId()) {
                    String outbidMessage = String.format(
                        "You've been outbid on %s! New bid: %.2f by %s",
                        item.getItemName(),
                        event.getAmount(),
                        event.getUsername()
                    );
                    createNotificationForUser(
                        highestBidderId,
                        outbidMessage,
                        "OUTBID",
                        event.getItemId()
                    );
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Error processing bid placed notification: " + e.getMessage());
        }
    }
    
    /**
     * Process a bid updated notification
     */
    public void processBidUpdatedNotification(AuctionEvent event) {
        LOGGER.info("Processing bid updated notification: " + event.getMessage());
        
        try {
            // Create notification for the seller
            Optional<AuctionItem> itemOpt = DatabaseUtil.getAuctionItemById(event.getItemId());
            if (itemOpt.isPresent()) {
                AuctionItem item = itemOpt.get();
                // Notify the seller
                createNotificationForUser(
                    item.getSellerUserId(),
                    event.getMessage(),
                    event.getType().name(),
                    event.getItemId()
                );
            }
        } catch (Exception e) {
            LOGGER.severe("Error processing bid updated notification: " + e.getMessage());
        }
    }
    
    /**
     * Process an auction created notification
     */
    public void processAuctionCreatedNotification(AuctionEvent event) {
        LOGGER.info("Processing auction created notification: " + event.getMessage());
        
        // No specific notifications needed for now
        // In a real app, you might notify users interested in this category
    }
    
    /**
     * Process an auction updated notification
     */
    public void processAuctionUpdatedNotification(AuctionEvent event) {
        LOGGER.info("Processing auction updated notification: " + event.getMessage());
        
        try {
            // Create notifications for all bidders
            Optional<AuctionItem> itemOpt = DatabaseUtil.getAuctionItemById(event.getItemId());
            if (itemOpt.isPresent()) {
                // Notify bidders that the auction details have changed
                DatabaseUtil.getBidsByItemId(event.getItemId())
                    .stream()
                    .map(bid -> bid.getUserId())
                    .distinct()
                    .forEach(bidderId -> {
                        createNotificationForUser(
                            bidderId,
                            String.format("Auction '%s' details have been updated", event.getItemName()),
                            event.getType().name(),
                            event.getItemId()
                        );
                    });
            }
        } catch (Exception e) {
            LOGGER.severe("Error processing auction updated notification: " + e.getMessage());
        }
    }
    
    /**
     * Process an auction closed notification
     */
    public void processAuctionClosedNotification(AuctionEvent event) {
        LOGGER.info("Processing auction closed notification: " + event.getMessage());
        
        try {
            // Get the auction item
            Optional<AuctionItem> itemOpt = DatabaseUtil.getAuctionItemById(event.getItemId());
            if (itemOpt.isPresent()) {
                AuctionItem item = itemOpt.get();
                
                // Notify the seller
                createNotificationForUser(
                    item.getSellerUserId(),
                    String.format("Your auction '%s' has ended", item.getItemName()),
                    event.getType().name(),
                    event.getItemId()
                );
                
                // Notify the winner (if any)
                if (item.getHighestBidderId() != null) {
                    createNotificationForUser(
                        item.getHighestBidderId(),
                        String.format("You won the auction for '%s' with a bid of %.2f", 
                            item.getItemName(), item.getCurrentHighestBid()),
                        "AUCTION_WON",
                        event.getItemId()
                    );
                }
                
                // Notify other bidders who didn't win
                DatabaseUtil.getBidsByItemId(event.getItemId())
                    .stream()
                    .map(bid -> bid.getUserId())
                    .distinct()
                    .filter(bidderId -> !bidderId.equals(item.getHighestBidderId()))
                    .forEach(bidderId -> {
                        createNotificationForUser(
                            bidderId,
                            String.format("The auction for '%s' has ended. You didn't win this time.", item.getItemName()),
                            "AUCTION_LOST",
                            event.getItemId()
                        );
                    });
            }
        } catch (Exception e) {
            LOGGER.severe("Error processing auction closed notification: " + e.getMessage());
        }
    }
    
    /**
     * Process a user registered notification
     */
    public void processUserRegisteredNotification(AuctionEvent event) {
        LOGGER.info("Processing user registered notification: " + event.getMessage());
        // In a real application, you might send a welcome email
    }
    
    /**
     * Helper method to create a notification for a user
     */
    private void createNotificationForUser(int userId, String message, String eventType, int itemId) {
        Notification notification = new Notification(userId, message, eventType, itemId);
        DatabaseUtil.createNotification(notification);
    }
    
    /**
     * Scheduled job to clean up old notifications (runs daily at midnight)
     */
    @Schedule(hour = "0", minute = "0", second = "0", persistent = false)
    public void cleanupOldNotifications() {
        LOGGER.info("Running scheduled cleanup of old notifications");
        // Keep notifications for 30 days
        DatabaseUtil.deleteOldNotifications(30);
    }
} 