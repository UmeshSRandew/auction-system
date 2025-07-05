package lk.jiat.ee.auction.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.ee.auction.ejb.UserBeanRemote;
import lk.jiat.ee.auction.model.User;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @EJB
    private UserBeanRemote userBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsps/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            req.setAttribute("errorMessage", "Username and password are required.");
            req.getRequestDispatcher("/WEB-INF/jsps/login.jsp").forward(req, resp);
            return;
        }

        Optional<User> userOpt = userBean.loginUser(username, password);

        if (userOpt.isPresent()) {
            HttpSession session = req.getSession();
            session.setAttribute("loggedInUser", userOpt.get()); // Store full user object
            session.setAttribute("successMessage", "Login successful!");
            resp.sendRedirect(req.getContextPath() + "/listAuctions"); // Redirect to auctions list or dashboard
        } else {
            req.setAttribute("errorMessage", "Invalid username or password.");
            req.getRequestDispatcher("/WEB-INF/jsps/login.jsp").forward(req, resp);
        }
    }
}