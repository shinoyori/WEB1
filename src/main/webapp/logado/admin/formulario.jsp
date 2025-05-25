<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %> <%-- Updated URI --%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %> <%-- Updated URI --%>
<!DOCTYPE html>
<html>
<fmt:bundle basename="message">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>
            <c:choose>
                <c:when test="${usuario != null}"><fmt:message key="users.update" /></c:when>
                <c:otherwise><fmt:message key="users.create" /></c:otherwise>
            </c:choose>
            | <fmt:message key="page.title" />
        </title>
        <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
    <header class="page-header">
        <div class="container">
            <div class="header-content">
                <div class="site-title">
                    <c:choose>
                        <c:when test="${usuario != null}"><fmt:message key="users.update" /></c:when>
                        <c:otherwise><fmt:message key="users.create" /></c:otherwise>
                    </c:choose>
                </div>
                <div class="login-logout-nav">
                    <span><fmt:message key="welcome.message" /> ${sessionScope.usuarioLogado.nome}!</span>
                    <a href="${pageContext.request.contextPath}/usuarios/lista" class="button"><fmt:message key="users.list" /></a>
                    <a href="${pageContext.request.contextPath}/logout.jsp" class="button button-logout"><fmt:message key="exit.link" /></a>
                </div>
            </div>
        </div>
    </header>

    <main class="page-content">
        <div class="container">
            <div class="form-container card">
                <h2>
                    <c:choose>
                        <c:when test="${usuario != null}"><fmt:message key="users.update" /></c:when>
                        <c:otherwise><fmt:message key="users.create" /></c:otherwise>
                    </c:choose>
                </h2>

                <c:if test="${not empty requestScope.mensagens and requestScope.mensagens.existeErros}">
                    <div id="erro">
                        <ul>
                            <c:forEach items="${requestScope.mensagens.erros}" var="erro">
                                <li><c:out value="${erro}" /></li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>

                <c:choose>
                    <c:when test="${usuario != null}">
                        <form action="${pageContext.request.contextPath}/usuarios/atualizacao" method="post">
                            <%@include file="campos.jsp"%>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <form action="${pageContext.request.contextPath}/usuarios/insercao" method="post">
                            <%@include file="campos.jsp"%>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>

    <footer class="page-footer">
        <div class="container">
            <p>&copy; ${sessionScope.year != null ? sessionScope.year : 2024} Your Company Name.</p>
        </div>
    </footer>
    </body>
</fmt:bundle>
</html>