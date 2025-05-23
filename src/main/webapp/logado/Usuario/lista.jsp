<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<fmt:bundle basename="message">

    <head>
        <title><fmt:message key="page.title" /></title>
    </head>

    <body>
    <div align="center">
        <h1><fmt:message key="users.welcome" /></h1>
        <h2><fmt:message key="users.list" /></h2>
        <br/>
    </div>

    <div align="center">
        <table border="1">
            <tr>
                <th><fmt:message key="user.ID" /></th>
                <th><fmt:message key="user.login" /></th>
                <th><fmt:message key="user.password" /></th>
                <th><fmt:message key="user.name" /></th>
                <th><fmt:message key="user.role" /></th>
                <th><fmt:message key="actions.link" /></th>
            </tr>

            <c:forEach var="usuario" items="${listaUsuarios}">
                <tr>
                    <td><c:out value="${usuario.id}" /></td>
                    <td><c:out value="${usuario.email}" /></td>
                    <td><c:out value="${usuario.senha}" /></td>
                    <td><c:out value="${usuario.nome}" /></td>
                    <td><c:out value="${usuario.tipo}" /></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/usuarios/edicao?id=${usuario.id}">
                            <fmt:message key="users.update" />
                        </a>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <a href="${pageContext.request.contextPath}/usuarios/remocao?id=${usuario.id}"
                           onclick="return confirm('<fmt:message key="confirm.link" />');">
                            <fmt:message key="users.delete" />
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
    </body>
</fmt:bundle>
</html>
