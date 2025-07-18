<%-- 
    Document   : footer
    Created on : 4 Jul 2025, 14:02:31
    Author     : Dell-PC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<footer class="footer" style="background-color: #0f0f0f; color: #fff; padding: 40px 0 20px;">
    <div class="footer-top">
      <div class="container d-flex flex-wrap justify-content-between">

        <div class="footer-brand-wrapper d-flex align-items-center mb-4">
          <a href="<%=request.getContextPath()%>/movies" class="logo me-5">
            <img src="./assets/images/logo.svg" alt="Filmlane logo" style="height: 50px;">
          </a>

          <ul class="footer-list list-unstyled d-flex flex-wrap">
            <li class="me-3"><a href="<%=request.getContextPath()%>/movies" class="footer-link text-decoration-none">Home</a></li>
            <li class="me-3"><a href="#" class="footer-link text-decoration-none">Movie</a></li>
            <li class="me-3"><a href="#" class="footer-link text-decoration-none">TV Show</a></li>
            <li class="me-3"><a href="#" class="footer-link text-decoration-none">Web Series</a></li>
            <li><a href="#" class="footer-link text-decoration-none">Pricing</a></li>
          </ul>
        </div>

        <div class="divider" style="width: 100%; height: 1px; background-color: #333; margin: 20px 0;"></div>

        <div class="quicklink-wrapper w-100 d-flex justify-content-between align-items-center">
          <ul class="quicklink-list list-unstyled d-flex flex-wrap">
            <li class="me-3"><a href="#" class="quicklink-link text-decoration-none">Faq</a></li>
            <li class="me-3"><a href="#" class="quicklink-link text-decoration-none">Help center</a></li>
            <li class="me-3"><a href="#" class="quicklink-link text-decoration-none">Terms of use</a></li>
            <li><a href="#" class="quicklink-link text-decoration-none">Privacy</a></li>
          </ul>

          <ul class="social-list list-unstyled d-flex">
            <li class="me-2"><a href="#" class="social-link text-decoration-none"><ion-icon name="logo-facebook" style="font-size: 1.5rem;"></ion-icon></a></li>
            <li class="me-2"><a href="#" class="social-link text-decoration-none"><ion-icon name="logo-twitter" style="font-size: 1.5rem;"></ion-icon></a></li>
            <li class="me-2"><a href="#" class="social-link text-decoration-none"><ion-icon name="logo-pinterest" style="font-size: 1.5rem;"></ion-icon></a></li>
            <li><a href="#" class="social-link text-decoration-none"><ion-icon name="logo-linkedin" style="font-size: 1.5rem;"></ion-icon></a></li>
          </ul>
        </div>

      </div>
    </div>

    <div class="footer-bottom" style="background-color: #0a0a0a; padding: 20px 0;">
      <div class="container d-flex justify-content-between align-items-center">
        <p class="copyright m-0">
          &copy; 2025 <a href="#" class="text-decoration-none">codewithsadee</a>. All Rights Reserved
        </p>

        <img src="./assets/images/footer-bottom-img.png" alt="Online banking companies logo" style="height: 40px;">
      </div>
    </div>
  </footer>