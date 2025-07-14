<%-- 
    Document   : header
    Created on : 4 Jul 2025, 14:02:52
    Author     : Dell-PC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String ctx = request.getContextPath();
    model.User sessionUser = (model.User) session.getAttribute("loggedInUser");
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

        <a href="<%=ctx%>/api/auth/login" class="btn btn-primary" id="loginLink" <%= (sessionUser!=null ? "style=\"display:none;\"" : "") %>>Sign in</a>
        <a href="<%=ctx%>/api/auth/register" class="btn" id="registerLink" <%= (sessionUser!=null ? "style=\"display:none;\"" : "") %>>Register</a>
        <a href="<%=ctx%>/user/profile" class="btn" id="profileLink" <%= (sessionUser==null ? "style=\"display:none;\"" : "") %>>Profile</a>
        <a href="<%=ctx%>/history" class="btn" id="historyLink" <%= (sessionUser==null ? "style=\"display:none;\"" : "") %>>History</a>
        <a href="<%=ctx%>/admin/users" class="btn" id="adminLink" <%= (sessionUser!=null && \"admin\".equals(sessionUser.getRole()) ? "" : "style=\"display:none;\"") %>>Admin</a>
        <a href="<%=ctx%>/api/auth/logout" class="btn" id="logoutLink" <%= (sessionUser==null ? "style=\"display:none;\"" : "") %>>Logout</a>
        <span id="userEmail" style="color:#fff; margin-left:10px; <%= (sessionUser==null ? "display:none;" : "") %>">
            <%= sessionUser!=null ? sessionUser.getEmail() : "" %>
        </span>

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