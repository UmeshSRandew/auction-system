package lk.jiat.ee.auction.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet; // Use annotation
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.jiat.ee.auction.ejb.AuctionManagerRemote;
import lk.jiat.ee.auction.model.AuctionItem;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/viewAuction") // Use annotation
public class ViewAuctionServlet extends HttpServlet {
    @EJB
    private AuctionManagerRemote auctionManager;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String itemIdStr = request.getParameter("itemId");
        if (itemIdStr != null) {
            try {
                int itemId = Integer.parseInt(itemIdStr);
                Optional<AuctionItem> itemOpt = auctionManager.getAuctionItemById(itemId);
                if (itemOpt.isPresent()) {
                    request.setAttribute("auctionItem", itemOpt.get());
                } else {
                    request.setAttribute("errorMessage", "Auction item not found.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid item ID format.");
            }
        } else {
            request.setAttribute("errorMessage", "Item ID is required.");
        }

        if (request.getSession().getAttribute("bidStatusMessage") != null) {
            request.setAttribute("bidStatusMessage", request.getSession().getAttribute("bidStatusMessage"));
            request.setAttribute("bidSuccess", request.getSession().getAttribute("bidSuccess"));
            request.getSession().removeAttribute("bidStatusMessage");
            request.getSession().removeAttribute("bidSuccess");
        }
        request.getRequestDispatcher("/WEB-INF/jsps/view_auction.jsp").forward(request, response);
    }
}