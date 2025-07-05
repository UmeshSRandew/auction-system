<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>


<html>
<head>
  <title>My Bids</title>
  <fmt:setLocale value="en_LK"/>
</head>
<body>
<jsp:include page="/WEB-INF/jsps/header.jsp" />
<div class="container">
  <h1>Items I've Bid On</h1>
  
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
    <c:when test="${not empty bidOnItems}">
      <table>
        <thead>
        <tr>
          <th>Item Name</th>
          <th>Seller</th>
          <th>Current Highest Bid</th>
          <th>Current Highest Bidder</th>
          <th>Status</th>
          <th>Ends At</th>
          <th>My Bid Status</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${bidOnItems}">
          <tr>
            <td><c:out value="${item.itemName}"/></td>
            <td><c:out value="${item.sellerUsername}"/></td>
            <td><c:out value="${item.formattedCurrentHighestBid}"/></td>
            <td><c:out value="${empty item.highestBidderUsername ? (item.highestBidderId == null ? 'No bids yet' : item.highestBidderUsername) : item.highestBidderUsername}"/></td>
            <td><span style="font-weight:bold; color:${item.status == 'OPEN' ? 'green' : 'red'};"><c:out value="${item.status}"/></span></td>
            <td><c:out value="${item.formattedEndTime}"/></td>
            <td>
              <c:choose>
                <c:when test="${item.status == 'CLOSED' && not empty item.highestBidderId && item.highestBidderId == currentUserId}">
                  <span style="color: green; font-weight: bold;">YOU WON!</span>
                </c:when>
                <c:when test="${item.status == 'CLOSED' && not empty item.highestBidderId && item.highestBidderId != currentUserId}">
                  <span style="color: red;">Lost</span>
                </c:when>
                <c:when test="${item.status == 'CLOSED' && empty item.highestBidderId}">
                  <span>Ended with no bids</span>
                </c:when>
                <c:when test="${item.status == 'OPEN' && not empty item.highestBidderId && item.highestBidderId == currentUserId}">
                  <span style="color: orange; font-weight: bold;">Currently Highest Bidder</span>
                </c:when>
                <c:when test="${item.status == 'OPEN' && not empty item.highestBidderId && item.highestBidderId != currentUserId}">
                  <span style="color: blue;">Outbid</span>
                </c:when>
                <c:otherwise>
                  -
                </c:otherwise>
              </c:choose>
            </td>
            <td>
              <a href="${pageContext.request.contextPath}/viewAuction?itemId=${item.id}" class="btn btn-secondary">View Item</a>
              <a href="${pageContext.request.contextPath}/viewBids?itemId=${item.id}" class="btn btn-primary">View/Edit Bids</a>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <p>You have not placed any bids yet. <a href="${pageContext.request.contextPath}/listAuctions">Find auctions to bid on!</a></p>
    </c:otherwise>
  </c:choose>
</div>
<jsp:include page="/WEB-INF/jsps/footer.jsp" />
</body>
</html>