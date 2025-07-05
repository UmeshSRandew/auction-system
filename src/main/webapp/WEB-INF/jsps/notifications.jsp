<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="header.jsp">
    <jsp:param name="title" value="Your Notifications" />
</jsp:include>

<div class="container mt-4">
    <div class="row">
        <div class="col-md-12">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2>Your Notifications</h2>
                <div>
                    <a href="${pageContext.request.contextPath}/mark-read?all=true" class="btn btn-outline-secondary">
                        Mark All as Read
                    </a>
                </div>
            </div>
            
            <c:if test="${empty notifications}">
                <div class="alert alert-info">
                    You don't have any notifications yet.
                </div>
            </c:if>
            
            <c:if test="${not empty notifications}">
                <div class="list-group">
                    <c:forEach var="notification" items="${notifications}">
                        <div class="list-group-item list-group-item-action ${notification.read ? '' : 'list-group-item-primary'}">
                            <div class="d-flex w-100 justify-content-between">
                                <h5 class="mb-1">${notification.eventType.replace('_', ' ')}</h5>
                                <small class="text-muted">
                                    <fmt:parseDate value="${notification.createdAt}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate" type="both" />
                                    <fmt:formatDate value="${parsedDate}" pattern="MMM d, yyyy h:mm a" />
                                </small>
                            </div>
                            <p class="mb-1">${notification.message}</p>
                            <small class="text-muted">
                                <c:if test="${not empty notification.itemId}">
                                    <a href="${pageContext.request.contextPath}/view-auction?id=${notification.itemId}">
                                        View Auction
                                    </a>
                                </c:if>
                                <c:if test="${not notification.read}">
                                    | <a href="${pageContext.request.contextPath}/mark-read?id=${notification.id}">
                                        Mark as Read
                                    </a>
                                </c:if>
                            </small>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" /> 