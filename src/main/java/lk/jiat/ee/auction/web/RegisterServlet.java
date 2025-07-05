package lk.jiat.ee.auction.web;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.jiat.ee.auction.ejb.UserBeanRemote;
import lk.jiat.ee.auction.model.User;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @EJB
    private UserBeanRemote userBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsps/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        if (username == null || username.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.isEmpty()) {
            req.setAttribute("errorMessage", "All fields are required.");
            req.getRequestDispatcher("/WEB-INF/jsps/register.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(confirmPassword)) {
            req.setAttribute("errorMessage", "Passwords do not match.");
            req.getRequestDispatcher("/WEB-INF/jsps/register.jsp").forward(req, resp);
            return;
        }

        // Check if username or email already exists
        if (userBean.getUserByUsername(username).isPresent()) {
            req.setAttribute("errorMessage", "Username already taken.");
            req.getRequestDispatcher("/WEB-INF/jsps/register.jsp").forward(req, resp);
            return;
        }
        // Add similar check for email if needed via UserBean

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(password); // UserBean will hash it

        boolean success = userBean.registerUser(newUser);

        if (success) {
            req.getSession().setAttribute("successMessage", "Registration successful! Please login.");
            resp.sendRedirect(req.getContextPath() + "/login");
        } else {
            req.setAttribute("errorMessage", "Registration failed. Username or email might already exist, or an internal error occurred.");
            req.getRequestDispatcher("/WEB-INF/jsps/register.jsp").forward(req, resp);
        }
    }
}