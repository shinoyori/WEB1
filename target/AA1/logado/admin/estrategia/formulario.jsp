<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%-- Removed any reference or need for fn: functions library --%>
<!DOCTYPE html>
<html>
<fmt:bundle basename="message">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
      <c:choose>
        <c:when test="${estrategia != null}"><fmt:message key="strategies.update" /></c:when>
        <c:otherwise><fmt:message key="strategies.create" /></c:otherwise>
      </c:choose>
      | <fmt:message key="page.title" />
    </title>
    <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    <style>
      /* Minor style for better spacing in image field groups if not in main CSS */
      .image-field-group { margin-bottom: 10px; padding: 10px; border: 1px solid #eee; }
      .image-field-group label { display: block; margin-top: 5px; }
    </style>
    <script>
      // Helper function to escape HTML characters for use in HTML attributes
      function escapeHtml(unsafe) {
        if (typeof unsafe !== 'string') {
          return unsafe; // Or handle numbers/booleans as needed
        }
        return unsafe
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#039;");
      }

      function addImageField(url = '', desc = '', idVal = '') {
        const container = document.getElementById('image-fields-container');
        const existingGroups = container.getElementsByClassName('image-field-group');
        const newIndex = existingGroups.length + 1; // This simple indexing works if fields are not removed dynamically

        const div = document.createElement('div');
        div.className = 'image-field-group';

        // Use the helper to ensure values are safe for HTML attributes
        const safeUrl = escapeHtml(url);
        const safeDesc = escapeHtml(desc);
        const safeIdVal = escapeHtml(idVal); // id is usually a number, but good practice

        div.innerHTML = `
                    <label for="imageUrl${newIndex}"><fmt:message key="strategy.image.url" /> #${newIndex}:</label>
                    <input type="text" id="imageUrl${newIndex}" name="imageUrl${newIndex}" value="${safeUrl}" placeholder="https://example.com/image.jpg" />
                    <label for="imageDesc${newIndex}"><fmt:message key="strategy.image.description" /> #${newIndex}:</label>
                    <input type="text" id="imageDesc${newIndex}" name="imageDesc${newIndex}" value="${safeDesc}" placeholder="<fmt:message key="strategy.image.description.placeholder" />" />
                    <input type="hidden" name="imageId${newIndex}" value="${safeIdVal}" />
                `;
        container.appendChild(div);
      }

      document.addEventListener('DOMContentLoaded', function() {
        const container = document.getElementById('image-fields-container');
        let imagesPreFilled = false;

        <c:if test="${estrategia != null && not empty estrategia.imagens}">
        // If editing and images exist, clear any static placeholder and fill them
        if (container) {
          container.innerHTML = ''; // Clear placeholder fields if any
        }
        <c:forEach var="img" items="${estrategia.imagens}">
        // Pass raw values from JSTL; escaping is handled by addImageField
        addImageField(
                '<c:out value="${img.url}" escapeXml="false"/>',
                '<c:out value="${img.descricao}" escapeXml="false"/>',
                '<c:out value="${img.id}"/>'
        );
        </c:forEach>
        imagesPreFilled = true;
        </c:if>

        // If it's create mode OR (edit mode and no images were pre-filled),
        // ensure at least one empty field is there.
        if (container && !imagesPreFilled && container.children.length === 0) {
          addImageField(); // Add one empty set of fields for new entries
        }
      });
    </script>
  </head>
  <body>
  <header class="page-header">
    <div class="container">
      <div class="header-content">
        <div class="site-title">
          <c:choose>
            <c:when test="${estrategia != null}"><fmt:message key="strategies.update" /></c:when>
            <c:otherwise><fmt:message key="strategies.create" /></c:otherwise>
          </c:choose>
        </div>
        <div class="login-logout-nav">
          <span><fmt:message key="welcome.message" /> ${sessionScope.usuarioLogado.nome}!</span>
          <a href="${pageContext.request.contextPath}/estrategias/lista" class="button"><fmt:message key="strategies.list" /></a>
          <a href="${pageContext.request.contextPath}/admin/" class="button"><fmt:message key="admin.dashboard.title" /></a>
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
            <c:when test="${estrategia != null}"><fmt:message key="strategies.update" /></c:when>
            <c:otherwise><fmt:message key="strategies.create" /></c:otherwise>
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
          <c:when test="${estrategia != null}">
            <form action="${pageContext.request.contextPath}/admin/estrategias/atualizacao" method="post">
              <input type="hidden" name="id" value="<c:out value='${estrategia.id}' />" />
              <div class="form-group">
                <label for="nome"><fmt:message key="strategy.name" />:</label>
                <input type="text" id="nome" name="nome" value="<c:out value='${estrategia.nome}' />" required />
              </div>
              <div class="form-group">
                <label for="descricao"><fmt:message key="strategy.description" />:</label>
                <textarea id="descricao" name="descricao" rows="5" required><c:out value='${estrategia.descricao}' /></textarea>
              </div>
              <div class="form-group">
                <label for="dicas"><fmt:message key="strategy.tips" />:</label>
                <textarea id="dicas" name="dicas" rows="3"><c:out value='${estrategia.dicas}' /></textarea>
              </div>
              <div class="form-group">
                <label><fmt:message key="strategy.images" />:</label>
                <div id="image-fields-container">
                    <%-- Image fields will be populated by JavaScript --%>
                </div>
                <button type="button" onclick="addImageField()" class="button button-secondary button-small"><fmt:message key="strategy.add.image" /></button>
              </div>
              <div class="form-actions">
                <input type="submit" value="<fmt:message key="save.link" />" class="button button-primary" />
              </div>
            </form>
          </c:when>
          <c:otherwise>
            <form action="${pageContext.request.contextPath}/admin/estrategias/insercao" method="post">
              <div class="form-group">
                <label for="nome"><fmt:message key="strategy.name" />:</label>
                <input type="text" id="nome" name="nome" required />
              </div>
              <div class="form-group">
                <label for="descricao"><fmt:message key="strategy.description" />:</label>
                <textarea id="descricao" name="descricao" rows="5" required></textarea>
              </div>
              <div class="form-group">
                <label for="dicas"><fmt:message key="strategy.tips" />:</label>
                <textarea id="dicas" name="dicas" rows="3"></textarea>
              </div>
              <div class="form-group">
                <label><fmt:message key="strategy.images" />:</label>
                <div id="image-fields-container">
                    <%-- Image fields will be populated by JavaScript --%>
                </div>
                <button type="button" onclick="addImageField()" class="button button-secondary button-small"><fmt:message key="strategy.add.image" /></button>
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