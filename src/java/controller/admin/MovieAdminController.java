package controller.admin;

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

@WebServlet(urlPatterns = {"/api/admin/movie", "/api/admin/movie/*"})
public class MovieAdminController extends HttpServlet {
    private boolean isAdmin(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        String email = JwtUtil.verifyToken(token);
        return "admin@example.com".equals(email);
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
        try { m.setPricePoint(Integer.parseInt(req.getParameter("price"))); } catch(Exception e){ m.setPricePoint(0); }
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
        try { m.setPricePoint(Integer.parseInt(req.getParameter("price"))); } catch(Exception e){ m.setPricePoint(0); }
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