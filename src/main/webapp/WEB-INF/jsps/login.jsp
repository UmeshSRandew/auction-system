<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Login - Auction System</title>
</head>
<body>
<jsp:include page="/WEB-INF/jsps/header.jsp" />
<div class="container">
    <h2>Login to Your Account</h2>
    <c:if test="${not empty param.logout}">
        <div class="messages success-message">You have been logged out successfully.</div>
    </c:if>
    <c:if test="${not empty param.redirectMsg}">
        <div class="messages error-message"><c:out value="${param.redirectMsg}"/></div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <div class="form-group">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" value="<c:out value='${param.username}'/>" required>
        </div>
        <div class="form-group">
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>
        </div>
        <button type="submit" class="btn">Login</button>
    </form>
    <p style="margin-top:15px;">Don't have an account? <a href="${pageContext.request.contextPath}/register">Register here</a>.</p>
</div>
<jsp:include page="/WEB-INF/jsps/footer.jsp" />
</body>
</html>