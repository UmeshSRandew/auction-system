<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<html>
<head>
  <title>My Listed Auctions</title>
</head>
<body>
<jsp:include page="/WEB-INF/jsps/header.jsp" />
<div class="container">
  <h1>My Listed Auctions</h1>
  
  <c:if test="${not empty sessionScope.errorMessage}">
    <div class="error-message">
      <c:out value="${sessionScope.errorMessage}"/>
      <c:remove var="errorMessage" scope="session" />
    </div>
  </c:if>
  
  <c:if test="${not empty sessionScope.successMessage}">
    <div class="success-message">
      <c:out value="${sessionScope.successMessage}"/>
      <c:remove var="successMessage" scope="session" />
    </div>
  </c:if>
  
  <c:choose>
    <c:when test="${not empty myAuctionItems}">
      <table>
        <thead>
        <tr>
          <th>Item Name</th>
          <th>Status</th>
          <th>Current Bid</th>
          <th>Highest Bidder</th>
          <th>Ends At</th>
          <th>Created At</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${myAuctionItems}">
          <tr>
            <td><c:out value="${item.itemName}"/></td>
            <td><span style="font-weight:bold; color:${item.status == 'OPEN' ? 'green' : (item.status == 'CLOSED' ? 'red' : 'orange')};"><c:out value="${item.status}"/></span></td>
            <td><c:out value="${item.formattedCurrentHighestBid}"/></td>
            <td><c:out value="${empty item.highestBidderUsername ? (item.highestBidderId == null ? 'No bids yet' : item.highestBidderUsername) : item.highestBidderUsername}"/></td>
            <td><c:out value="${item.formattedEndTime}"/></td>
            <td><c:out value="${item.formattedCreatedAt}"/></td>
            <td>
              <a href="${pageContext.request.contextPath}/viewAuction?itemId=${item.id}" class="btn btn-secondary">View Details</a>
              <c:if test="${item.status != 'CLOSED'}">
                <a href="${pageContext.request.contextPath}/editAuction?itemId=${item.id}" class="btn btn-primary">Edit</a>
              </c:if>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <p>You have not listed any auctions yet. <a href="${pageContext.request.contextPath}/createAuction">Create one now!</a></p>
    </c:otherwise>
  </c:choose>
</div>
<jsp:include page="/WEB-INF/jsps/footer.jsp" />
</body>
</html>