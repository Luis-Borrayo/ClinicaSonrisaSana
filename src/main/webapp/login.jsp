<%--
  Created by IntelliJ IDEA.
  User: luisf
  Date: 25/08/2025
  Time: 20:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Inicio de Sesión</title>
</head>
<body>
<h2>Inicio de Sesión</h2>

<c:if test="${param.err == 1}">
    <p style="color: red;">❌ Usuario o contraseña incorrectos.</p>
</c:if>
<c:if test="${param.logout == 1}">
    <p style="color: green;">✅ Has cerrado sesión correctamente.</p>
</c:if>

<form action="${pageContext.request.contextPath}/auth/login" method="post">
    <div>
        <label>Usuario:</label>
        <input type="text" name="usuario" required>
    </div>
    <div>
        <label>Contraseña:</label>
        <input type="password" name="password" required>
    </div>
    <div>
        <button type="submit">Ingresar</button>
    </div>
</form>

</body>
</html>
