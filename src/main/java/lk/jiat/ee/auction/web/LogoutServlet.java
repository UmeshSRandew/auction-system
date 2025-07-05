package lk.jiat.ee.auction.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false); // Don't create if it doesn't exist
        if (session != null) {
            session.removeAttribute("loggedInUser");
            session.invalidate(); // Invalidate the session
        }
        // Redirect to login page with a message or just home
        resp.sendRedirect(req.getContextPath() + "/login?logout=true");
    }
}