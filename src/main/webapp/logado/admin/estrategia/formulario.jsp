<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- Define linguagem Java, tipo de conteúdo HTML com UTF-8 --%>

<%@ page isELIgnored="false"%>
<%-- Habilita Expression Language para usar ${} na página --%>

<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%-- Importa JSTL core (para <c:forEach>, <c:if>, <c:choose>, etc) --%>

<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%-- Importa JSTL fmt para internacionalização e formatação --%>

<!DOCTYPE html>
<html>
<fmt:bundle basename="message">
  <%-- Abre o bundle de mensagens internacionalizadas com base em "message" --%>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <%-- Charset da página --%>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <%-- Meta para responsividade em dispositivos móveis --%>

    <title>
      <c:choose>
        <%-- Escolhe o título dependendo se existe um objeto "estrategia" (edição) --%>
        <c:when test="${estrategia != null}"><fmt:message key="strategies.update" /></c:when>
        <c:otherwise><fmt:message key="strategies.create" /></c:otherwise>
      </c:choose>
      | <fmt:message key="page.title" />
      <%-- Título geral do site/página --%>
    </title>

    <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    <%-- Link para o CSS principal da aplicação --%>

    <style>
      /* Pequeno estilo inline para melhorar espaçamento dos campos de imagem */
      .image-field-group { margin-bottom: 10px; padding: 10px; border: 1px solid #eee; }
      .image-field-group label { display: block; margin-top: 5px; }
    </style>

    <script>
      // Função para escapar caracteres especiais em HTML (para evitar problemas de injeção)
      function escapeHtml(unsafe) {
        if (typeof unsafe !== 'string') {
          return unsafe; // Caso seja número ou booleano, retorna direto
        }
        return unsafe
          .replace(/&/g, "&amp;")
          .replace(/</g, "&lt;")
          .replace(/>/g, "&gt;")
          .replace(/"/g, "&quot;")
          .replace(/'/g, "&#039;");
      }

      // Função que cria dinamicamente um grupo de campos para adicionar uma imagem
      // Parâmetros opcionais: url da imagem, descrição e id (para edição)
      function addImageField(url = '', desc = '', idVal = '') {
        const container = document.getElementById('image-fields-container');
        const existingGroups = container.getElementsByClassName('image-field-group');
        const newIndex = existingGroups.length + 1; 
        // Índice para nomear os inputs (incrementa conforme já existentes)

        const div = document.createElement('div');
        div.className = 'image-field-group';

        // Escapa valores para garantir segurança no HTML
        const safeUrl = escapeHtml(url);
        const safeDesc = escapeHtml(desc);
        const safeIdVal = escapeHtml(idVal);

        // Define o conteúdo HTML do novo grupo de campos
        div.innerHTML = `
          <label for="imageUrl${newIndex}"><fmt:message key="strategy.image.url" /> #${newIndex}:</label>
          <input type="text" id="imageUrl${newIndex}" name="imageUrl${newIndex}" value="${safeUrl}" placeholder="https://example.com/image.jpg" />
          <label for="imageDesc${newIndex}"><fmt:message key="strategy.image.description" /> #${newIndex}:</label>
          <input type="text" id="imageDesc${newIndex}" name="imageDesc${newIndex}" value="${safeDesc}" placeholder="<fmt:message key="strategy.image.description.placeholder" />" />
          <input type="hidden" name="imageId${newIndex}" value="${safeIdVal}" />
        `;
        container.appendChild(div);
      }

      // Quando a página terminar de carregar, inicializa os campos de imagens
      document.addEventListener('DOMContentLoaded', function() {
        const container = document.getElementById('image-fields-container');
        let imagesPreFilled = false;

        <c:if test="${estrategia != null && not empty estrategia.imagens}">
          // Se estiver editando uma estratégia e ela já tem imagens
          if (container) {
            container.innerHTML = ''; // Limpa qualquer conteúdo estático de placeholder
          }
          <c:forEach var="img" items="${estrategia.imagens}">
            // Para cada imagem, chama a função JS para criar o campo preenchido
            addImageField(
              '<c:out value="${img.url}" escapeXml="false"/>',
              '<c:out value="${img.descricao}" escapeXml="false"/>',
              '<c:out value="${img.id}"/>'
            );
          </c:forEach>
          imagesPreFilled = true;
        </c:if>

        // Se for criação nova OU não foi pré-preenchido nenhum campo, adiciona um campo vazio
        if (container && !imagesPreFilled && container.children.length === 0) {
          addImageField();
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
          <%-- Exibe saudação com nome do usuário logado --%>
          <span><fmt:message key="welcome.message" /> ${sessionScope.usuarioLogado.nome}!</span>
          <%-- Link para lista de estratégias --%>
          <a href="${pageContext.request.contextPath}/estrategias/lista" class="button"><fmt:message key="strategies.list" /></a>
          <%-- Link para dashboard admin --%>
          <a href="${pageContext.request.contextPath}/admin/" class="button"><fmt:message key="admin.dashboard.title" /></a>
          <%-- Link para logout --%>
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
            <%-- Título do formulário muda conforme criação ou edição --%>
            <c:when test="${estrategia != null}"><fmt:message key="strategies.update" /></c:when>
            <c:otherwise><fmt:message key="strategies.create" /></c:otherwise>
          </c:choose>
        </h2>

        <%-- Exibe mensagens de erro, se houver --%>
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
          <%-- Formulário para edição (se existe estratégia) --%>
          <c:when test="${estrategia != null}">
            <form action="${pageContext.request.contextPath}/admin/estrategias/atualizacao" method="post">
              <%-- Campo oculto com id da estratégia --%>
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
                  <%-- Campos de imagem serão gerados pelo JavaScript --%>
                </div>
                <%-- Botão para adicionar novo campo de imagem dinamicamente --%>
                <button type="button" onclick="addImageField()" class="button button-secondary button-small">
                  <fmt:message key="strategy.add.image" />
                </button>
              </div>

              <div class="form-actions">
                <input type="submit" value="<fmt:message key="save.link" />" class="button button-primary" />
              </div>
            </form>
          </c:when>

          <%-- Formulário para criação (não existe estratégia) --%>
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
                  <%-- Campos de imagem serão gerados pelo JavaScript --%>
                </div>
                <button type="button" onclick="addImageField()" class="button button-secondary button-small">
                  <fmt:message key="strategy.add.image" />
                </button>
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

  </body>
</fmt:bundle>
</html>
