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
import lk.jiat.ee.auction.model.Bid;
import lk.jiat.ee.auction.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/viewBids")
public class ViewBidsServlet extends HttpServlet {
    @EJB
    private AuctionManagerRemote auctionManager;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

        if (loggedInUser == null) {
            // This should ideally be caught by the AuthenticationFilter
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String itemIdStr = req.getParameter("itemId");
        if (itemIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/myBids");
            return;
        }

        try {
            int itemId = Integer.parseInt(itemIdStr);
            Optional<AuctionItem> itemOpt = auctionManager.getAuctionItemById(itemId);
            
            if (itemOpt.isEmpty()) {
                session.setAttribute("errorMessage", "Auction item not found.");
                resp.sendRedirect(req.getContextPath() + "/myBids");
                return;
            }
            
            AuctionItem item = itemOpt.get();
            List<Bid> bids = auctionManager.getBidsByItemId(itemId);
            
            req.setAttribute("auctionItem", item);
            req.setAttribute("bids", bids);
            req.setAttribute("currentUserId", loggedInUser.getId());
            
            req.getRequestDispatcher("/WEB-INF/jsps/view_bids.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid item ID format.");
            resp.sendRedirect(req.getContextPath() + "/myBids");
        }
    }
} 