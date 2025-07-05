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
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@WebServlet("/createAuction")
public class CreateAuctionServlet extends HttpServlet {
    @EJB
    private AuctionManagerRemote auctionManager;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Authentication check will be handled by the filter
        req.getRequestDispatcher("/WEB-INF/jsps/create_auction.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User seller = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

        if (seller == null) {
            // This should ideally be caught by the AuthenticationFilter
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String itemName = req.getParameter("itemName");
        String description = req.getParameter("description");
        String startingPriceStr = req.getParameter("startingPrice");
        String endTimeStr = req.getParameter("endTime"); // Expected format: YYYY-MM-DDTHH:mm

        if (itemName == null || itemName.trim().isEmpty() ||
                startingPriceStr == null || startingPriceStr.trim().isEmpty() ||
                endTimeStr == null || endTimeStr.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Item name, starting price, and end time are required.");
            req.getRequestDispatcher("/WEB-INF/jsps/create_auction.jsp").forward(req, resp);
            return;
        }

        try {
            double startingPrice = Double.parseDouble(startingPriceStr);
            LocalDateTime endTime = LocalDateTime.parse(endTimeStr); // Parses ISO_LOCAL_DATE_TIME

            if (endTime.isBefore(LocalDateTime.now().plusMinutes(1))) { // Auction must end at least 1 minute in future
                req.setAttribute("errorMessage", "End time must be at least 1 minute in the future.");
                req.getRequestDispatcher("/WEB-INF/jsps/create_auction.jsp").forward(req, resp);
                return;
            }

            AuctionItem newItem = new AuctionItem();
            newItem.setItemName(itemName);
            newItem.setDescription(description);
            newItem.setStartingPrice(startingPrice);
            newItem.setEndTime(endTime);
            newItem.setSellerUserId(seller.getId());
            // createdAt and initial status ("OPEN") are set in AuctionItem constructor/DatabaseUtil

            boolean success = auctionManager.createAuctionItem(newItem);

            if (success) {
                req.getSession().setAttribute("successMessage", "Auction created successfully!");
                resp.sendRedirect(req.getContextPath() + "/myAuctions");
            } else {
                req.setAttribute("errorMessage", "Failed to create auction. Please try again.");
                req.getRequestDispatcher("/WEB-INF/jsps/create_auction.jsp").forward(req, resp);
            }

        } catch (NumberFormatException e) {
            req.setAttribute("errorMessage", "Invalid starting price format. Please enter a valid number.");
            req.getRequestDispatcher("/WEB-INF/jsps/create_auction.jsp").forward(req, resp);
        } catch (DateTimeParseException e) {
            req.setAttribute("errorMessage", "Invalid end time format. Please use YYYY-MM-DDTHH:MM (e.g., 2025-12-31T23:59).");
            req.getRequestDispatcher("/WEB-INF/jsps/create_auction.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMessage", "An unexpected error occurred during auction creation.");
            req.getRequestDispatcher("/WEB-INF/jsps/create_auction.jsp").forward(req, resp);
        }
    }
}