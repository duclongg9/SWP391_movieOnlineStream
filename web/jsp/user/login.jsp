<%-- 
    Document   : login
    Created on : 4 Jul 2025, 13:19:41
    Author     : Dell-PC
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
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
          <h2 class="h2 section-title">Login</h2>
          <form id="loginForm" class="auth-form">
            <input type="email" name="email" placeholder="Email" required />
            <input type="password" name="password" placeholder="Password" required />
            <button type="submit" class="btn btn-primary">Sign in</button>
          </form>
          <p><a href="<%=request.getContextPath()%>/api/auth/sso/google">Login with Google</a></p>
          <p><a href="<%=request.getContextPath()%>/api/auth/sso/facebook">Login with Facebook</a></p>
          <p class="form-switch">Don't have an account? <a href="<%=request.getContextPath()%>/api/auth/register">Register</a></p>
          <div id="errorMessage" class="error-message" style="color: red; display: none;"></div>
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
    // Existing script remains the same
    const base = '<%=request.getContextPath()%>';
    const errorDiv = document.getElementById('errorMessage');

    function showError(message) {
      errorDiv.textContent = message;
      errorDiv.style.display = 'block';
    }

    function hideError() {
      errorDiv.style.display = 'none';
    }

    document.getElementById('loginForm').addEventListener('submit', async function(e) {
      e.preventDefault();
      hideError();
      const form = e.target;
      const email = form.email.value.trim();
      const password = form.password.value.trim();

      if (!email || !password) {
        showError('Please enter email and password.');
        return;
      }

      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        showError('Please enter a valid email address.');
        return;
      }

      const data = new URLSearchParams(new FormData(form));
      try {
        const res = await fetch(base + '/api/auth/login', { method: 'POST', body: data });
        const text = await res.text();
        if (res.ok) {
          try {
            const obj = JSON.parse(text);
            if (obj.token) {
              localStorage.setItem('token', obj.token);
              window.location.href = base + '/index.jsp';
            } else {
              showError('Login successful, but no token received.');
            }
          } catch (err) {
            showError('Unexpected response format.');
          }
        } else {
          try {
            const obj = JSON.parse(text);
            showError(obj.error || 'Login failed. Please check your credentials.');
          } catch (err) {
            showError(text || 'An error occurred during login.');
          }
        }
      } catch (err) {
        showError('Network error. Please try again later.');
      }
    });
  </script>
</body>
</html>