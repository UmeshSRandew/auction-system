package lk.jiat.ee.auction.ejb; // Ensure correct package

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage; // Ensure jakarta.jms
import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/AuctionTopic"),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Topic")
        }
)
public class BidMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(BidMDB.class.getName());

    public BidMDB() {
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String content = textMessage.getText();
                LOGGER.info("BidMDB received message: " + content);
                // Further processing could be added here, e.g., updating a live dashboard, sending email notifications, etc.
            } else {
                LOGGER.warning("Received non-text message in BidMDB: " + message.getClass().getName());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing message in BidMDB", e);
        }
    }
}