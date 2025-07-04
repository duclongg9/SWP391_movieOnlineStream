<%-- 
    Document   : login
    Created on : 4 Jul 2025, 13:19:41
    Author     : Dell-PC
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
<h2>Login</h2>
<p><a href="/movies">Back to Home</a></p>
<form method="post" action="/api/auth/login">
    Email: <input type="email" name="email" /> <br/>
    Password: <input type="password" name="password" /> <br/>
    <input type="submit" value="Login" />
</form>
</body>
</html>
