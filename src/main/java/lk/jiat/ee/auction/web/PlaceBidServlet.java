package lk.jiat.ee.auction.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet; // Use annotation
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.ee.auction.ejb.AuctionManagerRemote;
import lk.jiat.ee.auction.model.User; // Import User model

import java.io.IOException;

@WebServlet("/placeBid") // Use annotation
public class PlaceBidServlet extends HttpServlet {
    @EJB
    private AuctionManagerRemote auctionManager;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

        if (loggedInUser == null) {
            session.setAttribute("errorMessage", "You must be logged in to place a bid.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String itemIdStr = request.getParameter("itemId");
        String bidAmountStr = request.getParameter("bidAmount");
        String redirectUrl = request.getContextPath() + "/viewAuction?itemId=" + (itemIdStr != null ? itemIdStr : "");

        if (itemIdStr == null || bidAmountStr == null) {
            session.setAttribute("bidStatusMessage", "Missing item ID or bid amount.");
            session.setAttribute("bidSuccess", false);
            response.sendRedirect(redirectUrl);
            return;
        }

        try {
            int itemId = Integer.parseInt(itemIdStr);
            double bidAmount = Double.parseDouble(bidAmountStr);
            int userId = loggedInUser.getId(); // Get user ID from logged-in user

            boolean success = auctionManager.placeBid(itemId, userId, bidAmount);

            if (success) {
                session.setAttribute("bidStatusMessage", "Bid placed successfully!");
                session.setAttribute("bidSuccess", true);
            } else {
                session.setAttribute("bidStatusMessage", "Failed to place bid. It might be too low, auction ended, or an error occurred.");
                session.setAttribute("bidSuccess", false);
            }
            response.sendRedirect(redirectUrl);

        } catch (NumberFormatException e) {
            session.setAttribute("bidStatusMessage", "Invalid item ID or bid amount format.");
            session.setAttribute("bidSuccess", false);
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("bidStatusMessage", "An unexpected error occurred while placing your bid.");
            session.setAttribute("bidSuccess", false);
            response.sendRedirect(redirectUrl);
        }
    }
}

