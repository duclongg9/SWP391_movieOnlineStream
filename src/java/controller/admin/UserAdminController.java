package controller.admin;


import dao.user.UserDAO;
import model.User;
import util.JwtUtil;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = {"/api/admin/users", "/api/admin/user/*", "/admin/users"})
public class UserAdminController extends HttpServlet {


    private boolean isAdmin(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String email = JwtUtil.verifyToken(token);
        if (email == null) return false;
        User u = UserDAO.findByEmail(email);
        return u != null && "admin".equals(u.getRole());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else if ("DELETE".equalsIgnoreCase(req.getMethod())) {
            doDelete(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/admin/users".equals(req.getServletPath())) {
            req.getRequestDispatcher("/jsp/admin/users.jsp").forward(req, resp);
            return;
        }
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        List<User> list = UserDAO.findAll();
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.write(util.SimpleJson.usersToJson(list));
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) { resp.setStatus(400); return; }
        String[] parts = path.substring(1).split("/");
        if (parts.length != 2) { resp.setStatus(400); return; }
        int id;
        try { id = Integer.parseInt(parts[0]); } catch (NumberFormatException e) { resp.setStatus(400); return; }
        boolean lock = "lock".equals(parts[1]);
        boolean unlock = "unlock".equals(parts[1]);
        if (!lock && !unlock) { resp.setStatus(404); return; }
        boolean ok = UserDAO.setLocked(id, lock);
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        if (ok) {
            out.write("{\"status\":\"" + (lock ? "locked" : "unlocked") + "\"}");
        } else {
            resp.setStatus(500);
            out.write("{\"error\":\"update failed\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) { resp.setStatus(400); return; }
        int id;
        try { id = Integer.parseInt(path.substring(1)); } catch (NumberFormatException e) { resp.setStatus(400); return; }
        boolean ok = UserDAO.softDelete(id);
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        if (ok) {
            out.write("{\"status\":\"deleted\"}");
        } else {
            resp.setStatus(500);
            out.write("{\"error\":\"delete failed\"}");
        }
    }
}
