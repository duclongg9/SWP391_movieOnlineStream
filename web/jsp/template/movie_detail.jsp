<%-- 
    Document   : movie_detail
    Created on : 4 Jul 2025, 14:39:04
    Author     : Dell-PC
--%>

<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="model.Movie"%>
<%
    Movie mv = (Movie) request.getAttribute("movie");
    if (mv == null) {
        mv = new Movie();
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><%= mv.getTitle() %></title>

  <!-- favicon -->
  <link rel="shortcut icon" href="./favicon.svg" type="image/svg+xml">

  <!-- custom css link -->
  <link rel="stylesheet" href="./assets/css/style.css">

  <!-- Bootstrap CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

  <!-- google font link -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

  <style>
    .movie-detail { background-color: #1a1a1a; color: #fff; padding: 60px 0; }
    .movie-detail-banner { position: relative; overflow: hidden; border-radius: 15px; }
    .movie-detail-banner img { width: 100%; height: auto; transition: transform 0.3s ease; }
    .movie-detail-banner:hover img { transform: scale(1.05); }
    .play-btn { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); font-size: 4rem; color: #fff; opacity: 0.8; transition: opacity 0.3s; }
    .play-btn:hover { opacity: 1; }
    .detail-subtitle { font-size: 1.2rem; color: #ccc; }
    .detail-title { font-size: 3rem; margin-bottom: 20px; }
    .meta-wrapper { display: flex; flex-wrap: wrap; gap: 15px; margin-bottom: 30px; }
    .badge { padding: 5px 10px; border-radius: 20px; font-weight: bold; }
    .badge-fill { background-color: #28a745; color: #fff; }
    .badge-outline { border: 1px solid #fff; color: #fff; }
    .ganre-wrapper a { color: #ccc; text-decoration: none; margin-right: 10px; }
    .date-time { display: flex; gap: 20px; }
    .storyline { font-size: 1.1rem; line-height: 1.6; margin-bottom: 30px; }
    .details-actions { display: flex; align-items: center; gap: 20px; margin-bottom: 20px; }
    .share { background: none; border: none; color: #fff; cursor: pointer; display: flex; align-items: center; gap: 5px; }
    .btn-primary { background-color: #e50914; border: none; padding: 10px 20px; border-radius: 5px; font-weight: bold; transition: background-color 0.3s; }
    .btn-primary:hover { background-color: #c40812; }
    .download-btn { color: #fff; text-decoration: none; display: inline-flex; align-items: center; gap: 5px; }
    .tv-series { padding: 60px 0; background-color: #0f0f0f; }
    .section-title { color: #fff; }
    .movies-list { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; }
    .movie-card { background-color: #1f1f1f; border-radius: 10px; overflow: hidden; transition: transform 0.3s; }
    .movie-card:hover { transform: translateY(-5px); }
    .card-banner img { width: 100%; height: 300px; object-fit: cover; }
    .title-wrapper { padding: 15px; text-align: center; }
    .card-title { color: #fff; margin-bottom: 5px; }
    .card-meta { display: flex; justify-content: space-between; padding: 0 15px 15px; color: #ccc; }
  </style>
</head>

<body id="top">

  <jsp:include page="/jsp/template/header.jsp" />

  <main>
    <article>

      <!-- #MOVIE DETAIL -->
      <section class="movie-detail">
        <div class="container">
          <div class="row">
            <div class="col-md-6">
              <figure class="movie-detail-banner">
                <img src="<%= mv.getPosterUrl() %>" alt="<%= mv.getTitle() %> poster">
                <button class="play-btn">
                  <ion-icon name="play-circle-outline"></ion-icon>
                </button>
              </figure>
            </div>
            <div class="col-md-6 movie-detail-content">
              <p class="detail-subtitle"><%= mv.getGenre() %></p>
              <h1 class="h1 detail-title"><%= mv.getTitle() %></h1>
              <div class="meta-wrapper">
                <div class="badge-wrapper">
                  <div class="badge badge-fill"><%= mv.getRating() %></div>
                  <div class="badge badge-outline"><%= mv.getQuality() %></div>
                </div>
                <div class="ganre-wrapper">
                  <a href="#"><%= mv.getGenre() %></a>
                </div>
                <div class="date-time">
                  <div>
                    <ion-icon name="calendar-outline"></ion-icon>
                    <time datetime="<%= mv.getYear() %>"><%= mv.getYear() %></time>
                  </div>
                  <div>
                    <ion-icon name="time-outline"></ion-icon>
                    <time datetime="PT115M">115 min</time>
                  </div>
                </div>
              </div>
              <p class="storyline"><%= mv.getDescription() %></p>
              <div class="details-actions">
                <button class="share">
                  <ion-icon name="share-social"></ion-icon>
                  <span>Share</span>
                </button>
                <div class="title-wrapper">
                  <p class="title">Prime Video</p>
                  <p class="text">Streaming Channels</p>
                </div>
                <button class="btn btn-primary">
                  <ion-icon name="play"></ion-icon>
                  <span>Watch Now</span>
                </button>
              </div>
              <a href="<%= mv.getPosterUrl() %>" download class="download-btn">
                <span>Download</span>
                <ion-icon name="download-outline"></ion-icon>
              </a>
            </div>
          </div>
        </div>
      </section>

      <!-- #TV SERIES -->
      <section class="tv-series">
        <div class="container">
          <p class="section-subtitle">Best TV Series</p>
          <h2 class="h2 section-title">World Best TV Series</h2>
          <ul class="movies-list">
            <!-- Existing list items... -->
          </ul>
        </div>
      </section>

    </article>
  </main>
  <jsp:include page="/jsp/template/footer.jsp" />

  <a href="#top" class="go-top" data-go-top>
    <ion-icon name="chevron-up"></ion-icon>
  </a>

  <script src="./assets/js/script.js"></script>
  <script type="module" src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.esm.js"></script>
  <script nomodule src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.js"></script>

</body>

</html>