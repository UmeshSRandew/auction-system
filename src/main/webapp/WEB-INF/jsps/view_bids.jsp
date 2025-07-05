<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<html>
<head>
    <title>Bids for ${auctionItem.itemName}</title>
</head>
<body>
<jsp:include page="/WEB-INF/jsps/header.jsp" />
<div class="container">
    <h1>Bids for ${auctionItem.itemName}</h1>
    
    <div class="auction-details">
        <p><strong>Item:</strong> <c:out value="${auctionItem.itemName}"/></p>
        <p><strong>Description:</strong> <c:out value="${auctionItem.description}"/></p>
        <p><strong>Current Highest Bid:</strong> <c:out value="${auctionItem.formattedCurrentHighestBid}"/></p>
        <p><strong>Status:</strong> <span style="font-weight:bold; color:${auctionItem.status == 'OPEN' ? 'green' : 'red'};"><c:out value="${auctionItem.status}"/></span></p>
        <p><strong>Ends At:</strong> <c:out value="${auctionItem.formattedEndTime}"/></p>
    </div>
    
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="success-message">
            <c:out value="${sessionScope.successMessage}"/>
            <c:remove var="successMessage" scope="session" />
        </div>
    </c:if>
    
    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="error-message">
            <c:out value="${sessionScope.errorMessage}"/>
            <c:remove var="errorMessage" scope="session" />
        </div>
    </c:if>
    
    <h2>All Bids</h2>
    <c:choose>
        <c:when test="${not empty bids}">
            <table>
                <thead>
                <tr>
                    <th>Bidder</th>
                    <th>Amount</th>
                    <th>Time</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="bid" items="${bids}">
                    <tr>
                        <td><c:out value="${bid.username}"/></td>
                        <td><c:out value="${bid.formattedBidAmount}"/></td>
                        <td><c:out value="${bid.formattedBidTime}"/></td>
                        <td>
                            <c:if test="${bid.userId == currentUserId && auctionItem.status == 'OPEN'}">
                                <a href="${pageContext.request.contextPath}/editBid?bidId=${bid.id}" class="btn btn-primary">Edit</a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <p>No bids have been placed on this item yet.</p>
        </c:otherwise>
    </c:choose>
    
    <p style="margin-top: 20px;">
        <a href="${pageContext.request.contextPath}/viewAuction?itemId=${auctionItem.id}" class="btn btn-secondary">View Auction</a>
        <a href="${pageContext.request.contextPath}/myBids" class="btn btn-secondary">Back to My Bids</a>
    </p>
</div>
<jsp:include page="/WEB-INF/jsps/footer.jsp" />
</body>
</html> 