<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<fmt:bundle basename="message">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><fmt:message key="home.page.title" /> | <fmt:message key="page.title" /></title>
        <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
    <header class="page-header">
        <div class="container">
            <div class="login-logout-nav">
                <c:choose>
                    <c:when test="${sessionScope.usuarioLogado != null}">
                        <span><fmt:message key="welcome.message" /> ${sessionScope.usuarioLogado.nome}!</span>
                        <a href="${pageContext.request.contextPath}/logout.jsp" class="button button-logout"><fmt:message key="exit.link" /></a>
                        <c:if test="${sessionScope.usuarioLogado.tipo == 'ADMIN'}">
                            <a href="${pageContext.request.contextPath}/admin/" class="button"><fmt:message key="admin.dashboard.title" /></a>
                        </c:if>
                        <c:if test="${sessionScope.usuarioLogado.tipo == 'TESTER'}">
                            <a href="${pageContext.request.contextPath}/usuarios/" class="button"><fmt:message key="users.list" /></a>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login.jsp" class="button button-login"><fmt:message key="user.login" /></a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </header>

    <main class="page-content">
        <div class="container">
            <h1><fmt:message key="home.page.heading" /></h1>
            <p><fmt:message key="home.page.content" /></p>

            <nav class="public-info-nav">
                <h3><fmt:message key="public.info.link1"/></h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/estrategias/lista"><fmt:message key="strategies.list" /></a></li>
                </ul>
            </nav>
        </div>
    </main>
    </body>
</fmt:bundle>
</html>
