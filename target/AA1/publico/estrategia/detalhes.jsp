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
    <title><fmt:message key="strategy.details" /> | <c:out value="${estrategia.nome}" /> | <fmt:message key="page.title" /></title>
    <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
  </head>
  <body>
  <header class="page-header">
    <div class="container">
      <div class="header-content">
        <div class="site-title">
          <fmt:message key="strategy.details" />: <c:out value="${estrategia.nome}" />
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
      <c:if test="${not empty requestScope.mensagens and requestScope.mensagens.existeErros}">
        <div id="erro">
          <ul>
            <c:forEach items="${requestScope.mensagens.erros}" var="erro">
              <li><c:out value="${erro}" /></li>
            </c:forEach>
          </ul>
        </div>
      </c:if>

      <c:if test="${estrategia != null}">
        <div class="details-container card">
          <h1><c:out value="${estrategia.nome}" /></h1>
          <p><strong><fmt:message key="strategy.description" />:</strong></p>
          <p><c:out value="${estrategia.descricao}" /></p>

          <c:if test="${not empty estrategia.dicas}">
            <p><strong><fmt:message key="strategy.tips" />:</strong></p>
            <p><c:out value="${estrategia.dicas}" /></p>
          </c:if>

          <c:if test="${not empty estrategia.imagens}">
            <div class="image-gallery">
              <h3><fmt:message key="strategy.images" />:</h3>
              <div class="image-grid">
                <c:forEach var="imagem" items="${estrategia.imagens}">
                  <div class="image-item">
                    <img src="<c:out value='${imagem.url}' />" alt="<c:out value='${imagem.descricao}' />" onerror="this.onerror=null;this.src='https://placehold.co/300x200/cccccc/333333?text=Image+Not+Found';" />
                    <c:if test="${not empty imagem.descricao}">
                      <p class="image-caption"><c:out value="${imagem.descricao}" /></p>
                    </c:if>
                  </div>
                </c:forEach>
              </div>
            </div>
          </c:if>

          <div class="form-actions">
            <a href="${pageContext.request.contextPath}/estrategias/lista" class="button"><fmt:message key="return.to.strategies.list" /></a>
            <c:if test="${sessionScope.usuarioLogado != null && sessionScope.usuarioLogado.tipo == 'ADMIN'}">
              <a href="${pageContext.request.contextPath}/admin/estrategias/edicao?id=${estrategia.id}" class="button button-edit">
                <fmt:message key="strategies.update" />
              </a>
            </c:if>
          </div>
        </div>
      </c:if>
      <c:if test="${estrategia == null && empty requestScope.mensagens}">
        <div class="card">
          <p><fmt:message key="strategy.not.found" /></p>
          <div class="form-actions">
            <a href="${pageContext.request.contextPath}/estrategias/lista" class="button"><fmt:message key="return.to.strategies.list" /></a>
          </div>
        </div>
      </c:if>
    </div>
  </main>
  </body>
</fmt:bundle>
</html>