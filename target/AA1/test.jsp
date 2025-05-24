<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head><title>FMT Test</title></head>
<body>
<jsp:useBean id="now" class="java.util.Date" />
<p>Formatted Date: <fmt:formatDate value="${now}" pattern="dd/MM/yyyy HH:mm:ss" /></p>
<p>Formatted Date Time (problematic tag): <fmt:formatDateTime value="${now}" pattern="dd/MM/yyyy HH:mm:ss" /></p>
</body>
</html>