package lk.jiat.ee.auction.ejb;

import jakarta.ejb.Remote;
import lk.jiat.ee.auction.model.User;
import java.util.Optional;

@Remote
public interface UserBeanRemote {
    boolean registerUser(User user);
    Optional<User> loginUser(String username, String password);
    Optional<User> getUserByUsername(String username); // Already in DatabaseUtil, but good to expose via EJB
    Optional<User> getUserById(int userId);
}