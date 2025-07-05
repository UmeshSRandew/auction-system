package lk.jiat.ee.auction.jms;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import lk.jiat.ee.auction.service.NotificationService;

import jakarta.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Message-Driven Bean that listens for auction events on the NotificationQueue
 * and processes them for user notifications (e.g. email, database storage, etc.)
 */
@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/NotificationQueue"),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
                @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
        }
)
public class NotificationQueueListener implements MessageListener {
    private static final Logger LOGGER = Logger.getLogger(NotificationQueueListener.class.getName());
    
    @Inject
    private NotificationService notificationService;
    
    public NotificationQueueListener() {
        LOGGER.info("NotificationQueueListener initialized");
    }
    
    @Override
    public void onMessage(Message message) {
        try {
            LOGGER.info("NotificationQueueListener received a message");
            if (message instanceof ObjectMessage) {
                ObjectMessage objectMessage = (ObjectMessage) message;
                Object object = objectMessage.getObject();
                
                if (object instanceof AuctionEvent) {
                    AuctionEvent event = (AuctionEvent) object;
                    LOGGER.info("Processing notification for event: " + event.getMessage());
                    
                    if (notificationService == null) {
                        LOGGER.severe("NotificationService is null! Dependency injection failed.");
                        return;
                    }
                    
                    // Process the notification based on event type
                    switch (event.getType()) {
                        case BID_PLACED:
                            notificationService.processBidPlacedNotification(event);
                            break;
                        case BID_UPDATED:
                            notificationService.processBidUpdatedNotification(event);
                            break;
                        case AUCTION_CREATED:
                            notificationService.processAuctionCreatedNotification(event);
                            break;
                        case AUCTION_UPDATED:
                            notificationService.processAuctionUpdatedNotification(event);
                            break;
                        case AUCTION_CLOSED:
                            notificationService.processAuctionClosedNotification(event);
                            break;
                        case USER_REGISTERED:
                            notificationService.processUserRegisteredNotification(event);
                            break;
                        default:
                            LOGGER.warning("Unknown event type: " + event.getType());
                    }
                } else {
                    LOGGER.warning("Received non-AuctionEvent object: " + object.getClass().getName());
                }
            } else {
                LOGGER.warning("Received non-ObjectMessage: " + message.getClass().getName());
            }
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Error processing JMS message", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in NotificationQueueListener", e);
        }
    }
} 