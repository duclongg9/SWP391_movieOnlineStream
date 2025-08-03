package controller.admin;

import dao.user.UserDAO;
import model.User;
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

@WebServlet(urlPatterns = {"/api/admin/users", "/api/admin/user/*", "/admin/users"})
public class UserAdminController extends HttpServlet {
    private boolean isAdmin(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        String email = JwtUtil.verifyToken(token);
        return UserDAO.isAdmin(email);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/admin/users".equals(req.getServletPath())) {
            req.getRequestDispatcher("/jsp/admin/users.jsp").forward(req, resp);
            return;
        }
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        int page = 1;
        int size = 10;
        try { page = Integer.parseInt(req.getParameter("page")); } catch (Exception e) {}
        try { size = Integer.parseInt(req.getParameter("size")); } catch (Exception e) {}
        List<User> users = UserDAO.findPage((page-1)*size, size);
        int total = UserDAO.count();
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.write("{\"total\":"+total+",\"page\":"+page+",\"users\":"+SimpleJson.usersToJson(users)+"}");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) { resp.setStatus(400); return; }
        int id = Integer.parseInt(path.substring(1));
        String action = req.getParameter("action");
        boolean ok = false;
        if ("lock".equals(action)) {
            ok = UserDAO.lockUser(id, true);
        } else if ("unlock".equals(action)) {
            ok = UserDAO.lockUser(id, false);
        }
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        if (ok) out.write("{\"status\":\"updated\"}");
        else { resp.setStatus(500); out.write("{\"error\":\"update failed\"}"); }
    }
}