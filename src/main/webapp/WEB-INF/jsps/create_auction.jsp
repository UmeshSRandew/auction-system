<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
  <title>Create New Auction</title>
</head>
<body>
<jsp:include page="/WEB-INF/jsps/header.jsp" />
<div class="container">
  <h2>List a New Item for Auction</h2>

  <form action="${pageContext.request.contextPath}/createAuction" method="post">
    <div class="form-group">
      <label for="itemName">Item Name:</label>
      <input type="text" id="itemName" name="itemName" value="<c:out value='${param.itemName}'/>" required>
    </div>
    <div class="form-group">
      <label for="description">Description:</label>
      <textarea id="description" name="description" rows="4" required><c:out value='${param.description}'/></textarea>
    </div>
    <div class="form-group">
      <label for="startingPrice">Starting Price (LKR):</label>
      <input type="number" id="startingPrice" name="startingPrice" step="0.01" min="0.01" value="<c:out value='${param.startingPrice}'/>" required>
    </div>
    <div class="form-group">
      <label for="endTime">Auction End Time (YYYY-MM-DDTHH:MM):</label>
      <input type="datetime-local" id="endTime" name="endTime" value="<c:out value='${param.endTime}'/>" required
             min="${requestScope.minDateTime}"> <%-- Servlet can set minDateTime --%>
      <small>Example: 2025-12-31T23:59. Ensure the auction runs for a reasonable duration (at least 1 minute after current time).</small>
    </div>
    <button type="submit" class="btn">Create Auction</button>
  </form>
</div>
<jsp:include page="/WEB-INF/jsps/footer.jsp" />
</body>
</html>