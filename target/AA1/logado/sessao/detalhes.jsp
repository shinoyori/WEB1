<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%> <%-- Keep for fmt:message and fmt:bundle --%>
<!DOCTYPE html>
<html>
<fmt:bundle basename="message">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="session.details" />: <c:out value="${sessao.titulo}" /> | <fmt:message key="page.title" /></title>
    <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
  </head>
  <body>
  <header class="page-header">
    <div class="container">
      <div class="header-content">
        <div class="site-title">
          <fmt:message key="session.details" />: <c:out value="${sessao.titulo}" />
        </div>
        <div class="login-logout-nav">
          <span><fmt:message key="welcome.message" /> ${sessionScope.usuarioLogado.nome}!</span>
          <a href="${pageContext.request.contextPath}/estrategias/lista"><fmt:message key="strategies.list" /></a>
          <a href="${pageContext.request.contextPath}/logout.jsp" class="button button-logout"><fmt:message key="exit.link" /></a>
        </div>
      </div>
    </div>
  </header>

  <main class="page-content">
    <div class="container">
      <c:if test="${not empty requestScope.mensagens and requestScope.mensagens.existeErros}">
        <div id="erro">
          <ul>
            <c:forEach items="${requestScope.mensagens.erros}" var="erro">
              <li><c:out value="${erro}" /></li>
            </c:forEach>
          </ul>
        </div>
      </c:if>

      <c:if test="${sessao != null}">
        <div class="details-container card">
          <h1><c:out value="${sessao.titulo}" /></h1>

          <p><strong><fmt:message key="project.name" />:</strong> <c:out value="${sessao.projeto.nome}" /></p>
          <p><strong><fmt:message key="session.tester" />:</strong> <c:out value="${sessao.testador.nome}" /></p>
          <p><strong><fmt:message key="session.strategy" />:</strong> <c:out value="${sessao.estrategia.nome}" /></p>
          <p><strong><fmt:message key="session.status" />:</strong> <fmt:message key="session.status.${sessao.status}" /></p>

          <p><strong><fmt:message key="session.description" />:</strong></p>
          <p><c:out value="${sessao.descricao}" /></p>

          <p><strong><fmt:message key="session.created.at" />:</strong> <c:out value="${criadoEmFormatado}" /></p>
          <c:if test="${not empty inicioEmFormatado}">
            <p><strong><fmt:message key="session.started.at" />:</strong> <c:out value="${inicioEmFormatado}" /></p>
          </c:if>
          <c:if test="${not empty finalizadoEmFormatado}">
            <p><strong><fmt:message key="session.finished.at" />:</strong> <c:out value="${finalizadoEmFormatado}" /></p>
          </c:if>

            <%-- Placeholder for Bugs related to this session if you implement that later --%>
            <%-- <h3>Bugs Encontrados:</h3> --%>
            <%-- <c:forEach var="bug" items="${sessao.bugs}"> ... </c:forEach> --%>

          <div class="form-actions">
            <a href="${pageContext.request.contextPath}/sessoes/listaPorProjeto?projetoId=${sessao.projeto.id}" class="button">
              <fmt:message key="return.to.sessions.list.for.project" />
            </a>
              <%-- Lifecycle actions could be added here too if appropriate --%>
            <c:if test="${(sessionScope.usuarioLogado.tipo == 'ADMIN' || sessionScope.usuarioLogado.id == sessao.testador.id)}">
              <c:if test="${sessao.status == 'CRIADA'}">
                <form action="${pageContext.request.contextPath}/sessoes/atualizarStatus" method="post" style="display:inline;">
                  <input type="hidden" name="sessaoId" value="${sessao.id}" />
                  <input type="hidden" name="novoStatus" value="EM_ANDAMENTO" />
                  <input type="hidden" name="projetoId" value="${sessao.projeto.id}" />
                  <button type="submit" class="button button-start" onclick="return confirm('<fmt:message key='confirm.link'/>')"><fmt:message key="session.action.start" /></button>
                </form>
              </c:if>
              <c:if test="${sessao.status == 'EM_ANDAMENTO'}">
                <form action="${pageContext.request.contextPath}/sessoes/atualizarStatus" method="post" style="display:inline;">
                  <input type="hidden" name="sessaoId" value="${sessao.id}" />
                  <input type="hidden" name="novoStatus" value="FINALIZADA" />
                  <input type="hidden" name="projetoId" value="${sessao.projeto.id}" />
                  <button type="submit" class="button button-finish" onclick="return confirm('<fmt:message key='confirm.link'/>')"><fmt:message key="session.action.finish" /></button>
                </form>
              </c:if>
              <c:if test="${sessao.status == 'CRIADA' || sessao.status == 'EM_ANDAMENTO'}">
                <form action="${pageContext.request.contextPath}/sessoes/atualizarStatus" method="post" style="display:inline;">
                  <input type="hidden" name="sessaoId" value="${sessao.id}" />
                  <input type="hidden" name="novoStatus" value="CANCELADA" />
                  <input type="hidden" name="projetoId" value="${sessao.projeto.id}" />
                  <button type="submit" class="button button-cancel" onclick="return confirm('<fmt:message key='confirm.link'/>')"><fmt:message key="session.action.cancel" /></button>
                </form>
              </c:if>
            </c:if>
          </div>
        </div>
      </c:if>
      <c:if test="${sessao == null && empty requestScope.mensagens}">
        <div class="card">
          <p><fmt:message key="session.not.found" /></p>
          <div class="form-actions">
            <a href="${pageContext.request.contextPath}/projetos/lista" class="button"> <%-- Assuming a general projects list exists --%>
              <fmt:message key="return.to.projects.list" />
            </a>
          </div>
        </div>
      </c:if>
    </div>
  </main>
  </body>
</fmt:bundle>
</html>