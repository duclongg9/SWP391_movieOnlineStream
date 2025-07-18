<%-- 
    Document   : header
    Created on : 4 Jul 2025, 14:02:52
    Author     : Dell-PC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String ctx = request.getContextPath();
%>
<header class="header" data-header>
    <div class="container">

      <div class="overlay" data-overlay></div>

      <a href="<%=ctx%>/movies" class="logo">
        <img src="<%=ctx%>/assets/images/logo.svg" alt="Filmlane logo">
      </a>

      <div class="header-actions">

        <button class="search-btn">
          <ion-icon name="search-outline"></ion-icon>
        </button>

        <div class="lang-wrapper">
          <label for="language">
            <ion-icon name="globe-outline"></ion-icon>
          </label>

          <select name="language" id="language">
            <option value="en">EN</option>
            <option value="au">AU</option>
            <option value="ar">AR</option>
            <option value="tu">TU</option>
          </select>
        </div>

        <a href="<%=ctx%>/api/auth/login" class="btn btn-primary" id="loginLink">Sign in</a>
<!--        <a href="<%=ctx%>/api/auth/register" class="btn" id="registerLink">Register</a>-->
<!--        <a href="<%=ctx%>/user/profile" class="btn" id="profileLink" style="display:none;">Profile</a>
        <a href="<%=ctx%>/history" class="btn" id="historyLink" style="display:none;">History</a>-->
        <a href="<%=ctx%>/admin/users" class="btn" id="adminLink" style="display:none;">Admin</a>
<!--        <a href="#" class="btn" id="logoutLink" style="display:none;">Logout</a>
        <span id="userEmail" style="color:#fff; margin-left:10px; display:none;"></span>-->
        <div class="user-dropdown" id="userDropdown" style="display:none;">
          <ion-icon name="person-circle-outline"></ion-icon>
          <div class="user-dropdown-menu" id="userMenu">
            <span id="userName" style="padding:5px 10px; display:block;"></span>
            <a href="<%=ctx%>/user/profile">Profile</a>
            <a href="<%=ctx%>/history">History</a>
            <a href="#" id="logoutLink">Logout</a>
          </div>
        </div>


      </div>

      <button class="menu-open-btn" data-menu-open-btn>
        <ion-icon name="reorder-two"></ion-icon>
      </button>

      <nav class="navbar" data-navbar>

        <div class="navbar-top">

          <a href="<%=ctx%>/movies" class="logo">
            <img src="<%=ctx%>/assets/images/logo.svg" alt="Filmlane logo">
          </a>

          <button class="menu-close-btn" data-menu-close-btn>
            <ion-icon name="close-outline"></ion-icon>
          </button>

        </div>

        <ul class="navbar-list">

          <li>
            <a href="<%=ctx%>/movies" class="navbar-link">Home</a>
          </li>

          <li>
            <a href="#" class="navbar-link">Movie</a>
          </li>

          <li>
            <a href="#" class="navbar-link">Tv Show</a>
          </li>

          <li>
            <a href="#" class="navbar-link">Web Series</a>
          </li>

          <li>
            <a href="#" class="navbar-link">Pricing</a>
          </li>

        </ul>

        <ul class="navbar-social-list">

          <li>
            <a href="#" class="navbar-social-link">
              <ion-icon name="logo-twitter"></ion-icon>
            </a>
          </li>

          <li>
            <a href="#" class="navbar-social-link">
              <ion-icon name="logo-facebook"></ion-icon>
            </a>
          </li>

          <li>
            <a href="#" class="navbar-social-link">
              <ion-icon name="logo-pinterest"></ion-icon>
            </a>
          </li>

          <li>
            <a href="#" class="navbar-social-link">
              <ion-icon name="logo-instagram"></ion-icon>
            </a>
          </li>

          <li>
            <a href="#" class="navbar-social-link">
              <ion-icon name="logo-youtube"></ion-icon>
            </a>
          </li>

        </ul>

      </nav>

    </div>
  </header>
<script>
    (function() {
      const token = localStorage.getItem('token');
      const base = '<%=ctx%>';
      if (token) {
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
//          const emailSpan = document.getElementById('userEmail');
          const loginLink = document.getElementById('loginLink');
//          const regLink = document.getElementById('registerLink');
//          const profileLink = document.getElementById('profileLink');
//          const historyLink = document.getElementById('historyLink');
//          const logoutLink = document.getElementById('logoutLink');
          const adminLink = document.getElementById('adminLink');
//          if (payload.sub) {
//            emailSpan.textContent = payload.sub;
//            emailSpan.style.display = 'inline-block';
//          }
          const dropdown = document.getElementById('userDropdown');
          const nameSpan = document.getElementById('userName');
          if (loginLink) loginLink.style.display = 'none';
//          if (regLink) regLink.style.display = 'none';
//          if (profileLink) profileLink.style.display = 'inline-block';
//          if (historyLink) historyLink.style.display = 'inline-block';
//          if (logoutLink) logoutLink.style.display = 'inline-block';
            if (dropdown) dropdown.style.display = 'inline-block';
          if (adminLink && payload.sub === 'admin@example.com') {
            adminLink.style.display = 'inline-block';
          }
          fetch(base + '/api/user/profile', {headers:{Authorization:'Bearer '+token}})
            .then(r => r.json())
            .then(d => { if(nameSpan) nameSpan.textContent = d.fullName || payload.sub; });
        } catch (err) {
          console.error('Invalid token:', err);
          localStorage.removeItem('token');
        }
      }
      const logoutLink = document.getElementById('logoutLink');
      if (logoutLink) {
        logoutLink.addEventListener('click', async function(e) {
          e.preventDefault();
          try {
            await fetch(base + '/api/auth/logout', { method: 'POST' });
          } catch (err) {
            console.error('Logout request failed:', err);
          }
          localStorage.removeItem('token');
          window.location.href = base + '/index.jsp';
        });
      }
    })();
  </script>