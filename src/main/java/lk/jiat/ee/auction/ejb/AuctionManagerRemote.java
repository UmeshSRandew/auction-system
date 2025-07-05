package lk.jiat.ee.auction.ejb;

import jakarta.ejb.Remote;
import lk.jiat.ee.auction.model.AuctionItem;
import lk.jiat.ee.auction.model.Bid; // Added for MyBids feature
import java.util.List;
import java.util.Optional;

@Remote
public interface AuctionManagerRemote {
    void init();
    boolean createAuctionItem(AuctionItem item);
    List<AuctionItem> getAllOpenAuctionItems();
    Optional<AuctionItem> getAuctionItemById(int itemId);
    boolean placeBid(int itemId, int userId, double bidAmount); // userId from session
    String getUsernameById(int userId); // This might be better in UserBean or remove if not used broadly

    List<AuctionItem> getAuctionItemsBySellerId(int sellerUserId);
    List<AuctionItem> getItemsUserBidOn(int userId); // To show items user has bid on
    List<Bid> getBidsByItemId(int itemId); // To show bids for a specific item
    Optional<Bid> getBidById(int bidId); // To get a specific bid
    boolean editBid(int bidId, double newAmount, int userId); // To edit a bid
    
    boolean updateAuctionItem(int itemId, String itemName, String description, int sellerId); // To edit auction details

    void checkAndCloseAuctions(); // For the timer
}