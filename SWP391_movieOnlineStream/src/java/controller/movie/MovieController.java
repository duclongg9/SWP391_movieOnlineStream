package controller.movie;

import dao.movie.MovieDAO;
import java.io.IOException;
import java.util.List;
import.jakarta.servlet.ServletException;
import.jakarta.servlet.annotation.WebServlet;
import.jakarta.servlet.http.HttpServlet;
import.jakarta.servlet.http.HttpServletRequest;
import.jakarta.servlet.http.HttpServletResponse;
import model.Movie;

@WebServlet("/movies")
public class MovieController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Movie> movies = MovieDAO.getUpcomingMovies();
        req.setAttribute("movies", movies);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}