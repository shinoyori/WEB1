<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html>
<fmt:bundle basename="message">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><fmt:message key="home.page.title" /> | <fmt:message key="page.title" /></title>
        <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
    <div align="center">
        <h1><fmt:message key="home.page.heading" /></h1>

            <%-- Opção de Login/Logout no canto --%>
        <div style="position: absolute; top: 10px; right: 10px;">
            <c:choose>
                <c:when test="${sessionScope.usuarioLogado != null}">
                    <p><fmt:message key="welcome.message" /> ${sessionScope.usuarioLogado.nome}!</p>
                    <a href="${pageContext.request.contextPath}/logout.jsp"><fmt:message key="exit.link" /></a>
                    <c:if test="${sessionScope.usuarioLogado.tipo == 'ADMIN'}">
                        &nbsp;|&nbsp; <a href="${pageContext.request.contextPath}/admin/"><fmt:message key="admin.dashboard.title" /></a>
                    </c:if>
                    <c:if test="${sessionScope.usuarioLogado.tipo == 'TESTER'}">
                        &nbsp;|&nbsp; <a href="${pageContext.request.contextPath}/usuarios/"><fmt:message key="users.list" /></a>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login.jsp"><fmt:message key="user.login" /></a>
                </c:otherwise>
            </c:choose>
        </div>

        <p><fmt:message key="home.page.content" /></p>

            <%-- Adicione aqui links para funcionalidades visíveis a GUEST, como informações públicas --%>
        <ul>
            <li><a href="#"><fmt:message key="public.info.link1" /></a></li>
            <li><a href="#"><fmt:message key="public.info.link2" /></a></li>
        </ul>
    </div>
    </body>
</fmt:bundle>
</html>