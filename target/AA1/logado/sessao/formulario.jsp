<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<fmt:bundle basename="message">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><fmt:message key="sessions.create" /> - <c:out value="${projeto.nome}" /> | <fmt:message key="page.title" /></title>
        <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
    <header class="page-header">
        <div class="container">
            <div class="header-content">
                <div class="site-title">
                    <fmt:message key="sessions.create.for.project" />: <c:out value="${projeto.nome}" />
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
                <h2><fmt:message key="sessions.create" /></h2>

                <c:if test="${not empty requestScope.mensagens and requestScope.mensagens.existeErros}">
                    <div id="erro">
                        <ul>
                            <c:forEach items="${requestScope.mensagens.erros}" var="erro">
                                <li><c:out value="${erro}" /></li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>
                    <%-- Repopulate form data using sessaoFormData if validation fails --%>
                <% request.setAttribute("formData", request.getAttribute("sessaoFormData")); %>


                <form action="${pageContext.request.contextPath}/sessoes/insercao" method="post">
                    <input type="hidden" name="projetoId" value="<c:out value='${projeto.id}' />" />

                    <div class="form-group">
                        <label for="titulo"><fmt:message key="session.title" />:</label>
                        <input type="text" id="titulo" name="titulo" value="<c:out value='${formData["titulo"][0]}' />" required />
                    </div>

                    <div class="form-group">
                        <label for="descricao"><fmt:message key="session.description" />:</label>
                        <textarea id="descricao" name="descricao" rows="5"><c:out value='${formData["descricao"][0]}' /></textarea>
                    </div>

                    <div class="form-group">
                        <label for="estrategiaId"><fmt:message key="session.strategy" />:</label>
                        <select id="estrategiaId" name="estrategiaId" class="form-control" required>
                            <option value=""><fmt:message key="session.select.strategy" /></option>
                            <c:forEach var="estrategia" items="${requestScope.listaEstrategias}">
                                <option value="${estrategia.id}" ${formData["estrategiaId"][0] == estrategia.id ? 'selected' : ''}>
                                    <c:out value="${estrategia.nome}" />
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-actions">
                        <input type="submit" value="<fmt:message key="sessions.create.button" />" class="button button-primary" />
                        <a href="${pageContext.request.contextPath}/sessoes/listaPorProjeto?projetoId=${projeto.id}" class="button button-cancel">
                            <fmt:message key="actions.cancel" />
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </main>
    </body>
</fmt:bundle>
</html>