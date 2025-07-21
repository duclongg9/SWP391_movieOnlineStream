package controller.admin;

import dao.user.UserDAO;
import model.User;
import util.JwtUtil;
import util.PasswordUtil_test;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import util.PasswordUtil;

@WebServlet(urlPatterns = {"/api/admin/login", "/admin/login", "/api/admin/change-password"})
public class AdminLoginController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/admin/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getServletPath();
        PrintWriter out = resp.getWriter();
        if ("/api/admin/login".equals(path)) {
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            User admin = UserDAO.findAdminByUsername(username);
            if (admin != null && PasswordUtil.check(password, admin.getPassword())) {
                String token = JwtUtil.generateToken(admin.getEmail());
                out.write("{\"token\":\"" + token + "\"}");
            } else {
                resp.setStatus(401);
                out.write("{\"error\":\"invalid credentials\"}");
            }
        } else if ("/api/admin/change-password".equals(path)) {
            String token = req.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
            String email = JwtUtil.verifyToken(token);
            if (email == null) {
                resp.setStatus(401);
                out.write("{\"error\":\"unauthorized\"}");
                return;
            }
            User user = UserDAO.findByEmail(email);
            if (user == null || !"admin".equals(user.getRole())) {
                resp.setStatus(403);
                out.write("{\"error\":\"forbidden\"}");
                return;
            }
            String oldPassword = req.getParameter("oldPassword");
            String newPassword = req.getParameter("newPassword");
            if (oldPassword == null || newPassword == null) {
                resp.setStatus(400);
                out.write("{\"error\":\"missing parameters\"}");
                return;
            }
            if (!PasswordUtil.check(oldPassword, user.getPassword())) {
                resp.setStatus(401);
                out.write("{\"error\":\"invalid old password\"}");
                return;
            }
            boolean updated = UserDAO.changePassword(email, PasswordUtil_test.hash(newPassword));
            if (updated) {
                out.write("{\"status\":\"success\"}");
            } else {
                resp.setStatus(500);
                out.write("{\"error\":\"update failed\"}");
            }
        } else {
            resp.setStatus(404);
            out.write("{\"error\":\"not found\"}");
        }
    }
}