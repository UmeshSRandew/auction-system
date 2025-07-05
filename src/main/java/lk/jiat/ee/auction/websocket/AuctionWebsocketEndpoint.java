package lk.jiat.ee.auction.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lk.jiat.ee.auction.jms.AuctionEvent;
import lk.jiat.ee.auction.util.FormatUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WebSocket endpoint for real-time auction event notifications.
 * Clients connect with their user ID to receive notifications relevant to them.
 */
@ServerEndpoint("/ws/auctions/{userId}")
@ApplicationScoped
public class AuctionWebsocketEndpoint {
    private static final Logger LOGGER = Logger.getLogger(AuctionWebsocketEndpoint.class.getName());
    
    // Static collections to ensure they are shared across instances
    private static final Map<String, Set<Session>> userSessions = new ConcurrentHashMap<>();
    private static final Set<Session> allSessions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        LOGGER.info("WebSocket connection opened for user: " + userId);
        
        // Add to global sessions
        allSessions.add(session);
        
        // Add to user-specific sessions
        userSessions.computeIfAbsent(userId, k -> Collections.newSetFromMap(new ConcurrentHashMap<>()))
                    .add(session);
        
        // Send a welcome message
        try {
            session.getBasicRemote().sendText(createJsonMessage("CONNECT", "Connected to auction notifications"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending welcome message", e);
        }
    }
    
    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        LOGGER.info("WebSocket connection closed for user: " + userId);
        
        // Remove from global sessions
        allSessions.remove(session);
        
        // Remove from user-specific sessions
        Set<Session> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }
    }
    
    @OnError
    public void onError(Session session, @PathParam("userId") String userId, Throwable throwable) {
        LOGGER.log(Level.WARNING, "WebSocket error for user " + userId, throwable);
        
        // Close the session on error
        try {
            session.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error closing WebSocket session", e);
        }
    }
    
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") String userId) {
        LOGGER.info("Received message from user " + userId + ": " + message);
        
        // Echo the message back (for testing/debugging)
        try {
            session.getBasicRemote().sendText(createJsonMessage("ECHO", message));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending echo message", e);
        }
    }
    
    /**
     * Broadcast an auction event to all connected clients
     */
    public void broadcastEvent(AuctionEvent event) {
        LOGGER.info("Broadcasting event to " + allSessions.size() + " WebSocket sessions: " + event.getMessage());
        String json = createJsonEvent(event);
        
        // Broadcast to all sessions
        for (Session session : allSessions) {
            try {
                session.getBasicRemote().sendText(json);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error sending event to session", e);
            }
        }
    }
    
    /**
     * Static method to broadcast an event - to be used by other components
     */
    public static void broadcast(AuctionEvent event) {
        String json = createJsonEvent(event);
        LOGGER.info("Static broadcast to " + allSessions.size() + " sessions: " + event.getMessage());
        
        for (Session session : allSessions) {
            try {
                session.getBasicRemote().sendText(json);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error in static broadcast", e);
            }
        }
    }
    
    /**
     * Send an auction event to a specific user's sessions
     */
    public void sendEventToUser(AuctionEvent event, String userId) {
        String json = createJsonEvent(event);
        Set<Session> sessions = userSessions.get(userId);
        
        if (sessions != null) {
            for (Session session : sessions) {
                try {
                    session.getBasicRemote().sendText(json);
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error sending event to user " + userId, e);
                }
            }
        }
    }
    
    /**
     * Create a JSON string for an auction event
     */
    private static String createJsonEvent(AuctionEvent event) {
        // Simple JSON format for the event
        try {
            return String.format(
                "{\"type\":\"%s\",\"timestamp\":\"%s\",\"itemId\":%d,\"itemName\":\"%s\",\"message\":\"%s\"}",
                event.getType(),
                FormatUtil.formatDateTime(event.getTimestamp()),
                event.getItemId(),
                event.getItemName() != null ? event.getItemName().replace("\"", "\\\"") : "",
                event.getMessage() != null ? event.getMessage().replace("\"", "\\\"") : ""
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating JSON event", e);
            return "{\"type\":\"ERROR\",\"message\":\"Error creating event JSON\"}";
        }
    }
    
    /**
     * Create a simple JSON message
     */
    private String createJsonMessage(String type, String message) {
        return String.format(
            "{\"type\":\"%s\",\"message\":\"%s\"}",
            type,
            message != null ? message.replace("\"", "\\\"") : ""
        );
    }
} 