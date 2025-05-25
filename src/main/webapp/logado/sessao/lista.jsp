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
    <title><fmt:message key="sessions.list.for.project" />: <c:out value="${projeto.nome}" /> | <fmt:message key="page.title" /></title>
    <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    <script>
      function confirmAction(formId) {
        if (confirm("<fmt:message key='confirm.link' />")) { // Make sure 'confirm.link' key exists in your properties
          document.getElementById(formId).submit();
        }
      }
    </script>
  </head>
  <body>
  <header class="page-header">
    <div class="container">
      <div class="header-content">
        <div class="site-title">
          <fmt:message key="sessions.list.for.project" />: <c:out value="${projeto.nome}" />
        </div>
        <div class="login-logout-nav">
          <span><fmt:message key="welcome.message" /> ${sessionScope.usuarioLogado.nome}!</span>

            <%-- Link back to the overall projects list --%>
          <a href="${pageContext.request.contextPath}/projetos/lista" class="button"><fmt:message key="projects.list" /></a>

            <%-- R7: "Create New Session" button for TESTERs --%>
          <c:if test="${sessionScope.usuarioLogado.tipo == 'TESTER'}">
            <a href="${pageContext.request.contextPath}/sessoes/cadastro?projetoId=${projeto.id}" class="button button-add">
              <fmt:message key="sessions.create" />
            </a>
          </c:if>
            <%-- End R7 Button --%>

          <a href="${pageContext.request.contextPath}/logout.jsp" class="button button-logout"><fmt:message key="exit.link" /></a>
        </div>
      </div>
    </div>
  </header>

  <main class="page-content">
    <div class="container">
        <%-- The h1 was "Session List", changed to reflect it's for a specific project --%>
      <h1><fmt:message key="sessions.list.for.project.title" /> <c:out value="${projeto.nome}" /></h1>
        <%-- Removed the <p> with project name as it's now in the h1 and header --%>

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
            <th>
              <a href="${pageContext.request.contextPath}/sessoes/listaPorProjeto?projetoId=${projeto.id}&sortBy=titulo&sortOrder=<c:out value='${currentSortBy == "titulo" && currentSortOrder == "asc" ? "desc" : "asc"}' />">
                <fmt:message key="session.title" />
                <c:if test="${currentSortBy == 'titulo'}"> <c:out value="${currentSortOrder == 'asc' ? '&#9650;' : '&#9660;'}" /> </c:if>
              </a>
            </th>
            <th><fmt:message key="session.tester" /></th>
            <th><fmt:message key="session.strategy" /></th>
            <th>
              <a href="${pageContext.request.contextPath}/sessoes/listaPorProjeto?projetoId=${projeto.id}&sortBy=status&sortOrder=<c:out value='${currentSortBy == "status" && currentSortOrder == "asc" ? "desc" : "asc"}' />">
                <fmt:message key="session.status" />
                <c:if test="${currentSortBy == 'status'}"> <c:out value="${currentSortOrder == 'asc' ? '&#9650;' : '&#9660;'}" /> </c:if>
              </a>
            </th>
            <th><fmt:message key="session.created.at" /></th>
            <th><fmt:message key="session.started.at" /></th>
            <th><fmt:message key="session.finished.at" /></th>
            <th><fmt:message key="actions.link" /></th>
          </tr>
          </thead>
          <tbody>
          <c:forEach var="sessaoView" items="${requestScope.listaSessoesView}">
            <tr>
              <td><c:out value="${sessaoView.id}" /></td>
              <td><a href="${pageContext.request.contextPath}/sessoes/detalhes?id=${sessaoView.id}"><c:out value="${sessaoView.titulo}" /></a></td>
              <td><c:out value="${sessaoView.testador.nome}" /></td>
              <td><c:out value="${sessaoView.estrategia.nome}" /></td>
              <td><fmt:message key="session.status.${sessaoView.status}" /></td>
              <td><c:out value="${sessaoView.criadoEmFormatado}" /></td>
              <td><c:out value="${sessaoView.inicioEmFormatado}" /></td>
              <td><c:out value="${sessaoView.finalizadoEmFormatado}" /></td>
              <td>
                <c:if test="${(sessionScope.usuarioLogado.tipo == 'ADMIN' || sessionScope.usuarioLogado.id == sessaoView.testador.id)}">
                  <c:if test="${sessaoView.status == 'CRIADA'}">
                    <form id="startForm${sessaoView.id}" action="${pageContext.request.contextPath}/sessoes/atualizarStatus" method="post" style="display:inline;">
                      <input type="hidden" name="sessaoId" value="${sessaoView.id}" />
                      <input type="hidden" name="novoStatus" value="EM_ANDAMENTO" />
                      <input type="hidden" name="projetoId" value="${projeto.id}" />
                      <button type="button" onclick="confirmAction('startForm${sessaoView.id}')" class="button button-start">
                        <fmt:message key="session.action.start" />
                      </button>
                    </form>
                    <form id="cancelFormCriada${sessaoView.id}" action="${pageContext.request.contextPath}/sessoes/atualizarStatus" method="post" style="display:inline;">
                      <input type="hidden" name="sessaoId" value="${sessaoView.id}" />
                      <input type="hidden" name="novoStatus" value="CANCELADA" />
                      <input type="hidden" name="projetoId" value="${projeto.id}" />
                      <button type="button" onclick="confirmAction('cancelFormCriada${sessaoView.id}')" class="button button-cancel">
                        <fmt:message key="session.action.cancel" />
                      </button>
                    </form>
                  </c:if>
                  <c:if test="${sessaoView.status == 'EM_ANDAMENTO'}">
                    <form id="finishForm${sessaoView.id}" action="${pageContext.request.contextPath}/sessoes/atualizarStatus" method="post" style="display:inline;">
                      <input type="hidden" name="sessaoId" value="${sessaoView.id}" />
                      <input type="hidden" name="novoStatus" value="FINALIZADA" />
                      <input type="hidden" name="projetoId" value="${projeto.id}" />
                      <button type="button" onclick="confirmAction('finishForm${sessaoView.id}')" class="button button-finish">
                        <fmt:message key="session.action.finish" />
                      </button>
                    </form>
                    <form id="cancelFormEmAndamento${sessaoView.id}" action="${pageContext.request.contextPath}/sessoes/atualizarStatus" method="post" style="display:inline;">
                      <input type="hidden" name="sessaoId" value="${sessaoView.id}" />
                      <input type="hidden" name="novoStatus" value="CANCELADA" />
                      <input type="hidden" name="projetoId" value="${projeto.id}" />
                      <button type="button" onclick="confirmAction('cancelFormEmAndamento${sessaoView.id}')" class="button button-cancel">
                        <fmt:message key="session.action.cancel" />
                      </button>
                    </form>
                  </c:if>
                </c:if>
                <a href="${pageContext.request.contextPath}/sessoes/detalhes?id=${sessaoView.id}" class="button button-details">
                  <fmt:message key="session.details" />
                </a>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty requestScope.listaSessoesView}">
            <tr>
              <td colspan="9"><fmt:message key="sessions.no.sessions.found" /></td>
            </tr>
          </c:if>
          </tbody>
        </table>
      </div>
    </div>
  </main>
  </body>
</fmt:bundle>
</html>