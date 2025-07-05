<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<html>
<head>
    <title>Current Auctions</title>
</head>
<body>
<jsp:include page="/WEB-INF/jsps/header.jsp" />
<div class="container">
    <h1>Current Open Auctions</h1>
    <c:choose>
        <c:when test="${not empty auctionItems}">
            <table>
                <thead>
                <tr>
                    <th>Item Name</th>
                    <th>Description</th>
                    <th>Current Bid</th>
                    <th>Highest Bidder</th>
                    <th>Seller</th>
                    <th>Ends At</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${auctionItems}">
                    <tr>
                        <td><c:out value="${item.itemName}"/></td>
                        <td><c:out value="${item.description}"/></td>
                        <td><c:out value="${item.formattedCurrentHighestBid}"/></td>
                        <td><c:out value="${empty item.highestBidderUsername ? 'No bids yet' : item.highestBidderUsername}"/></td>
                        <td><c:out value="${item.sellerUsername}"/></td>
                        <td><c:out value="${item.formattedEndTime}"/></td>
                        <td><a href="${pageContext.request.contextPath}/viewAuction?itemId=${item.id}" class="btn btn-secondary">View/Bid</a></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <p>No open auctions at the moment. Why not <a href="${pageContext.request.contextPath}/createAuction">create one</a>?</p>
        </c:otherwise>
    </c:choose>
</div>
<jsp:include page="/WEB-INF/jsps/footer.jsp" />
</body>
</html>