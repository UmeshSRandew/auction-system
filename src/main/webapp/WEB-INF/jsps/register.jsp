<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Register - Auction System</title>
</head>
<body>
<jsp:include page="/WEB-INF/jsps/header.jsp" />
<div class="container">
    <h2>Create Your Account</h2>

    <form action="${pageContext.request.contextPath}/register" method="post">
        <div class="form-group">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" value="<c:out value='${param.username}'/>" required>
        </div>
        <div class="form-group">
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" value="<c:out value='${param.email}'/>" required>
        </div>
        <div class="form-group">
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div class="form-group">
            <label for="confirmPassword">Confirm Password:</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required>
        </div>
        <button type="submit" class="btn">Register</button>
    </form>
    <p style="margin-top:15px;">Already have an account? <a href="${pageContext.request.contextPath}/login">Login here</a>.</p>
</div>
<jsp:include page="/WEB-INF/jsps/footer.jsp" />
</body>
</html>