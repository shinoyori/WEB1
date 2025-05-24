<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %> <%-- Updated URI --%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %> <%-- Updated URI --%>
<!DOCTYPE html>
<html>
<fmt:bundle basename="message"> <%-- This should still work with jakarta.tags.fmt --%>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="projects.list" /> | <fmt:message key="page.title" /></title>
    <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    <script>
      function confirmRemoval(id) {
        if (confirm("<fmt:message key='confirm.link' />")) {
          window.location.href = "${pageContext.request.contextPath}/projetos/remocao?id=" + id;
        }
      }
    </script>
  </head>
  <body>
  <header class="page-header">
    <div class="container">
      <div class="header-content">
        <div class="site-title">
          <fmt:message key="projects.list" />
        </div>
        <div class="login-logout-nav">
          <span><fmt:message key="welcome.message" /> ${sessionScope.usuarioLogado.nome}!</span>
          <c:if test="${sessionScope.usuarioLogado.tipo == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/projetos/cadastro" class="button button-add"><fmt:message key="projects.create" /></a>
            <a href="${pageContext.request.contextPath}/admin/" class="button"><fmt:message key="admin.dashboard.title" /></a>
          </c:if>
          <a href="${pageContext.request.contextPath}/logout.jsp" class="button button-logout"><fmt:message key="exit.link" /></a>
        </div>
      </div>
    </div>
  </header>

  <main class="page-content">
    <div class="container">
      <h1><fmt:message key="projects.list" /></h1>

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
              <a href="${pageContext.request.contextPath}/projetos/lista?sortBy=nome&sortOrder=<c:out value='${currentSortBy == "nome" && currentSortOrder == "asc" ? "desc" : "asc"}' />">
                <fmt:message key="project.name" />
                <c:if test="${currentSortBy == 'nome'}">
                  <c:choose>
                    <c:when test="${currentSortOrder == 'asc'}">&#9650;</c:when> <%-- Up arrow --%>
                    <c:otherwise>&#9660;</c:otherwise> <%-- Down arrow --%>
                  </c:choose>
                </c:if>
              </a>
            </th>
            <th><fmt:message key="project.description" /></th>
            <th>
              <a href="${pageContext.request.contextPath}/projetos/lista?sortBy=criadoEm&sortOrder=<c:out value='${currentSortBy == "criadoEm" && currentSortOrder == "asc" ? "desc" : "asc"}' />">
                <fmt:message key="project.created.at" />
                <c:if test="${currentSortBy == 'criadoEm'}">
                  <c:choose>
                    <c:when test="${currentSortOrder == 'asc'}">&#9650;</c:when>
                    <c:otherwise>&#9660;</c:otherwise>
                  </c:choose>
                </c:if>
              </a>
            </th>
            <th><fmt:message key="project.assigned.users" /></th>
            <c:if test="${sessionScope.usuarioLogado.tipo == 'ADMIN'}">
              <th><fmt:message key="actions.link" /></th>
            </c:if>
          </tr>
          </thead>
          <tbody>
          <c:forEach var="projeto" items="${requestScope.listaProjetos}">
            <tr>
              <td><c:out value="${projeto.id}" /></td>
              <td><c:out value="${projeto.nome}" /></td>
              <td><c:out value="${projeto.descricao}" /></td>
              <%@ page import="java.time.format.DateTimeFormatter" %>
              <%@ page import="java.time.LocalDateTime" %>
                <%-- ... other imports and page directives ... --%>

                <%-- Inside your <c:forEach var="projeto" items="${requestScope.listaProjetos}"> loop --%>
              <td>
                <%
                  // Make sure 'projeto' is the current iteration variable from your c:forEach
                  // And that projeto.getCriadoEm() returns a LocalDateTime object
                  Object criadoEmObj = pageContext.findAttribute("projeto_criadoEm"); // A way to access EL variable if needed, or directly use from 'projeto' if accessible
                  // Simpler: Assuming 'projeto' is directly accessible here as a scriptlet variable if it's set as an attribute
                  // This part can be tricky with scriptlets interacting with JSTL vars.
                  // Let's assume 'projeto' is an object available to scriptlets,
                  // or you'd need to pull its 'criadoEm' property more explicitly.

                  // Assuming 'projeto' is an instance of your Projeto domain object
                  // and it is available in the scope of the scriptlet
                  // THIS IS THE MORE LIKELY WAY IT WOULD BE ACCESSED IN THIS CONTEXT
                  // WHEN 'projeto' IS THE var IN A JSTL LOOP
                  br.ufscar.dc.dsw.domain.Projeto currentProjeto = (br.ufscar.dc.dsw.domain.Projeto) pageContext.findAttribute("projeto");
                  if (currentProjeto != null && currentProjeto.getCriadoEm() != null) {
                    LocalDateTime criadoEmDate = currentProjeto.getCriadoEm();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    out.print(criadoEmDate.format(formatter));
                  } else {
                    out.print(""); // Or N/A, or handle as appropriate
                  }
                %>
              </td>


              <td>
                <c:choose>
                  <c:when test="${not empty projeto.usuarios}">
                    <c:forEach var="user" items="${projeto.usuarios}" varStatus="loop">
                      <c:out value="${user.nome}" />${!loop.last ? ', ' : ''}
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <fmt:message key="project.no.assigned.users" />
                  </c:otherwise>
                </c:choose>
              </td>
              <c:if test="${sessionScope.usuarioLogado.tipo == 'ADMIN'}">
                <td>
                  <a href="${pageContext.request.contextPath}/projetos/edicao?id=${projeto.id}" class="button button-edit">
                    <fmt:message key="projects.update" />
                  </a>
                  <button type="button" onclick="confirmRemoval(${projeto.id})" class="button button-delete">
                    <fmt:message key="projects.delete" />
                  </button>
                </td>
              </c:if>
            </tr>
          </c:forEach>
          <c:if test="${empty requestScope.listaProjetos}">
            <tr>
              <td colspan="6"><fmt:message key="projects.no.projects.found" /></td>
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