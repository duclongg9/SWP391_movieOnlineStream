package controller.user;

import dao.user.UserDAO;
import model.User;
import util.JwtUtil;
import util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/api/user/profile", "/api/user/change-password", "/user/profile"})
public class UserController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/user/profile".equals(req.getServletPath())) {
            req.getRequestDispatcher("/jsp/user/profile.jsp").forward(req, resp);
            return;
        }
        if (!"/api/user/profile".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String token = extractToken(req);
        String email = JwtUtil.verifyToken(token);
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        if (email == null) {
            resp.setStatus(401);
            out.write("{\"error\":\"unauthorized\"}");
            return;
        }
        User u = UserDAO.findByEmail(email);
        if (u == null) {
            resp.setStatus(404);
            out.write("{\"error\":\"not found\"}");
            return;
        }
        out.write("{\"email\":\"" + u.getEmail() + "\"}");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"/api/user/profile".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String token = extractToken(req);
        String email = JwtUtil.verifyToken(token);
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        if (email == null) {
            resp.setStatus(401);
            out.write("{\"error\":\"unauthorized\"}");
            return;
        }
        String newEmail = req.getParameter("email");
        if (newEmail == null || newEmail.trim().isEmpty()) {
            resp.setStatus(400);
            out.write("{\"error\":\"missing email\"}");
            return;
        }
        boolean ok = UserDAO.updateEmail(email, newEmail.trim());
        if (ok) {
            out.write("{\"email\":\"" + newEmail.trim() + "\"}");
        } else {
            resp.setStatus(500);
            out.write("{\"error\":\"update failed\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"/api/user/change-password".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String token = extractToken(req);
        String email = JwtUtil.verifyToken(token);
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        if (email == null) {
            resp.setStatus(401);
            out.write("{\"error\":\"unauthorized\"}");
            return;
        }
        String oldPass = req.getParameter("oldPassword");
        String newPass = req.getParameter("newPassword");
        if (oldPass == null || newPass == null || oldPass.isEmpty() || newPass.isEmpty()) {
            resp.setStatus(400);
            out.write("{\"error\":\"missing fields\"}");
            return;
        }
        User u = UserDAO.validateUser(email, oldPass);
        if (u == null) {
            resp.setStatus(401);
            out.write("{\"error\":\"invalid password\"}");
            return;
        }
        boolean ok = UserDAO.changePassword(email, PasswordUtil.hash(newPass));
        if (ok) {
            out.write("{\"status\":\"ok\"}");
        } else {
            resp.setStatus(500);
            out.write("{\"error\":\"update failed\"}");
        }
    }

    private String extractToken(HttpServletRequest req) {
        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return req.getParameter("token");
    }
}