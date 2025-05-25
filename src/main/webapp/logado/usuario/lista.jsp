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
    <header class="page-header">
        <div class="container">
            <div class="header-content">
                <div class="site-title">
                    <h1><fmt:message key="users.list" /></h1>
                </div>
                <div class="login-logout-nav">
                    <span><fmt:message key="welcome.message" /> ${sessionScope.usuarioLogado.nome}!</span>
                    <a href="${pageContext.request.contextPath}/logout.jsp" class="button button-logout">
                        <fmt:message key="exit.link" />
                    </a>
                    &nbsp;&nbsp;&nbsp;

                        <%-- LINK PARA O DASHBOARD DO ADMIN (APENAS SE FOR ADMIN) --%>
                    <c:if test="${sessionScope.usuarioLogado.tipo == 'ADMIN'}">
                        <a href="${pageContext.request.contextPath}/admin/" class="button">
                            <fmt:message key="admin.dashboard.title" /> <%-- Usando a chave do dashboard --%>
                        </a>
                        &nbsp;&nbsp;&nbsp;
                        <a href="${pageContext.request.contextPath}/usuarios/cadastro" class="button">
                            <fmt:message key="users.create" />
                        </a>
                    </c:if>
                </div>
            </div>
        </div>
    </header>

    <main class="page-content">
        <div class="container">
                <%-- Removido o div align="center" e h2 ao redor dos links pois o header.page-header já gerencia isso --%>
                <%-- Conteúdo da tabela --%>
            <table border="1">
                <tr>
                    <th><fmt:message key="user.ID" /></th>
                    <th><fmt:message key="user.login" /></th>
                    <th><fmt:message key="user.name" /></th>
                    <th><fmt:message key="user.role" /></th>
                    <c:if test="${sessionScope.usuarioLogado.tipo == 'ADMIN'}">
                        <th><fmt:message key="actions.link" /></th>
                    </c:if>
                </tr>

                <c:forEach var="usuario" items="${listaUsuarios}">
                    <tr>
                        <td><c:out value="${usuario.id}" /></td>
                        <td><c:out value="${usuario.login}" /></td>
                        <td><c:out value="${usuario.nome}" /></td>
                        <td><c:out value="${usuario.tipo}" /></td>

                        <c:if test="${sessionScope.usuarioLogado.tipo == 'ADMIN'}">
                            <td>
                                <a href="${pageContext.request.contextPath}/usuarios/edicao?id=${usuario.id}" class="button button-small">
                                    <fmt:message key="users.update" />
                                </a>
                                &nbsp;&nbsp;&nbsp;&nbsp;
                                <a href="${pageContext.request.contextPath}/usuarios/remocao?id=${usuario.id}"
                                   onclick="return confirm('<fmt:message key="confirm.link" />');" class="button button-small button-delete">
                                    <fmt:message key="users.delete" />
                                </a>
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>
            </table>
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