<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<style>
    .footer {
        text-align: center;
        padding: 20px;
        margin-top: 30px;
        background-color: #003459;
        color: #e0f2f1;
        font-size: 0.9em;
    }
</style>
<div class="footer">
    <p>&copy; ${java.time.Year.now()} Advanced Auction System by Umesh. All rights reserved.</p>
</div>

<!-- Notifications area -->
<div id="notification-area" style="position: fixed; bottom: 20px; right: 20px; z-index: 9999;">
    <!-- Notifications will be dynamically added here -->
</div>

<!-- WebSocket for real-time notifications if user is logged in -->
<c:if test="${not empty sessionScope.loggedInUser}">
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Connect to WebSocket with user ID
            const userId = '${sessionScope.loggedInUser.id}';
            const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            const wsUrl = `${wsProtocol}//${window.location.host}${pageContext.request.contextPath}/ws/auctions/${userId}`;
            
            let socket = null;
            let reconnectAttempts = 0;
            const maxReconnectAttempts = 5;
            
            function connectWebSocket() {
                try {
                    socket = new WebSocket(wsUrl);
                    
                    socket.onopen = function(event) {
                        console.log('WebSocket connection opened');
                        reconnectAttempts = 0;
                    };
                    
                    socket.onmessage = function(event) {
                        // Parse the message
                        try {
                            const data = JSON.parse(event.data);
                            console.log('WebSocket message received:', data);
                            
                            // Handle based on message type
                            if (data.type === 'CONNECT') {
                                // Connection acknowledgment - no need to show
                            } else if (data.type === 'BID_PLACED' || data.type === 'BID_UPDATED' || 
                                      data.type === 'AUCTION_CREATED' || data.type === 'AUCTION_UPDATED' || 
                                      data.type === 'AUCTION_CLOSED') {
                                showNotification(data.message, data.type);
                            }
                        } catch (e) {
                            console.error('Error parsing WebSocket message:', e);
                        }
                    };
                    
                    socket.onclose = function(event) {
                        console.log('WebSocket connection closed');
                        
                        // Try to reconnect with increasing delay
                        if (reconnectAttempts < maxReconnectAttempts) {
                            reconnectAttempts++;
                            const delay = Math.min(30000, 1000 * Math.pow(2, reconnectAttempts));
                            console.log(`Attempting to reconnect in ${delay}ms...`);
                            setTimeout(connectWebSocket, delay);
                        }
                    };
                    
                    socket.onerror = function(error) {
                        console.error('WebSocket error:', error);
                    };
                } catch (e) {
                    console.error('Error creating WebSocket:', e);
                }
            }
            
            // Function to show a notification
            function showNotification(message, type) {
                const notificationArea = document.getElementById('notification-area');
                const notification = document.createElement('div');
                
                // Style based on type
                let bgColor = '#4caf50'; // Default green
                
                switch (type) {
                    case 'BID_PLACED':
                    case 'BID_UPDATED':
                        bgColor = '#2196F3'; // Blue
                        break;
                    case 'AUCTION_CREATED':
                    case 'AUCTION_UPDATED':
                        bgColor = '#4CAF50'; // Green
                        break;
                    case 'AUCTION_CLOSED':
                        bgColor = '#FF5722'; // Orange
                        break;
                }
                
                // Style the notification
                notification.style.backgroundColor = bgColor;
                notification.style.color = 'white';
                notification.style.padding = '10px 15px';
                notification.style.marginBottom = '10px';
                notification.style.borderRadius = '4px';
                notification.style.boxShadow = '0 2px 5px rgba(0,0,0,0.2)';
                notification.style.minWidth = '250px';
                notification.style.maxWidth = '350px';
                notification.style.opacity = '0';
                notification.style.transition = 'opacity 0.3s ease-in-out';
                
                // Add the message
                notification.textContent = message;
                
                // Add to notification area
                notificationArea.appendChild(notification);
                
                // Fade in
                setTimeout(() => {
                    notification.style.opacity = '1';
                }, 10);
                
                // Remove after 5 seconds
                setTimeout(() => {
                    notification.style.opacity = '0';
                    setTimeout(() => {
                        notificationArea.removeChild(notification);
                    }, 300);
                }, 5000);
            }
            
            // Start the WebSocket connection
            connectWebSocket();
            
            // Clean up on page unload
            window.addEventListener('beforeunload', function() {
                if (socket && socket.readyState === WebSocket.OPEN) {
                    socket.close();
                }
            });
        });
    </script>
</c:if>

</body>
</html>