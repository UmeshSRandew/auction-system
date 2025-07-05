package lk.jiat.ee.auction.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.ee.auction.model.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@WebFilter("/*") // Apply to all requests, then check path
public class AuthenticationFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());

    // List of paths that require authentication
    private final List<String> protectedPaths = Arrays.asList(
            "/createAuction",
            "/myAuctions",
            "/myBids",
            "/placeBid" // POST requests to placeBid also need auth, handled in servlet but good to have here too.
    );

    // List of paths that should be accessible without full authentication (e.g., static resources, login, register)
    private final List<String> publicPaths = Arrays.asList(
            "/login",
            "/register",
            "/listAuctions", // Viewing auctions is public
            "/viewAuction",  // Viewing a specific auction is public
            "/index.jsp",
            "/" // Root path
    );

    private final List<String> staticResourcePrefixes = Arrays.asList(
            "/css/",
            "/js/",
            "/images/"
    );


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false); // Don't create a new session if one doesn't exist

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        User loggedInUser = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

        // Allow access to static resources
        for (String prefix : staticResourcePrefixes) {
            if (path.startsWith(prefix)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // Allow access to public paths
        for (String publicPath : publicPaths) {
            if (path.equals(publicPath) || (path.equals("") && publicPath.equals("/"))) { // Also handle root path "" as "/"
                chain.doFilter(request, response);
                return;
            }
        }

        // Check if the path is protected
        boolean isProtectedPath = false;
        for (String protectedPath : protectedPaths) {
            if (path.startsWith(protectedPath)) { // Use startsWith to catch /placeBid?itemId=...
                isProtectedPath = true;
                break;
            }
        }

        if (isProtectedPath) {
            if (loggedInUser == null) {
                LOGGER.warning("AuthenticationFilter: User not logged in. Redirecting to login for path: " + path);
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?redirectMsg=Please login to access this page.");
                return; // Stop further processing
            }
            // User is logged in, allow access
        }
        // For non-protected paths or if user is logged in for protected paths
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("AuthenticationFilter initialized.");
    }

    @Override
    public void destroy() {
        // Cleanup code, if any
    }
}