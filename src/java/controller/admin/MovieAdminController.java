package controller.admin;

import dao.user.UserDAO;
import dao.movie.MovieDAO;
import model.Movie;
import util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = {"/api/admin/movies", "/api/admin/movie/*", "/admin/movies"})
public class MovieAdminController extends HttpServlet {
    private boolean isAdmin(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        String email = JwtUtil.verifyToken(token);
        return UserDAO.isAdmin(email);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/admin/movies".equals(req.getServletPath())) {
            req.getRequestDispatcher("/jsp/admin/movies.jsp").forward(req, resp);
            return;
        }
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        List<Movie> movies = MovieDAO.findAll();
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.write(toJson(movies)); // Assume toJson method
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        Movie m = new Movie();
        m.setTitle(req.getParameter("title"));
        m.setGenre(req.getParameter("genre"));
        m.setActor(req.getParameter("actor"));
        m.setVideoPath(req.getParameter("videoPath"));
        m.setDescription(req.getParameter("description"));
        m.setPricePoint(Integer.parseInt(req.getParameter("price")));
        boolean ok = MovieDAO.create(m);
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        if (ok) out.write("{\"status\":\"created\"}");
        else { resp.setStatus(500); out.write("{\"error\":\"create failed\"}"); }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) { resp.setStatus(400); return; }
        int id = Integer.parseInt(path.substring(1));
        Movie m = new Movie();
        m.setId(id);
        m.setTitle(req.getParameter("title"));
        m.setGenre(req.getParameter("genre"));
        m.setActor(req.getParameter("actor"));
        m.setVideoPath(req.getParameter("videoPath"));
        m.setDescription(req.getParameter("description"));
        m.setPricePoint(Integer.parseInt(req.getParameter("price")));
        boolean ok = MovieDAO.update(m);
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        if (ok) out.write("{\"status\":\"updated\"}");
        else { resp.setStatus(500); out.write("{\"error\":\"update failed\"}"); }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) { resp.setStatus(400); return; }
        int id = Integer.parseInt(path.substring(1));
        boolean ok = MovieDAO.softDelete(id);
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        if (ok) out.write("{\"status\":\"deleted\"}");
        else { resp.setStatus(500); out.write("{\"error\":\"delete failed\"}"); }
    }
}