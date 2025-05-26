<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<fmt:bundle basename="message">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><fmt:message key="admin.dashboard.title" /> | <fmt:message key="page.title" /></title>
        <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
    <header class="page-header">
        <div class="container">
            <div class="header-content">
                <div class="site-title">
                    <fmt:message key="admin.dashboard.title" />
                </div>
                <div class="login-logout-nav">
                    <span><fmt:message key="welcome.message" /> ${sessionScope.usuarioLogado.nome}!</span>
                    <a href="${pageContext.request.contextPath}/logout.jsp" class="button button-logout"><fmt:message key="exit.link" /></a>
                    <a href="${pageContext.request.contextPath}/home.jsp" class="button"><fmt:message key="return.to.home" /></a>
                </div>
            </div>
        </div>
    </header>

    <main class="page-content admin-dashboard">
        <div class="container">
            <h1><fmt:message key="admin.dashboard.heading" /></h1>

            <nav class="admin-menu card">
                <h2><fmt:message key="admin.menu.heading" /></h2>
                <ul>
                    <li>
                        <a href="${pageContext.request.contextPath}/usuarios/lista" class="admin-menu-button">
                            <fmt:message key="users.list" />
                        </a>
                    </li>

                    <li>
                        <a href="${pageContext.request.contextPath}/projetos/lista" class="admin-menu-button">
                            <fmt:message key="projects.list" />
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/projetos/cadastro" class="admin-menu-button">
                            <fmt:message key="projects.create" />
                        </a>
                    </li>

                        <%-- Inside the <ul> under admin.menu.heading --%>
                    <li>
                        <a href="${pageContext.request.contextPath}/admin/estrategias/cadastro" class="admin-menu-button">
                            <fmt:message key="strategies.create" />
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/estrategias/lista" class="admin-menu-button"> <%-- Public list, but useful for admin to navigate --%>
                            <fmt:message key="strategies.list" />
                        </a>
                    </li>
                        <%-- Example for other links if needed --%>
                        <%--
                        <li>
                            <a href="#" class="admin-menu-button">
                                Other Admin Link
                            </a>
                        </li>
                        --%>
                </ul>
            </nav>
        </div>
    </main>
    </body>
</fmt:bundle>
</html>