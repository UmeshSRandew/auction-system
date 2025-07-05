package lk.jiat.ee.auction.model;

import lk.jiat.ee.auction.util.FormatUtil;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Bid implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int itemId;
    private int userId;
    private String username; // For display
    private double bidAmount;
    private LocalDateTime bidTime;

    public Bid() {
        this.bidTime = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public double getBidAmount() { return bidAmount; }
    public void setBidAmount(double bidAmount) { this.bidAmount = bidAmount; }

    public LocalDateTime getBidTime() { return bidTime; }
    public void setBidTime(LocalDateTime bidTime) { this.bidTime = bidTime; }
    
    // Formatted getters for JSP display
    public String getFormattedBidTime() {
        return FormatUtil.formatDateTime(bidTime);
    }
    
    public String getFormattedBidAmount() {
        return FormatUtil.formatCurrency(bidAmount);
    }

    @Override
    public String toString() {
        return "Bid{" +
                "id=" + id +
                ", itemId=" + itemId +
                ", userId=" + userId +
                ", bidAmount=" + bidAmount +
                ", bidTime=" + bidTime +
                '}';
    }
}