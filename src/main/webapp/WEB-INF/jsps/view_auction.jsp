<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<html>
<head>
    <c:choose>
        <c:when test="${not empty auctionItem}">
            <title>Auction: <c:out value="${auctionItem.itemName}"/></title>
        </c:when>
        <c:otherwise>
            <title>Auction Not Found</title>
        </c:otherwise>
    </c:choose>
</head>
<body>
<jsp:include page="/WEB-INF/jsps/header.jsp" />
<div class="container">
    <c:choose>
        <c:when test="${not empty auctionItem}">
            <h1><c:out value="${auctionItem.itemName}"/></h1>
            <p><strong>Description:</strong> <c:out value="${auctionItem.description}"/></p>
            <p><strong>Seller:</strong> <c:out value="${auctionItem.sellerUsername}"/>
                <c:if test="${not empty sessionScope.loggedInUser && sessionScope.loggedInUser.id == auctionItem.sellerUserId}">
                    (You)
                </c:if>
            </p>
            <p><strong>Starting Price:</strong> <c:out value="${auctionItem.formattedStartingPrice}"/></p>
            <p><strong>Current Highest Bid:</strong> <c:out value="${auctionItem.formattedCurrentHighestBid}"/></p>
            <p><strong>Highest Bidder:</strong>
                <c:out value="${empty auctionItem.highestBidderUsername ? 'No bids yet' : auctionItem.highestBidderUsername}"/>
                <c:if test="${not empty sessionScope.loggedInUser && sessionScope.loggedInUser.id == auctionItem.highestBidderId}">
                    (You)
                </c:if>
            </p>
            <p>
                <strong>Auction Ends:</strong> <c:out value="${auctionItem.formattedEndTime}"/>
            </p>
            <p><strong>Status:</strong> <span style="font-weight:bold; color:${auctionItem.status == 'OPEN' ? 'green' : 'red'};"><c:out value="${auctionItem.status}"/></span></p>

            <c:if test="${not empty bidStatusMessage}">
                <div class="messages ${bidSuccess ? 'success-message' : 'error-message'}">
                    <c:out value="${bidStatusMessage}"/>
                </div>
            </c:if>
            
            <c:if test="${not empty sessionScope.successMessage}">
                <div class="success-message">
                    <c:out value="${sessionScope.successMessage}"/>
                    <c:remove var="successMessage" scope="session" />
                </div>
            </c:if>

            <div class="action-buttons" style="margin: 20px 0;">
                <a href="${pageContext.request.contextPath}/viewBids?itemId=${auctionItem.id}" class="btn btn-primary">View All Bids</a>
                <c:if test="${not empty sessionScope.loggedInUser && sessionScope.loggedInUser.id == auctionItem.sellerUserId && auctionItem.status != 'CLOSED'}">
                    <a href="${pageContext.request.contextPath}/editAuction?itemId=${auctionItem.id}" class="btn btn-primary">Edit Auction</a>
                </c:if>
            </div>

            <c:if test="${auctionItem.status == 'OPEN'}">
                <c:choose>
                    <c:when test="${not empty sessionScope.loggedInUser}">
                        <c:if test="${sessionScope.loggedInUser.id != auctionItem.sellerUserId}">
                            <form action="${pageContext.request.contextPath}/placeBid" method="POST" style="margin-top: 20px;">
                                <h3>Place Your Bid</h3>
                                <input type="hidden" name="itemId" value="${auctionItem.id}">
                                <div class="form-group">
                                    <label for="bidAmount">Your Bid (LKR) (must be > <fmt:formatNumber value="${auctionItem.currentHighestBid}" type="number" minFractionDigits="2" maxFractionDigits="2"/>):</label>
                                    <input type="number" id="bidAmount" name="bidAmount" step="0.01"
                                           min="<c:out value='${auctionItem.currentHighestBid + 0.01}'/>" required
                                           placeholder="Enter amount > ${auctionItem.currentHighestBid}">
                                </div>
                                <button type="submit" class="btn">Place Bid</button>
                            </form>
                        </c:if>
                        <c:if test="${sessionScope.loggedInUser.id == auctionItem.sellerUserId}">
                            <p style="color: #005f73; margin-top:15px;"><em>You cannot bid on your own auction.</em></p>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <p style="margin-top: 20px;"><a href="${pageContext.request.contextPath}/login?redirectMsg=Please login to place a bid." class="btn">Login to Bid</a></p>
                    </c:otherwise>
                </c:choose>
            </c:if>
            <c:if test="${auctionItem.status == 'CLOSED'}">
                <h3 style="color: #005f73; margin-top:20px;">This auction has ended.</h3>
                <c:if test="${not empty auctionItem.highestBidderUsername}">
                    <p><strong>Winning Bidder:</strong> <c:out value="${auctionItem.highestBidderUsername}"/></p>
                    <p><strong>Final Price:</strong> <c:out value="${auctionItem.formattedCurrentHighestBid}"/></p>
                </c:if>
                <c:if test="${empty auctionItem.highestBidderUsername && auctionItem.highestBidderId == null}">
                    <p>This auction ended with no bids.</p>
                </c:if>
            </c:if>

        </c:when>
        <c:otherwise>
            <p class="error-message">Auction item not found or you do not have permission to view it.</p>
        </c:otherwise>
    </c:choose>
    <p style="margin-top: 30px;"><a href="${pageContext.request.contextPath}/listAuctions" class="btn btn-secondary">&laquo; Back to Auctions List</a></p>
</div>
<jsp:include page="/WEB-INF/jsps/footer.jsp" />
</body>
</html>