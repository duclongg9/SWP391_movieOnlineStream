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
<p><a href="<%=request.getContextPath()%>/movies">Back to Home</a></p>
<form id="loginForm">
    Email: <input type="email" name="email" /> <br/>
    Password: <input type="password" name="password" /> <br/>
    <input type="submit" value="Login" />
</form>
<pre id="result"></pre>

<script>
    const base = '<%=request.getContextPath()%>';
document.getElementById('loginForm').addEventListener('submit', async function(e){
    e.preventDefault();
    const form = e.target;
    const data = new URLSearchParams(new FormData(form));
    const res = await fetch(base + '/api/auth/login', {method: 'POST', body: data});
    const text = await res.text();
    document.getElementById('result').textContent = text;
});
</script>
</body>
</html>
