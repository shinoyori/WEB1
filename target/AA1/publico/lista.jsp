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
    <title><fmt:message key="strategies.list" /> | <fmt:message key="page.title" /></title>
    <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
  </head>
  <body>
  <header class="page-header">
    <div class="container">
      <div class="header-content">
        <div class="site-title">
          <fmt:message key="strategies.list" />
        </div>
        <div class="login-logout-nav">
          <c:choose>
            <c:when test="${sessionScope.usuarioLogado != null}">
              <span><fmt:message key="welcome.message" /> ${sessionScope.usuarioLogado.nome}!</span>
              <a href="${pageContext.request.contextPath}/logout.jsp" class="button button-logout"><fmt:message key="exit.link" /></a>
              <c:if test="${sessionScope.usuarioLogado.tipo == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/admin/" class="button"><fmt:message key="admin.dashboard.title" /></a>
              </c:if>
              <c:if test="${sessionScope.usuarioLogado.tipo == 'TESTER'}">
                <a href="${pageContext.request.contextPath}/usuarios/" class="button"><fmt:message key="users.list" /></a>
              </c:if>
            </c:when>
            <c:otherwise>
              <a href="${pageContext.request.contextPath}/login.jsp" class="button button-login"><fmt:message key="user.login" /></a>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </header>

  <main class="page-content">
    <div class="container">
      <h1><fmt:message key="strategies.list" /></h1>

      <c:if test="${not empty requestScope.mensagens and requestScope.mensagens.existeErros}">
        <div id="erro">
          <ul>
            <c:forEach items="${requestScope.mensagens.erros}" var="erro">
              <li><c:out value="${erro}" /></li>
            </c:forEach>
          </ul>
        </div>
      </c:if>

      <div class="table-responsive card">
        <table class="data-table">
          <thead>
          <tr>
            <th>ID</th>
            <th><fmt:message key="strategy.name" /></th>
            <th><fmt:message key="strategy.description" /></th>
            <th><fmt:message key="strategy.tips" /></th>
            <th><fmt:message key="actions.link" /></th>
          </tr>
          </thead>
          <tbody>
          <c:forEach var="estrategia" items="${requestScope.listaEstrategias}">
            <tr>
              <td><c:out value="${estrategia.id}" /></td>
              <td><c:out value="${estrategia.nome}" /></td>
              <td><c:out value="${estrategia.descricao}" /></td>
              <td><c:out value="${estrategia.dicas}" /></td>
              <td>
                <a href="${pageContext.request.contextPath}/estrategias/detalhes?id=${estrategia.id}" class="button button-details">
                  <fmt:message key="strategy.details" />
                </a>
                <c:if test="${sessionScope.usuarioLogado != null && sessionScope.usuarioLogado.tipo == 'ADMIN'}">
                  <a href="${pageContext.request.contextPath}/admin/estrategias/edicao?id=${estrategia.id}" class="button button-edit">
                    <fmt:message key="strategies.update" />
                  </a>
                  <button type="button" onclick="if(confirm('<fmt:message key='confirm.link' />')) window.location.href='${pageContext.request.contextPath}/admin/estrategias/remocao?id=${estrategia.id}'" class="button button-delete">
                    <fmt:message key="strategies.delete" />
                  </button>
                </c:if>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty requestScope.listaEstrategias}">
            <tr>
              <td colspan="5"><fmt:message key="strategies.no.strategies.found" /></td>
            </tr>
          </c:if>
          </tbody>
        </table>
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