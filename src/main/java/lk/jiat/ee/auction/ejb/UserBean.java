package lk.jiat.ee.auction.ejb;

import jakarta.ejb.Stateless;
import lk.jiat.ee.auction.db.DatabaseUtil;
import lk.jiat.ee.auction.model.User;
import java.util.Optional;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;

@Stateless
public class UserBean implements UserBeanRemote {
    private static final Logger LOGGER = Logger.getLogger(UserBean.class.getName());

    @Override
    public boolean registerUser(User user) {
        // Basic validation (can be expanded)
        if (user.getUsername() == null || user.getUsername().trim().isEmpty() ||
                user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty() || // Raw password from form
                user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            LOGGER.warning("User registration failed: Missing required fields.");
            return false;
        }
        
        String rawPassword = user.getPasswordHash(); // Get the plain password from the object
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt()); // Hash it
        user.setPasswordHash(hashedPassword); // Set the hashed password back to the object


        LOGGER.info("Attempting to register user: " + user.getUsername());
        return DatabaseUtil.registerUser(user); // Now DatabaseUtil gets the hashed password
    }

    @Override
    public Optional<User> loginUser(String username, String password) {
        LOGGER.info("Attempting to login user: " + username);
        return DatabaseUtil.loginUser(username, password);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return DatabaseUtil.getUserByUsername(username);
    }

    @Override
    public Optional<User> getUserById(int userId) {
        return DatabaseUtil.getUserById(userId);
    }
}