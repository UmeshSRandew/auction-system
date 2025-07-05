package lk.jiat.ee.auction.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.ee.auction.ejb.AuctionManagerRemote;
import lk.jiat.ee.auction.model.Bid;
import lk.jiat.ee.auction.model.User;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/editBid")
public class EditBidServlet extends HttpServlet {
    @EJB
    private AuctionManagerRemote auctionManager;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

        if (loggedInUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String bidIdStr = req.getParameter("bidId");
        if (bidIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/myBids");
            return;
        }

        try {
            int bidId = Integer.parseInt(bidIdStr);
            Optional<Bid> bidOpt = auctionManager.getBidById(bidId);
            
            if (bidOpt.isEmpty()) {
                session.setAttribute("errorMessage", "Bid not found.");
                resp.sendRedirect(req.getContextPath() + "/myBids");
                return;
            }
            
            Bid bid = bidOpt.get();
            
            // Check if the bid belongs to the logged-in user
            if (bid.getUserId() != loggedInUser.getId()) {
                session.setAttribute("errorMessage", "You can only edit your own bids.");
                resp.sendRedirect(req.getContextPath() + "/myBids");
                return;
            }
            
            req.setAttribute("bid", bid);
            req.getRequestDispatcher("/WEB-INF/jsps/edit_bid.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid bid ID format.");
            resp.sendRedirect(req.getContextPath() + "/myBids");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

        if (loggedInUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String bidIdStr = req.getParameter("bidId");
        String bidAmountStr = req.getParameter("bidAmount");
        String itemIdStr = req.getParameter("itemId");
        
        if (bidIdStr == null || bidAmountStr == null || itemIdStr == null) {
            session.setAttribute("errorMessage", "Missing required parameters.");
            resp.sendRedirect(req.getContextPath() + "/myBids");
            return;
        }

        try {
            int bidId = Integer.parseInt(bidIdStr);
            double bidAmount = Double.parseDouble(bidAmountStr);
            int itemId = Integer.parseInt(itemIdStr);
            
            boolean success = auctionManager.editBid(bidId, bidAmount, loggedInUser.getId());
            
            if (success) {
                session.setAttribute("successMessage", "Bid updated successfully.");
            } else {
                session.setAttribute("errorMessage", "Failed to update bid. It might be too low, auction ended, or you don't have permission.");
            }
            
            resp.sendRedirect(req.getContextPath() + "/viewBids?itemId=" + itemId);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid bid ID or amount format.");
            resp.sendRedirect(req.getContextPath() + "/myBids");
        }
    }
} 