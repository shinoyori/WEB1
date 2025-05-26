<%@ page contentType="text/html" pageEncoding="UTF-8"%> 
<%-- Define o tipo de conteúdo da página como HTML e o encoding UTF-8 --%>

<%@ page isELIgnored="false"%> 
<%-- Habilita a Expression Language para usar ${} na página --%>

<%@ taglib uri="jakarta.tags.core" prefix="c"%> 
<%-- Importa a taglib core do JSTL com prefixo c (para <c:forEach>, <c:if>, etc) --%>

<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%> 
<%-- Importa a taglib de formatação e internacionalização com prefixo fmt --%>

<!DOCTYPE html>
<html>
<fmt:bundle basename="message">
  <%-- Define o contexto para buscar mensagens internacionais do arquivo 'message' --%>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <%-- Declara o charset para a página --%>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <%-- Meta para responsividade em dispositivos móveis --%>
    <title><fmt:message key="strategies.list" /> | <fmt:message key="page.title" /></title>
    <%-- Título da página com texto internacionalizado --%>
    <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    <%-- Link para o arquivo CSS de estilos da aplicação --%>
  </head>
  <body>
  <header class="page-header">
    <div class="container">
      <div class="header-content">
        <div class="site-title">
          <fmt:message key="strategies.list" />
          <%-- Mostra o título "Listagem de Estratégias" ou equivalente internacionalizado --%>
        </div>
        <div class="login-logout-nav">
          <c:choose>
            <%-- Verifica se o usuário está logado --%>
            <c:when test="${sessionScope.usuarioLogado != null}">
              <span><fmt:message key="welcome.message" /> ${sessionScope.usuarioLogado.nome}!</span>
              <%-- Exibe mensagem de boas-vindas com nome do usuário logado --%>


              <%-- Links específicos dependendo do tipo do usuário --%>
              <c:if test="${sessionScope.usuarioLogado.tipo == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/admin/" class="button">
                  <fmt:message key="admin.dashboard.title" />
                  <%-- Link para dashboard do admin --%>
                </a>
              </c:if>

              <c:if test="${sessionScope.usuarioLogado.tipo == 'TESTER'}">
                <a href="${pageContext.request.contextPath}/projetos/lista" class="button">
                  <fmt:message key="projects.list" />
                </a>
              </c:if>


              <a href="${pageContext.request.contextPath}/logout.jsp" class="button button-logout">
                <fmt:message key="exit.link" />
                  <%-- Link para logout --%>
              </a>
              
            </c:when>
            <c:otherwise>
              <%-- Caso o usuário não esteja logado, mostra o link para login --%>
              <a href="${pageContext.request.contextPath}/login.jsp" class="button button-login">
                <fmt:message key="user.login" />
              </a>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </header>

  <main class="page-content">
    <div class="container">
      <h1><fmt:message key="strategies.list" /></h1>
      <%-- Título principal da página --%>

      <c:if test="${not empty requestScope.mensagens and requestScope.mensagens.existeErros}">
        <%-- Verifica se há mensagens de erro para exibir --%>
        <div id="erro">
          <ul>
            <c:forEach items="${requestScope.mensagens.erros}" var="erro">
              <li><c:out value="${erro}" /></li>
              <%-- Lista cada mensagem de erro --%>
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
          <%-- Percorre a lista de estratégias disponível no requestScope --%>
          <c:forEach var="estrategia" items="${requestScope.listaEstrategias}">
            <tr>
              <td><c:out value="${estrategia.id}" /></td>
              <%-- Exibe o ID da estratégia --%>
              <td><c:out value="${estrategia.nome}" /></td>
              <%-- Exibe o nome da estratégia --%>
              <td><c:out value="${estrategia.descricao}" /></td>
              <%-- Exibe a descrição da estratégia --%>
              <td><c:out value="${estrategia.dicas}" /></td>
              <%-- Exibe as dicas da estratégia --%>
              <td>
                <%-- Link para detalhes da estratégia --%>
                <a href="${pageContext.request.contextPath}/estrategias/detalhes?id=${estrategia.id}" class="button button-details">
                  <fmt:message key="strategy.details" />
                </a>
                <%-- Se o usuário estiver logado e for admin, mostra botões de editar e remover --%>
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

          <%-- Caso a lista esteja vazia, exibe mensagem informativa --%>
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
  </body>
</fmt:bundle>
</html>
