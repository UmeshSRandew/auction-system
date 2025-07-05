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
import java.util.List;

@WebServlet("/myBids")
public class MyBidsServlet extends HttpServlet {
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

        // Get items the user has placed bids on
        List<AuctionItem> bidOnItems = auctionManager.getItemsUserBidOn(loggedInUser.getId());

        // Optionally, get all bids made by the user
        // List<Bid> myBids = auctionManager.getBidsByUserId(loggedInUser.getId());
        // req.setAttribute("myBids", myBids);

        req.setAttribute("bidOnItems", bidOnItems);
        req.setAttribute("currentUserId", loggedInUser.getId()); // To check if current user is highest bidder in JSP
        req.getRequestDispatcher("/WEB-INF/jsps/my_bids.jsp").forward(req, resp);
    }
}