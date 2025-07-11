package controller.movie;

import dao.movie.MovieDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Movie;

import java.io.IOException;

@WebServlet("/movie")
public class MovieDetailController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);
                Movie movie = MovieDAO.getMovieById(id);
                if (movie != null) {
                    req.setAttribute("movie", movie);
                    req.getRequestDispatcher("/jsp/template/movie_detail.jsp").forward(req, resp);
                    return;
                }
            } catch (NumberFormatException ex) {
                // ignore and fall through to 404
            }
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}