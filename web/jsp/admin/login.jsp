<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Admin Login</title>
  <link rel="shortcut icon" href="<%=request.getContextPath()%>/favicon.svg" type="image/svg+xml">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body id="top">
<main>
  <section class="auth-section">
    <div class="container">
      <h2 class="h2 section-title">Admin Login</h2>
      <form id="adminLoginForm" class="auth-form">
        <input type="text" name="username" placeholder="Username" required />
        <input type="password" name="password" placeholder="Password" required />
        <button type="submit" class="btn btn-primary">Sign in</button>
      </form>
      <div id="errorMessage" class="error-message" style="color:red;display:none;"></div>
    </div>
  </section>
</main>
<script>
const base = '<%=request.getContextPath()%>';
const form = document.getElementById('adminLoginForm');
const errorDiv = document.getElementById('errorMessage');
form.addEventListener('submit', async (e)=>{
  e.preventDefault();
  errorDiv.style.display='none';
  const data = new URLSearchParams(new FormData(form));
  const res = await fetch(base+'/api/admin/login',{method:'POST',body:data});
  const text = await res.text();
  if(res.ok){
    try { const obj = JSON.parse(text); localStorage.setItem('adminToken', obj.token); location.href=base+'/admin/users'; } catch(err){location.href=base+'/admin/users';}
  } else {
    errorDiv.textContent = 'Login failed';
    errorDiv.style.display='block';
  }
});
</script>
</body>
</html>
