<%--
  Created by IntelliJ IDEA.
  User: luisf
  Date: 28/08/2025
  Time: 22:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Página Principal</title>
</head>
<body>
<%
    HttpSession session1 = request.getSession(false);
    if (session == null || session.getAttribute("auth") == null || !(Boolean)session.getAttribute("auth")) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?err=1");
        return;
    }
%>

<h2>Bienvenido, <%= session.getAttribute("usuario") %> 👋</h2>
<p>Tu rol es: <%= session.getAttribute("role") %></p>

<a href="<%= request.getContextPath() %>/logout">Cerrar Sesión</a>

</body>
</html>

