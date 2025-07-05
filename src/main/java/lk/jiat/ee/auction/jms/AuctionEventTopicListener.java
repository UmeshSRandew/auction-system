package lk.jiat.ee.auction.jms;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;
import lk.jiat.ee.auction.websocket.AuctionWebsocketEndpoint;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Message-Driven Bean that listens for auction events on the AuctionTopic
 * and broadcasts them via WebSocket to connected clients.
 */
@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/AuctionTopic"),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Topic"),
                @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
                @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "NonDurable"),
                @ActivationConfigProperty(propertyName = "clientId", propertyValue = "AuctionWebsocketClient"),
                @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "AuctionWebsocketSubscription")
        }
)
public class AuctionEventTopicListener implements MessageListener {
    private static final Logger LOGGER = Logger.getLogger(AuctionEventTopicListener.class.getName());
    
    public AuctionEventTopicListener() {
        LOGGER.info("AuctionEventTopicListener initialized");
    }
    
    @Override
    public void onMessage(Message message) {
        try {
            LOGGER.info("AuctionEventTopicListener received a message");
            if (message instanceof ObjectMessage) {
                ObjectMessage objectMessage = (ObjectMessage) message;
                Object object = objectMessage.getObject();
                
                if (object instanceof AuctionEvent) {
                    AuctionEvent event = (AuctionEvent) object;
                    LOGGER.info("Received auction event: " + event.getMessage());
                    
                    // Use static method to broadcast to WebSocket clients
                    AuctionWebsocketEndpoint.broadcast(event);
                } else {
                    LOGGER.warning("Received non-AuctionEvent object: " + object.getClass().getName());
                }
            } else {
                LOGGER.warning("Received non-ObjectMessage: " + message.getClass().getName());
            }
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Error processing JMS message", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in AuctionEventTopicListener", e);
        }
    }
} 