<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<fmt:bundle basename="message">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><fmt:message key="page.title" /></title>
        <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
    <div class="login-form-container">
        <h1><fmt:message key="page.label" /></h1>

        <c:if test="${not empty requestScope.mensagens and requestScope.mensagens.existeErros}">
            <div id="erro">
                <ul>
                    <c:forEach var="erro" items="${requestScope.mensagens.erros}">
                        <li><c:out value="${erro}" /></li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login">
            <div>
                <label for="loginField"><fmt:message key="user.login" />:</label>
                <input type="text" id="loginField" name="login" value="<c:out value='${param.login}'/>" required />
            </div>
            <div>
                <label for="passwordField"><fmt:message key="user.password" />:</label>
                <input type="password" id="passwordField" name="senha" required /> <%-- Changed to type="password" for security --%>
            </div>
            <div>
                <input type="submit" name="bOK" value="<fmt:message key='page.title'/>" class="button" /> <%-- Added class="button" --%>
            </div>
        </form>

        <div style="text-align: center; margin-top: 15px;">
            <a href="${pageContext.request.contextPath}/" class="button button-secondary">
                <fmt:message key="return.to.home" />
            </a>
        </div>
    </div>
    </body>
</fmt:bundle>
</html>