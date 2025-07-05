package lk.jiat.ee.auction.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Simple servlet to handle the home page and root requests
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"", "/", "/home"})
public class HomeServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(HomeServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("HomeServlet: Processing GET request");
        
        // Forward to the home page JSP
        request.getRequestDispatcher("/WEB-INF/jsps/home.jsp").forward(request, response);
    }
} 