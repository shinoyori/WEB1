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
                <c:when test="${projeto != null}"><fmt:message key="projects.update" /></c:when>
                <c:otherwise><fmt:message key="projects.create" /></c:otherwise>
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
                        <c:when test="${projeto != null}"><fmt:message key="projects.update" /></c:when>
                        <c:otherwise><fmt:message key="projects.create" /></c:otherwise>
                    </c:choose>
                </div>
                <div class="login-logout-nav">
                    <span><fmt:message key="welcome.message" /> ${sessionScope.usuarioLogado.nome}!</span>
                    <a href="${pageContext.request.contextPath}/projetos/lista" class="button"><fmt:message key="projects.list" /></a>
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
                        <c:when test="${projeto != null}"><fmt:message key="projects.update" /></c:when>
                        <c:otherwise><fmt:message key="projects.create" /></c:otherwise>
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
                    <c:when test="${projeto != null}">
                        <form action="${pageContext.request.contextPath}/projetos/atualizacao" method="post">
                            <input type="hidden" name="id" value="<c:out value='${projeto.id}' />" />
                                <%-- Project Name --%>
                            <div class="form-group">
                                <label for="nome"><fmt:message key="project.name" />:</label>
                                <input type="text" id="nome" name="nome" value="<c:out value='${projeto.nome}' />" required />
                            </div>
                                <%-- Project Description --%>
                            <div class="form-group">
                                <label for="descricao"><fmt:message key="project.description" />:</label>
                                <textarea id="descricao" name="descricao" rows="5" required><c:out value='${projeto.descricao}' /></textarea>
                            </div>
                                <%-- Assign Users (Multi-select) --%>
                            <div class="form-group">
                                <label for="usuariosSelecionados"><fmt:message key="project.assign.users" />:</label>
                                <select id="usuariosSelecionados" name="usuariosSelecionados" multiple class="form-control" size="5">
                                    <c:forEach var="user" items="${requestScope.todosUsuarios}">
                                        <option value="${user.id}"
                                                <c:forEach var="projUser" items="${projeto.usuarios}">
                                                    <c:if test="${projUser.id == user.id}">selected</c:if>
                                                </c:forEach>
                                        >
                                            <c:out value="${user.nome}" /> (<c:out value="${user.tipo}" />)
                                        </option>
                                    </c:forEach>
                                </select>
                                <small class="form-text text-muted"><fmt:message key="project.assign.users.hint" /></small>
                            </div>
                            <div class="form-actions">
                                <input type="submit" value="<fmt:message key="save.link" />" class="button button-primary" />
                            </div>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <form action="${pageContext.request.contextPath}/projetos/insercao" method="post">
                                <%-- Project Name --%>
                            <div class="form-group">
                                <label for="nome"><fmt:message key="project.name" />:</label>
                                <input type="text" id="nome" name="nome" required />
                            </div>
                                <%-- Project Description --%>
                            <div class="form-group">
                                <label for="descricao"><fmt:message key="project.description" />:</label>
                                <textarea id="descricao" name="descricao" rows="5" required></textarea>
                            </div>
                                <%-- Assign Users (Multi-select) --%>
                            <div class="form-group">
                                <label for="usuariosSelecionados"><fmt:message key="project.assign.users" />:</label>
                                <select id="usuariosSelecionados" name="usuariosSelecionados" multiple class="form-control" size="5">
                                    <c:forEach var="user" items="${requestScope.todosUsuarios}">
                                        <option value="${user.id}"><c:out value="${user.nome}" /> (<c:out value="${user.tipo}" />)</option>
                                    </c:forEach>
                                </select>
                                <small class="form-text text-muted"><fmt:message key="project.assign.users.hint" /></small>
                            </div>
                            <div class="form-actions">
                                <input type="submit" value="<fmt:message key="save.link" />" class="button button-primary" />
                            </div>
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