package lk.jiat.ee.auction.jms;

import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Queue;
import jakarta.jms.Topic;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton EJB for producing JMS messages for auction system events.
 * Uses both a topic for broadcasting events and a queue for processing notifications.
 */
@Singleton
public class AuctionEventProducer {
    private static final Logger LOGGER = Logger.getLogger(AuctionEventProducer.class.getName());
    
    @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(lookup = "jms/AuctionTopic")
    private Topic auctionTopic;
    
    @Resource(lookup = "jms/NotificationQueue")
    private Queue notificationQueue;
    
    /**
     * Sends an auction event to the topic for broadcasting to all listeners
     */
    public void sendEventToTopic(AuctionEvent event) {
        try {
            if (auctionTopic == null) {
                LOGGER.severe("AuctionTopic is null! Resource injection failed.");
                return;
            }
            
            if (connectionFactory == null) {
                LOGGER.severe("ConnectionFactory is null! Resource injection failed.");
                return;
            }
            
            try (JMSContext context = connectionFactory.createContext()) {
                ObjectMessage message = context.createObjectMessage(event);
                // Add a property to filter by event type if needed
                message.setStringProperty("EVENT_TYPE", event.getType().name());
                
                context.createProducer().send(auctionTopic, message);
                LOGGER.info("JMS event sent to topic: " + event.getMessage());
            }
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Failed to send event to topic", e);
        }
    }
    
    /**
     * Sends an auction event to the queue for notification processing
     */
    public void sendEventToQueue(AuctionEvent event) {
        try {
            if (notificationQueue == null) {
                LOGGER.severe("NotificationQueue is null! Resource injection failed.");
                return;
            }
            
            if (connectionFactory == null) {
                LOGGER.severe("ConnectionFactory is null! Resource injection failed.");
                return;
            }
            
            try (JMSContext context = connectionFactory.createContext()) {
                ObjectMessage message = context.createObjectMessage(event);
                // Add a property to filter by event type if needed
                message.setStringProperty("EVENT_TYPE", event.getType().name());
                
                context.createProducer().send(notificationQueue, message);
                LOGGER.info("JMS event sent to queue: " + event.getMessage());
            }
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Failed to send event to queue", e);
        }
    }
    
    /**
     * Convenience method to send event to both topic and queue
     */
    public void sendEvent(AuctionEvent event) {
        sendEventToTopic(event);
        sendEventToQueue(event);
    }
} 