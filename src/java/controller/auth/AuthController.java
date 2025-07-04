package controller.auth;

import dao.user.UserDAO;
import model.User;
import util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/api/auth/login", "/api/auth/register"})
public class AuthController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getServletPath();
        PrintWriter out = resp.getWriter();
        if ("/api/auth/register".equals(path)) {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            boolean ok = UserDAO.createUser(email, PasswordUtil.hash(password));
            if (ok) {
                out.write("{\"status\":\"registered\"}");
            } else {
                resp.setStatus(400);
                out.write("{\"error\":\"register failed\"}");
            }
        } else if ("/api/auth/login".equals(path)) {
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            User u = UserDAO.validateUser(email, password);
            if (u != null) {
                String token = java.util.Base64.getEncoder().encodeToString((email + ":" + System.currentTimeMillis()).getBytes());
                out.write("{\"token\":\"" + token + "\"}");
            } else {
                resp.setStatus(401);
                out.write("{\"error\":\"invalid credentials\"}");
            }
        }
    }
}