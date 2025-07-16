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
        <div class="auth-container">
          <h2 class="auth-title">Login</h2>
          <p class="note">Note: Email/Password login is only for Admins. Users please use Google or Facebook.</p>
          <form id="loginForm" class="auth-form">
            <input type="email" name="email" placeholder="Email" required />
            <input type="password" name="password" placeholder="Password" required />
            <button type="submit" class="btn btn-primary">Sign in (Admin Only)</button>
          </form>
          <div class="sso-buttons" style="flex-direction: row; justify-content: center; gap: 20px;">
            <a href="<%=request.getContextPath()%>/api/auth/sso/google" class="sso-btn" style="padding: 10px; width: 50px; height: 50px; border-radius: 50%; display: flex; align-items: center; justify-content: center;" title="Login with Google">
              <ion-icon name="logo-google" style="font-size: 30px;"></ion-icon>
            </a>
            <a href="<%=request.getContextPath()%>/api/auth/sso/facebook" class="sso-btn" style="padding: 10px; width: 50px; height: 50px; border-radius: 50%; display: flex; align-items: center; justify-content: center;" title="Login with Facebook">
              <ion-icon name="logo-facebook" style="font-size: 30px;"></ion-icon>
            </a>
          </div>
          <p class="form-switch" style="margin-top: 20px;"><a href="<%=request.getContextPath()%>/index.jsp">Back to Home</a></p>
          <!--<p class="form-switch">Don't have an account? <a href="<%=request.getContextPath()%>/api/auth/register">Register</a></p>-->
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

    // Handle error from query parameter (e.g., from SSO callback errors)
    const urlParams = new URLSearchParams(window.location.search);
    const errorParam = urlParams.get('error');
    if (errorParam) {
      showError(decodeURIComponent(errorParam));
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