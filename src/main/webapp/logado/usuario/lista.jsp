<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<fmt:bundle basename="message">

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><fmt:message key="users.list" /> | <fmt:message key="page.title" /></title>
        <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    </head>

    <body>
    <div align="center">
        <h1><fmt:message key="users.list" /></h1>
        <h2>
                <%-- Links de navegação --%>
            <a href="${pageContext.request.contextPath}/logout.jsp">
                <fmt:message key="exit.link" />
            </a>
            &nbsp;&nbsp;&nbsp;

                <%-- Link para cadastro de novo usuário, visível apenas para ADMIN --%>
            <c:if test="${sessionScope.usuarioLogado.tipo == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/usuarios/cadastro">
                    <fmt:message key="users.create" />
                </a>
            </c:if>
        </h2>
        <br/>
    </div>

    <div align="center">
        <table border="1">
            <tr>
                <th><fmt:message key="user.ID" /></th>
                <th><fmt:message key="user.login" /></th>
                <th><fmt:message key="user.name" /></th>
                <th><fmt:message key="user.role" /></th>
                    <%-- A senha não deve ser exibida na lista por segurança --%>
                    <%-- <th><fmt:message key="user.password" /></th> --%>

                    <%-- Coluna de ações visível apenas para ADMINs --%>
                <c:if test="${sessionScope.usuarioLogado.tipo == 'ADMIN'}">
                    <th><fmt:message key="actions.link" /></th>
                </c:if>
            </tr>

            <c:forEach var="usuario" items="${listaUsuarios}">
                <tr>
                    <td><c:out value="${usuario.id}" /></td>
                    <td><c:out value="${usuario.login}" /></td> <%-- Exibe o login do usuário --%>
                    <td><c:out value="${usuario.nome}" /></td>
                    <td><c:out value="${usuario.tipo}" /></td> <%-- Exibe o nome da Role (ADMIN, TESTER, GUEST) --%>
                        <%-- Não exibir a senha --%>
                        <%-- <td><c:out value="${usuario.senha}" /></td> --%>

                        <%-- Ações de edição e remoção, visíveis apenas para ADMINs --%>
                    <c:if test="${sessionScope.usuarioLogado.tipo == 'ADMIN'}">
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
                    </c:if>
                </tr>
            </c:forEach>
        </table>
    </div>
    </body>
</fmt:bundle>
</html>