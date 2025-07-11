<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Filmlane - Best movie collections</title>

  <!-- 
    - favicon
  -->
  <link rel="shortcut icon" href="./favicon.svg" type="image/svg+xml">

  <!-- 
    - custom css link
  -->
  <link rel="stylesheet" href="./assets/css/style.css">

  <!-- 
    - google font link
  -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
</head>

<body id="top">

  <jsp:include page="jsp/template/header.jsp" />





  <main>
    <article>

      <!-- 
        - #HERO
      -->
<jsp:include page="jsp/template/hero.jsp" />

      
      <!-- 
        - #UPCOMING
      -->
<jsp:include page="jsp/template/upcoming.jsp" />
      

      <!-- 
        - #SERVICE
      -->
<jsp:include page="jsp/template/service.jsp" />
      

      <!-- 
        - #TOP RATED
      -->
<jsp:include page="jsp/template/top_rated.jsp" />
      

      <!-- 
        - #TV SERIES
      -->
<jsp:include page="jsp/template/tv_series.jsp" />
      

      <!-- 
        - #CTA
      -->
<jsp:include page="jsp/template/cta.jsp" />
      

    </article>
  </main>
      <!-- 
        - #footer
      -->

<jsp:include page="jsp/template/footer.jsp" />



 


  <!-- 
    - #GO TO TOP
  -->

  <a href="#top" class="go-top" data-go-top>
    <ion-icon name="chevron-up"></ion-icon>
  </a>





  <!-- 
    - custom js link
  -->
  <script src="./assets/js/script.js"></script>

  <!-- 
    - ionicon link
  -->
  <script type="module" src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.esm.js"></script>
  <script nomodule src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.js"></script>

</body>

</html>