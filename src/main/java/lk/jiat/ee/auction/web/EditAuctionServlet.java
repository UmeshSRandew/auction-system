package lk.jiat.ee.auction.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.ee.auction.ejb.AuctionManagerRemote;
import lk.jiat.ee.auction.model.AuctionItem;
import lk.jiat.ee.auction.model.User;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/editAuction")
public class EditAuctionServlet extends HttpServlet {
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

        String itemIdStr = req.getParameter("itemId");
        if (itemIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/myAuctions");
            return;
        }

        try {
            int itemId = Integer.parseInt(itemIdStr);
            Optional<AuctionItem> itemOpt = auctionManager.getAuctionItemById(itemId);
            
            if (itemOpt.isEmpty()) {
                session.setAttribute("errorMessage", "Auction item not found.");
                resp.sendRedirect(req.getContextPath() + "/myAuctions");
                return;
            }
            
            AuctionItem item = itemOpt.get();
            
            // Check if the auction belongs to the logged-in user
            if (item.getSellerUserId() != loggedInUser.getId()) {
                session.setAttribute("errorMessage", "You can only edit your own auctions.");
                resp.sendRedirect(req.getContextPath() + "/myAuctions");
                return;
            }
            
            // Check if the auction is closed
            if ("CLOSED".equals(item.getStatus())) {
                session.setAttribute("errorMessage", "Closed auctions cannot be edited.");
                resp.sendRedirect(req.getContextPath() + "/myAuctions");
                return;
            }
            
            req.setAttribute("auctionItem", item);
            req.getRequestDispatcher("/WEB-INF/jsps/edit_auction.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid item ID format.");
            resp.sendRedirect(req.getContextPath() + "/myAuctions");
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

        String itemIdStr = req.getParameter("itemId");
        String itemName = req.getParameter("itemName");
        String description = req.getParameter("description");
        
        if (itemIdStr == null || itemName == null || description == null) {
            session.setAttribute("errorMessage", "Missing required parameters.");
            resp.sendRedirect(req.getContextPath() + "/myAuctions");
            return;
        }

        try {
            int itemId = Integer.parseInt(itemIdStr);
            
            boolean success = auctionManager.updateAuctionItem(itemId, itemName, description, loggedInUser.getId());
            
            if (success) {
                session.setAttribute("successMessage", "Auction updated successfully.");
                resp.sendRedirect(req.getContextPath() + "/viewAuction?itemId=" + itemId);
            } else {
                session.setAttribute("errorMessage", "Failed to update auction. It might be closed or you don't have permission.");
                resp.sendRedirect(req.getContextPath() + "/myAuctions");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid item ID format.");
            resp.sendRedirect(req.getContextPath() + "/myAuctions");
        }
    }
} 