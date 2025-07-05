<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="header.jsp">
    <jsp:param name="title" value="Welcome - Auction System Advanced" />
</jsp:include>

<div class="container mt-5">
    <div class="jumbotron">
        <h1 class="display-4">Welcome to the Auction System!</h1>
        <p class="lead">Your one-stop platform for buying and selling unique items through exciting auctions.</p>
        <hr class="my-4">
        <p>Browse active auctions, place bids, or create your own auction listings.</p>
        <a class="btn btn-primary btn-lg" href="${pageContext.request.contextPath}/listAuctions" role="button">Browse Auctions</a>
        
        <c:if test="${empty sessionScope.loggedInUser}">
            <a class="btn btn-outline-secondary btn-lg ml-2" href="${pageContext.request.contextPath}/login" role="button">Login</a>
            <a class="btn btn-success btn-lg ml-2" href="${pageContext.request.contextPath}/register" role="button">Register</a>
        </c:if>
        <c:if test="${not empty sessionScope.loggedInUser}">
            <a class="btn btn-success btn-lg ml-2" href="${pageContext.request.contextPath}/create-auction" role="button">Create Auction</a>
        </c:if>
    </div>
    
    <div class="row mt-5">
        <div class="col-md-4">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Browse Auctions</h5>
                    <p class="card-text">Discover a wide variety of items available for bidding.</p>
                    <a href="${pageContext.request.contextPath}/listAuctions" class="btn btn-primary">Browse Now</a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Place Bids</h5>
                    <p class="card-text">Participate in auctions and win items at competitive prices.</p>
                    <a href="${pageContext.request.contextPath}/listAuctions" class="btn btn-primary">Find Auctions</a>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Sell Items</h5>
                    <p class="card-text">Create your own auctions and sell items to the highest bidder.</p>
                    <a href="${pageContext.request.contextPath}/create-auction" class="btn btn-primary">Start Selling</a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" /> 