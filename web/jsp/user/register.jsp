<%-- 
    Document   : register
    Created on : 4 Jul 2025, 13:20:20
    Author     : Dell-PC
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Register</title>
  <link rel="shortcut icon" href="<%=request.getContextPath()%>/favicon.svg" type="image/svg+xml">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
</head>
<body id="top">



  <main>
    <article>
      <section class="auth-section">
        <div class="container">
          <h2 class="h2 section-title">Register</h2>
          <form id="regForm" class="auth-form">
            <input type="email" name="email" placeholder="Email" required />
            <input type="password" name="password" placeholder="Password" required />
            <button type="submit" class="btn btn-primary">Register</button>
          </form>
          <p class="form-switch">Already have an account? <a href="<%=request.getContextPath()%>/api/auth/login">Login</a></p>
          <pre id="result"></pre>
        </div>
      </section>
    </article>
  </main>



  <a href="#top" class="go-top" data-go-top>
    <ion-icon name="chevron-up"></ion-icon>
  </a>

<script src="<%=request.getContextPath()%>/assets/js/script.js"></script>
  <script type="module" src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.esm.js"></script>
  <script nomodule src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.js"></script>
  <script>
    const base = '<%=request.getContextPath()%>';
document.getElementById('regForm').addEventListener('submit', async function(e){
      e.preventDefault();
      const data = new URLSearchParams(new FormData(e.target));
      const res = await fetch(base + '/api/auth/register', {method: 'POST', body: data});
      document.getElementById('result').textContent = await res.text();
    });
  </script>
</body>
</html>