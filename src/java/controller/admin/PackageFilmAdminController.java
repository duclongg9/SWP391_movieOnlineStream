package controller.admin;

import dao.pkg.PackageFilmDAO;
import util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/api/admin/package-film"})
public class PackageFilmAdminController extends HttpServlet {
    private boolean isAdmin(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        String email = JwtUtil.verifyToken(token);
        return "admin@example.com".equals(email);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        int filmId = Integer.parseInt(req.getParameter("filmId"));
        int packageId = Integer.parseInt(req.getParameter("packageId"));
        boolean ok = PackageFilmDAO.addFilmToPackage(filmId, packageId);
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        if (ok) out.write("{\"status\":\"added\"}");
        else { resp.setStatus(500); out.write("{\"error\":\"failed\"}"); }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        int filmId = Integer.parseInt(req.getParameter("filmId"));
        int packageId = Integer.parseInt(req.getParameter("packageId"));
        boolean ok = PackageFilmDAO.removeFilmFromPackage(filmId, packageId);
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        if (ok) out.write("{\"status\":\"removed\"}");
        else { resp.setStatus(500); out.write("{\"error\":\"failed\"}"); }
    }
}
