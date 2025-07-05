<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<html>
<head>
    <title>Edit Auction</title>
</head>
<body>
<jsp:include page="/WEB-INF/jsps/header.jsp" />
<div class="container">
    <h1>Edit Your Auction</h1>
    
    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="error-message">
            <c:out value="${sessionScope.errorMessage}"/>
            <c:remove var="errorMessage" scope="session" />
        </div>
    </c:if>
    
    <form action="${pageContext.request.contextPath}/editAuction" method="POST">
        <input type="hidden" name="itemId" value="${auctionItem.id}">
        
        <div class="form-group">
            <label for="itemName">Item Name:</label>
            <input type="text" id="itemName" name="itemName" value="<c:out value='${auctionItem.itemName}'/>" required>
        </div>
        
        <div class="form-group">
            <label for="description">Description:</label>
            <textarea id="description" name="description" rows="4" required><c:out value='${auctionItem.description}'/></textarea>
        </div>
        
        <div class="form-group">
            <label for="startingPrice">Starting Price:</label>
            <span id="startingPrice"><c:out value="${auctionItem.formattedStartingPrice}"/></span>
            <small>(Cannot be changed)</small>
        </div>
        
        <div class="form-group">
            <label for="currentBid">Current Highest Bid:</label>
            <span id="currentBid"><c:out value="${auctionItem.formattedCurrentHighestBid}"/></span>
        </div>
        
        <div class="form-group">
            <label for="endTime">Auction End Time:</label>
            <span id="endTime"><c:out value="${auctionItem.formattedEndTime}"/></span>
            <small>(Cannot be changed)</small>
        </div>
        
        <div class="form-group">
            <label for="status">Status:</label>
            <span id="status" style="font-weight:bold; color:${auctionItem.status == 'OPEN' ? 'green' : 'red'};"><c:out value="${auctionItem.status}"/></span>
        </div>
        
        <button type="submit" class="btn">Update Auction</button>
        <a href="${pageContext.request.contextPath}/viewAuction?itemId=${auctionItem.id}" class="btn btn-secondary">Cancel</a>
    </form>
</div>
<jsp:include page="/WEB-INF/jsps/footer.jsp" />
</body>
</html> 