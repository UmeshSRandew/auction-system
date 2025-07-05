<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<html>
<head>
    <title>Edit Bid</title>
</head>
<body>
<jsp:include page="/WEB-INF/jsps/header.jsp" />
<div class="container">
    <h1>Edit Your Bid</h1>
    
    <c:if test="${not empty sessionScope.errorMessage}">
        <div class="error-message">
            <c:out value="${sessionScope.errorMessage}"/>
            <c:remove var="errorMessage" scope="session" />
        </div>
    </c:if>
    
    <form action="${pageContext.request.contextPath}/editBid" method="POST">
        <input type="hidden" name="bidId" value="${bid.id}">
        <input type="hidden" name="itemId" value="${bid.itemId}">
        
        <div class="form-group">
            <label for="currentAmount">Current Bid Amount:</label>
            <span id="currentAmount"><c:out value="${bid.formattedBidAmount}"/></span>
        </div>
        
        <div class="form-group">
            <label for="bidAmount">New Bid Amount (LKR):</label>
            <input type="number" id="bidAmount" name="bidAmount" step="0.01" 
                   min="${bid.bidAmount}" value="${bid.bidAmount}" required>
            <small>Your new bid must be equal to or higher than your current bid.</small>
        </div>
        
        <div class="form-group">
            <label for="bidTime">Bid Time:</label>
            <span id="bidTime"><c:out value="${bid.formattedBidTime}"/></span>
            <small>(Will be updated to current time when you save)</small>
        </div>
        
        <button type="submit" class="btn">Update Bid</button>
        <a href="${pageContext.request.contextPath}/viewBids?itemId=${bid.itemId}" class="btn btn-secondary">Cancel</a>
    </form>
</div>
<jsp:include page="/WEB-INF/jsps/footer.jsp" />
</body>
</html> 