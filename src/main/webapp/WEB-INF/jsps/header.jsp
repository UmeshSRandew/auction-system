<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<style>
    body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f7f6; color: #333; }
    .navbar { background-color: #005f73; padding: 15px 30px; color: white; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
    .navbar a { color: white; text-decoration: none; margin: 0 15px; font-size: 1.1em; }
    .navbar a:hover { text-decoration: underline; color: #94d2bd; }
    .navbar .brand { font-size: 1.5em; font-weight: bold; }
    .navbar .user-info { font-size: 0.9em; }
    .container { max-width: 1200px; margin: 20px auto; padding: 20px; background-color: #fff; border-radius: 8px; box-shadow: 0 0 15px rgba(0,0,0,0.05); }
    .messages { padding: 15px; margin-bottom: 20px; border-radius: 5px; }
    .success-message { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
    .error-message { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
    h1, h2 { color: #003459; }
    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
    th, td { padding: 12px; border: 1px solid #ddd; text-align: left; }
    th { background-color: #0077b6; color: white; }
    tr:nth-child(even) { background-color: #e9f5f9; }
    .form-group { margin-bottom: 15px; }
    .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
    .form-group input[type="text"],
    .form-group input[type="password"],
    .form-group input[type="email"],
    .form-group input[type="number"],
    .form-group input[type="datetime-local"],
    .form-group textarea {
        width: calc(100% - 22px);
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 4px;
        box-sizing: border-box;
    }
    .form-group textarea { resize: vertical; min-height: 80px; }
    .btn {
        background-color: #0077b6; color: white; padding: 10px 15px;
        border: none; border-radius: 4px; cursor: pointer; font-size: 1em;
        text-decoration: none; display: inline-block; text-align: center;
    }
    .btn:hover { background-color: #005f73; }
    .btn-danger { background-color: #d9534f; }
    .btn-danger:hover { background-color: #c9302c; }
    .btn-secondary { background-color: #6c757d; }
    .btn-secondary:hover { background-color: #545b62; }
</style>

<div class="navbar">
    <div>
        <a href="${pageContext.request.contextPath}/" class="brand">AuctionSys</a>
        <a href="${pageContext.request.contextPath}/listAuctions">View Auctions</a>
        <c:if test="${not empty sessionScope.loggedInUser}">
            <a href="${pageContext.request.contextPath}/createAuction">Create Auction</a>
            <a href="${pageContext.request.contextPath}/myAuctions">My Auctions</a>
            <a href="${pageContext.request.contextPath}/myBids">My Bids</a>
            <a href="${pageContext.request.contextPath}/notifications">Notifications</a>
        </c:if>
    </div>
    <div>
        <c:choose>
            <c:when test="${not empty sessionScope.loggedInUser}">
                <span class="user-info">Welcome, <c:out value="${sessionScope.loggedInUser.username}"/>!</span>
                <a href="${pageContext.request.contextPath}/logout">Logout</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login">Login</a>
                <a href="${pageContext.request.contextPath}/register">Register</a>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<div class="container">
    <%-- Display session messages --%>
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="messages success-message">
            <c:out value="${sessionScope.successMessage}"/>
        </div>
        <% session.removeAttribute("successMessage"); %>
    </c:if>
    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="messages error-message">
            <c:out value="${sessionScope.errorMessage}"/>
        </div>
        <% session.removeAttribute("errorMessage"); %>
    </c:if>
    <%-- Display request messages (usually for form validation errors on POST-redirect-GET or forward) --%>
    <c:if test="${not empty requestScope.successMessage}">
        <div class="messages success-message">
            <c:out value="${requestScope.successMessage}"/>
        </div>
    </c:if>
    <c:if test="${not empty requestScope.errorMessage}">
        <div class="messages error-message">
            <c:out value="${requestScope.errorMessage}"/>
        </div>
    </c:if>
</div>