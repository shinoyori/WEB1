<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<fmt:bundle basename="message">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>
            <c:choose>
                <c:when test="${usuario != null}">
                    <fmt:message key="users.update" />
                </c:when>
                <c:otherwise>
                    <fmt:message key="users.create" />
                </c:otherwise>
            </c:choose>
            | <fmt:message key="page.title" />
        </title>
        <link href="${pageContext.request.contextPath}/layout.css" rel="stylesheet" type="text/css"/>
    </head>

    <body>
    <div align="center">
        <h1>
            <c:choose>
                <c:when test="${usuario != null}">
                    <fmt:message key="users.update" />
                </c:when>
                <c:otherwise>
                    <fmt:message key="users.create" />
                </c:otherwise>
            </c:choose>
        </h1>
        <h2>

                <%-- Link para Logout --%>
            <a href="${pageContext.request.contextPath}/logout.jsp">
                <fmt:message key="exit.link" />
            </a>
            &nbsp;&nbsp;&nbsp;


            <a href="${pageContext.request.contextPath}/usuarios/lista">
                <fmt:message key="users.list" />
            </a>
        </h2>
    </div>

    <div align="center">
        <c:choose>
            <c:when test="${usuario != null}">
                <form action="${pageContext.request.contextPath}/usuarios/atualizacao" method="post">
                    <%@include file="campos.jsp"%>
                </form>
            </c:when>
            <c:otherwise>
                <form action="${pageContext.request.contextPath}/usuarios/insercao" method="post">
                    <%@include file="campos.jsp"%>
                </form>
            </c:otherwise>
        </c:choose>
    </div>

        <%-- Exibição de mensagens de erro --%>
    <c:if test="${not empty requestScope.mensagens and requestScope.mensagens.existeErros}">
        <div id="erro"> <%-- Use a div 'erro' do layout.css --%>
            <ul>
                <c:forEach items="${requestScope.mensagens.erros}" var="erro">
                    <li>${erro}</li>
                </c:forEach>
            </ul>
        </div>
    </c:if>
    </body>
</fmt:bundle>
</html>