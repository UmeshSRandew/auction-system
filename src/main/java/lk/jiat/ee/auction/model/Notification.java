package lk.jiat.ee.auction.model;

import java.time.LocalDateTime;

/**
 * Model class representing a notification for a user.
 * These are stored in the database for retrieval even when users are offline.
 */
public class Notification {
    private Integer id;
    private Integer userId;
    private String message;
    private String eventType;
    private Integer itemId;
    private LocalDateTime createdAt;
    private boolean isRead;
    
    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }
    
    public Notification(Integer userId, String message, String eventType, Integer itemId) {
        this();
        this.userId = userId;
        this.message = message;
        this.eventType = eventType;
        this.itemId = itemId;
    }
    
    // Getters and setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public Integer getItemId() {
        return itemId;
    }
    
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
    
    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", eventType='" + eventType + '\'' +
                ", itemId=" + itemId +
                ", createdAt=" + createdAt +
                ", isRead=" + isRead +
                '}';
    }
} 