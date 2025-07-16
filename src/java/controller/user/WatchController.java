package controller.user;

import dao.movie.MovieDAO;
import dao.purchase.PurchaseDAO;
import dao.watch.WatchDAO;
import util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/user/watch/*")
public class WatchController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = extractToken(req);
        String email = JwtUtil.verifyToken(token);
        if (email == null) {
            resp.setStatus(401);
            return;
        }
        int userId = UserDAO.getUserIdByEmail(email);
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) { resp.setStatus(400); return; }
        int filmId = Integer.parseInt(path.substring(1));
        if (!PurchaseDAO.hasAccessToFilm(userId, filmId)) {
            resp.setStatus(403);
            return;
        }
        String videoPath = MovieDAO.getMovieById(filmId).getVideoPath();
        // Integrate with Streaming API or CDN, here mock redirect to video
        // For real, use HLS or DASH for streaming
        resp.sendRedirect("https://cdn.example.com/" + videoPath); // Assume CDN URL
        WatchDAO.log(userId, filmId);
    }

    private String extractToken(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}