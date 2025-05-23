<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<fmt:bundle basename="message">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><fmt:message key="admin.dashboard.title" /> | <fmt:message key="page.title" /></title>
        <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
    <div align="center">
        <h1><fmt:message key="admin.dashboard.heading" /></h1>
        <p><fmt:message key="welcome.message" /> ${sessionScope.usuarioLogado.nome}</p>

        <h2><fmt:message key="admin.menu.heading" /></h2>
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/usuarios/lista">
                    <fmt:message key="users.list" />
                </a>
            </li>
                <%-- Adicionar aqui  links de funcionalidades futuras --%>
            <li>
                <a href="${pageContext.request.contextPath}/logout.jsp">
                    <fmt:message key="exit.link" />
                </a>
            </li>
        </ul>
    </div>
    </body>
</fmt:bundle>
</html>