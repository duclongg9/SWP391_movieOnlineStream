package controller.purchase;

import dao.movie.MovieDAO;
import dao.purchase.PurchaseDAO;
import dao.user.UserDAO;
import model.Movie;
import model.User;
import util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/purchase/movie/*")
public class MoviePurchaseController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        String email = JwtUtil.verifyToken(token);
        PrintWriter out = resp.getWriter();
        if (email == null) {
            resp.setStatus(401);
            out.write("{\"error\":\"unauthorized\"}");
            return;
        }
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) { resp.setStatus(400); return; }
        int filmId = Integer.parseInt(path.substring(1));
        Movie mv = MovieDAO.getMovieById(filmId);
        if (mv == null) { resp.setStatus(404); out.write("{\"error\":\"not found\"}"); return; }
        Integer balance = UserDAO.getPointBalance(email);
        if (balance == null || balance < mv.getPricePoint()) {
            resp.setStatus(400); out.write("{\"error\":\"insufficient points\"}"); return;
        }
        User u = UserDAO.findByEmail(email);
        boolean ok = UserDAO.addPoints(email, -mv.getPricePoint());
        if (ok) {
            PurchaseDAO.addMoviePurchase(u.getId(), filmId);
            out.write("{\"status\":\"purchased\"}");
        } else {
            resp.setStatus(500); out.write("{\"error\":\"purchase failed\"}");
        }
    }
}