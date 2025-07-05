package lk.jiat.ee.auction.model;

import lk.jiat.ee.auction.util.FormatUtil;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AuctionItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String itemName;
    private String description;
    private double startingPrice;
    private double currentHighestBid;
    private Integer highestBidderId; // Can be null if no bids
    private String highestBidderUsername; // For display
    private Integer sellerUserId; // Link to the user who created the auction
    private String sellerUsername; // For display
    private LocalDateTime createdAt;
    private LocalDateTime endTime;
    private String status; // "OPEN", "CLOSED", "PENDING"

    public AuctionItem() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING"; // Initial status
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getStartingPrice() { return startingPrice; }
    public void setStartingPrice(double startingPrice) { this.startingPrice = startingPrice; }

    public double getCurrentHighestBid() { return currentHighestBid; }
    public void setCurrentHighestBid(double currentHighestBid) { this.currentHighestBid = currentHighestBid; }

    public Integer getHighestBidderId() { return highestBidderId; }
    public void setHighestBidderId(Integer highestBidderId) { this.highestBidderId = highestBidderId; }

    public String getHighestBidderUsername() { return highestBidderUsername; }
    public void setHighestBidderUsername(String highestBidderUsername) { this.highestBidderUsername = highestBidderUsername; }

    public Integer getSellerUserId() { return sellerUserId; }
    public void setSellerUserId(Integer sellerUserId) { this.sellerUserId = sellerUserId; }

    public String getSellerUsername() { return sellerUsername; }
    public void setSellerUsername(String sellerUsername) { this.sellerUsername = sellerUsername; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // Formatted getters for JSP display
    public String getFormattedEndTime() {
        return FormatUtil.formatDateTime(endTime);
    }
    
    public String getFormattedCreatedAt() {
        return FormatUtil.formatDateTime(createdAt);
    }
    
    public String getFormattedStartingPrice() {
        return FormatUtil.formatCurrency(startingPrice);
    }
    
    public String getFormattedCurrentHighestBid() {
        return FormatUtil.formatCurrency(currentHighestBid);
    }

    @Override
    public String toString() {
        return "AuctionItem{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", currentHighestBid=" + currentHighestBid +
                ", highestBidderUsername='" + highestBidderUsername + '\'' +
                ", sellerUsername='" + sellerUsername + '\'' +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                '}';
    }
}