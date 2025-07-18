package controller.admin;

import dao.pkg.PackageDAO;
import dao.user.UserDAO;
import model.Package;
import util.JwtUtil;
import util.SimpleJson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = {"/api/admin/packages", "/api/admin/package/*", "/admin/packages"})
public class PackageAdminController extends HttpServlet {
    private boolean isAdmin(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        String email = JwtUtil.verifyToken(token);
        return UserDAO.isAdmin(email);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/admin/packages".equals(req.getServletPath())) {
            req.getRequestDispatcher("/jsp/admin/packages.jsp").forward(req, resp);
            return;
        }
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        List<Package> packages = PackageDAO.findAll();
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.write(util.SimpleJson.packagesToJson(packages));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        Package p = new Package();
        p.setName(req.getParameter("name"));
        p.setDescription(req.getParameter("description"));
        p.setDurationDays(Integer.parseInt(req.getParameter("duration")));
        p.setPricePoint(Integer.parseInt(req.getParameter("price")));
        boolean ok = PackageDAO.create(p);
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
        Package p = new Package();
        p.setId(id);
        p.setName(req.getParameter("name"));
        p.setDescription(req.getParameter("description"));
        p.setDurationDays(Integer.parseInt(req.getParameter("duration")));
        p.setPricePoint(Integer.parseInt(req.getParameter("price")));
        boolean ok = PackageDAO.update(p);
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
        boolean ok = PackageDAO.delete(id);
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        if (ok) out.write("{\"status\":\"deleted\"}");
        else { resp.setStatus(500); out.write("{\"error\":\"delete failed\"}"); }
    }
}