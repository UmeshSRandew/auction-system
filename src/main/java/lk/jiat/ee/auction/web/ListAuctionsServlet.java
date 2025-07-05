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
import java.util.List;

@WebServlet("/listAuctions") // Use annotation
public class ListAuctionsServlet extends HttpServlet {
    @EJB
    private AuctionManagerRemote auctionManager;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<AuctionItem> items = auctionManager.getAllOpenAuctionItems();
        request.setAttribute("auctionItems", items);
        request.getRequestDispatcher("/WEB-INF/jsps/list_auctions.jsp").forward(request, response);
    }
}