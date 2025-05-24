<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<fmt:bundle basename="message">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="tester.dashboard.title" /> | <fmt:message key="page.title" /></title>
    <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
  </head>
  <body>
  <header class="page-header">
    <div class="container">
      <div class="header-content">
        <div class="site-title">
          <fmt:message key="tester.dashboard.title" />
        </div>
        <div class="login-logout-nav">
          <span><fmt:message key="welcome.message" /> ${sessionScope.usuarioLogado.nome}!</span>
          <a href="${pageContext.request.contextPath}/logout.jsp" class="button button-logout"><fmt:message key="exit.link" /></a>
        </div>
      </div>
    </div>
  </header>

  <main class="page-content">
    <div class="container">
      <h1><fmt:message key="tester.dashboard.heading" /></h1>

      <c:if test="${not empty requestScope.mensagens and requestScope.mensagens.existeErros}">
        <div id="erro">
          <ul>
            <c:forEach items="${requestScope.mensagens.erros}" var="erro">
              <li><c:out value="${erro}" /></li>
            </c:forEach>
          </ul>
        </div>
      </c:if>

      <div class="dashboard-section card">
        <h2><fmt:message key="tester.projects.assigned" /></h2>
        <c:choose>
          <c:when test="${not empty requestScope.projetosDoTester}">
            <ul class="item-list">
              <c:forEach var="projeto" items="${requestScope.projetosDoTester}">
                <li class="item-list-entry">
                  <div class="item-info">
                    <strong><c:out value="${projeto.nome}" /></strong>
                    <p><c:out value="${projeto.descricao}" /></p>
                  </div>
                  <div class="item-actions">
                    <a href="${pageContext.request.contextPath}/sessoes/listaPorProjeto?projetoId=${projeto.id}" class="button button-details">
                      <fmt:message key="sessions.view.for.project" />
                    </a>
                    <a href="${pageContext.request.contextPath}/sessoes/cadastro?projetoId=${projeto.id}" class="button button-add">
                      <fmt:message key="sessions.create" />
                    </a>
                  </div>
                </li>
              </c:forEach>
            </ul>
          </c:when>
          <c:otherwise>
            <p><fmt:message key="tester.no.projects.assigned" /></p>
          </c:otherwise>
        </c:choose>
      </div>

        <%-- Optional: Section for Tester's own sessions --%>
        <%--
        <div class="dashboard-section card">
            <h2><fmt:message key="tester.my.sessions" /></h2>
            <c:choose>
                <c:when test="${not empty requestScope.sessoesDoTester}">
                     <ul class="item-list">
                        <c:forEach var="sessao" items="${requestScope.sessoesDoTester}">
                           <li class="item-list-entry">
                                <div class="item-info">
                                    <strong><c:out value="${sessao.titulo}" /></strong> (<fmt:message key="project.name" />: <c:out value="${sessao.projeto.nome}" />)
                                    <p><fmt:message key="session.status" />: <fmt:message key="session.status.${sessao.status}" /></p>
                                </div>
                                <div class="item-actions">
                                     <a href="${pageContext.request.contextPath}/sessoes/detalhes?id=${sessao.id}" class="button button-details">
                                         <fmt:message key="session.details" />
                                     </a>
                                     // Add lifecycle buttons if appropriate here
                                </div>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise>
                    <p><fmt:message key="tester.no.sessions.active" /></p>
                </c:otherwise>
            </c:choose>
        </div>
        --%>

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