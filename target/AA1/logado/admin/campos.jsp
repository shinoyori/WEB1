<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<c:if test="${usuario != null}">
    <input type="hidden" name="id" value="<c:out value='${usuario.id}' />" />
</c:if>

<div class="form-group">
    <label for="nome"><fmt:message key="user.name" />:</label>
    <input type="text" id="nome" name="nome" value="<c:out value='${usuario.nome}' />" required />
</div>

<div class="form-group">
    <label for="login"><fmt:message key="user.login" />:</label>
    <input type="text" id="login" name="login" value="<c:out value='${usuario.login}' />" required />
</div>

<div class="form-group">
    <label for="senha"><fmt:message key="user.password" />:</label>
    <input type="text" id="senha" name="senha" <c:if test="${usuario == null}">required</c:if> />
    <c:if test="${usuario != null}">
        <small class="form-text text-muted">(<fmt:message key="user.password.update.hint" />)</small>
    </c:if>
</div>

<div class="form-group">
    <label for="tipo"><fmt:message key="user.role" />:</label>
    <select id="tipo" name="tipo" class="form-control">
        <option value="ADMIN" ${usuario.tipo == 'ADMIN' ? 'selected="selected"' : ''}>ADMIN</option>
        <option value="TESTER" ${usuario.tipo == 'TESTER' ? 'selected="selected"' : ''}>TESTER</option>
        <option value="GUEST" ${usuario.tipo == 'GUEST' ? 'selected="selected"' : ''}>GUEST</option>
    </select>
</div>

<div class="form-actions">
    <input type="submit" value="<fmt:message key="save.link" />" class="button button-primary" />
</div>