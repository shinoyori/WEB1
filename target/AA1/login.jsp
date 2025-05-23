<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<fmt:bundle basename="message">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0"> <%-- For responsiveness --%>
        <title><fmt:message key="page.title" /></title>
        <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
    <div class="login-form-container">  <%-- This is the main wrapper for centering --%>
        <h1><fmt:message key="page.label" /></h1> <%-- "Welcome to the system" --%>

        <c:if test="${not empty requestScope.mensagens and requestScope.mensagens.existeErros}">
            <div id="erro">
                <ul>
                    <c:forEach var="erro" items="${requestScope.mensagens.erros}">
                        <li><c:out value="${erro}" /></li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>

            <%-- The IndexController is mapped to /login. Targeting /login is clearer. --%>
        <form method="post" action="${pageContext.request.contextPath}/login">
            <div>
                <label for="loginField"><fmt:message key="user.login" />:</label>
                <input type="text" id="loginField" name="login" value="<c:out value='${param.login}'/>" required />
            </div>
            <div>
                <label for="passwordField"><fmt:message key="user.password" />:</label>
                    <%-- Kept as type="text" as per your file's comment. For security, consider type="password". --%>
                <input type="text" id="passwordField" name="senha" required />
            </div>
            <div>
                    <%-- The key 'user.login' might display as "Email" on the button.
                         Using 'page.title' (which is "Login") for the button value seems more appropriate based on your message keys.

                    --%>
                <input type="submit" name="bOK" value="<fmt:message key='page.title'/>" />
            </div>
        </form>
    </div>
    </body>
</fmt:bundle>
</html>