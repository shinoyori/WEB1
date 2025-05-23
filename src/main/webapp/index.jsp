<%-- This file redirects requests from the context root to the /home servlet --%>
<% response.sendRedirect(request.getContextPath() + "/home"); %>