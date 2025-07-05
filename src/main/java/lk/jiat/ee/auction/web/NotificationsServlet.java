package lk.jiat.ee.auction.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.ee.auction.db.DatabaseUtil;
import lk.jiat.ee.auction.model.Notification;
import lk.jiat.ee.auction.model.User;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Servlet for handling user notifications
 */
@WebServlet(name = "NotificationsServlet", urlPatterns = {"/notifications", "/mark-read"})
public class NotificationsServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(NotificationsServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("loggedInUser");
        int userId = user.getId();
        String path = request.getServletPath();
        
        if ("/notifications".equals(path)) {
            // Get notifications for the user (including already read ones)
            List<Notification> notifications = DatabaseUtil.getNotificationsForUser(userId, true);
            request.setAttribute("notifications", notifications);
            request.getRequestDispatcher("/WEB-INF/jsps/notifications.jsp").forward(request, response);
        } else if ("/mark-read".equals(path)) {
            // Mark notification as read
            String notificationId = request.getParameter("id");
            String all = request.getParameter("all");
            
            if ("true".equals(all)) {
                // Mark all notifications as read
                DatabaseUtil.markAllNotificationsAsRead(userId);
                LOGGER.info("Marked all notifications as read for user " + userId);
            } else if (notificationId != null && !notificationId.isEmpty()) {
                // Mark specific notification as read
                try {
                    int id = Integer.parseInt(notificationId);
                    DatabaseUtil.markNotificationAsRead(id);
                    LOGGER.info("Marked notification " + id + " as read");
                } catch (NumberFormatException e) {
                    LOGGER.warning("Invalid notification ID: " + notificationId);
                }
            }
            
            // Redirect back to notifications page
            response.sendRedirect(request.getContextPath() + "/notifications");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Handle POST requests (if needed)
        doGet(request, response);
    }
} 