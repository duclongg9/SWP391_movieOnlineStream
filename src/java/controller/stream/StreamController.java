package controller.stream;

import dao.purchase.PurchaseDAO;
import dao.user.UserDAO;
import dao.watch.WatchDAO;
import util.JwtUtil;
import dao.movie.MovieDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/api/stream/url", "/api/stream/watchlog"})
public class StreamController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"/api/stream/url".equals(req.getServletPath())) { resp.sendError(404); return; }
        resp.setContentType("application/json;charset=UTF-8");
        String token = extractToken(req);
        String email = JwtUtil.verifyToken(token);
        PrintWriter out = resp.getWriter();
        if (email == null) { resp.setStatus(401); out.write("{\"error\":\"unauthorized\"}"); return; }
        String movieIdStr = req.getParameter("movieId");
        int movieId;
        try { movieId = Integer.parseInt(movieIdStr); } catch(Exception e){ resp.setStatus(400); out.write("{\"error\":\"invalid id\"}"); return; }
        int userId = UserDAO.getUserIdByEmail(email);
        if (!PurchaseDAO.hasAccessToFilm(userId, movieId)) { resp.setStatus(403); out.write("{\"error\":\"no access\"}"); return; }
        String videoUrl = MovieDAO.getMovieById(movieId).getVideoPath();
        out.write("{\"url\":\"" + videoUrl + "\"}");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"/api/stream/watchlog".equals(req.getServletPath())) { resp.sendError(404); return; }
        resp.setContentType("application/json;charset=UTF-8");
        String token = extractToken(req);
        String email = JwtUtil.verifyToken(token);
        PrintWriter out = resp.getWriter();
        if (email == null) { resp.setStatus(401); out.write("{\"error\":\"unauthorized\"}"); return; }
        String movieIdStr = req.getParameter("movieId");
        int movieId;
        try { movieId = Integer.parseInt(movieIdStr); } catch(Exception e){ resp.setStatus(400); out.write("{\"error\":\"invalid id\"}"); return; }
        int userId = UserDAO.getUserIdByEmail(email);
        WatchDAO.log(userId, movieId);
        out.write("{\"status\":\"logged\"}");
    }

    private String extractToken(HttpServletRequest req) {
        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return req.getParameter("token");
    }
}