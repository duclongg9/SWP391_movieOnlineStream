<%-- 
    Document   : upcoming
    Created on : 4 Jul 2025, 14:22:47
    Author     : Dell-PC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<section class="upcoming">
  <div class="container">

    <div class="flex-wrapper">

      <div class="title-wrapper">
        <p class="section-subtitle">Online Streaming</p>

        <h2 class="h2 section-title">Upcoming Movies</h2>
      </div>

      <ul class="filter-list">

        <li>
          <button class="filter-btn">Movies</button>
        </li>

        <li>
          <button class="filter-btn">TV Shows</button>
        </li>

        <li>
          <button class="filter-btn">Anime</button>
        </li>

      </ul>

    </div>

    <ul class="movies-list  has-scrollbar">
      <% @SuppressWarnings("unchecked")
         java.util.List<model.Movie> movies = (java.util.List<model.Movie>)request.getAttribute("movies");
         if (movies != null) {
             for (model.Movie mv : movies) { %>

      <li>
        <div class="movie-card">

            <a href="movie?id=<%= mv.getId() %>">
            <figure class="card-banner">
              <img src="<%= mv.getPosterUrl() %>" alt="<%= mv.getTitle() %> poster">
            </figure>
          </a>

          <div class="title-wrapper">
            <a href="movie?id=<%= mv.getId() %>">
              <h3 class="card-title"><%= mv.getTitle() %></h3>
            </a>
            <time datetime="<%= mv.getYear() %>"><%= mv.getYear() %></time>
          </div>
          <div class="card-meta">
            <div class="badge badge-outline"><%= mv.getQuality() %></div>
            <div class="rating">
              <ion-icon name="star"></ion-icon>
              <data><%= mv.getRating() %></data>
            </div>
          </div>

         <p><%= mv.getGenre() %> - <%= mv.getActor() %></p>
          <p><%= mv.getDescription() %></p>

        </div>
      </li>
      <%   }
         }
      %>
    </ul>

  </div>
</section>
