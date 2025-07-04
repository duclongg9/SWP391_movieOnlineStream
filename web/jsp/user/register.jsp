<%-- 
    Document   : register
    Created on : 4 Jul 2025, 13:20:20
    Author     : Dell-PC
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register</title>
</head>
<body>
<h2>Register</h2>
<p><a href="/movies">Back to Home</a></p>
<form id="regForm">
    Email: <input type="email" name="email" /> <br/>
    Password: <input type="password" name="password" /> <br/>
    <input type="submit" value="Register" />
</form>
<pre id="result"></pre>

<script>
document.getElementById('regForm').addEventListener('submit', async function(e){
    e.preventDefault();
    const form = e.target;
    const data = new URLSearchParams(new FormData(form));
    const res = await fetch('/api/auth/register', {method: 'POST', body: data});
    const text = await res.text();
    document.getElementById('result').textContent = text;
});
</script>
</body>
</html>