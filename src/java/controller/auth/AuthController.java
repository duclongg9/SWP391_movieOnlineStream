package controller.auth;

import com.google.gson.Gson;
import dao.user.UserDAO;
import model.User;
import util.PasswordUtil;
import util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = {"/api/auth/login", "/api/auth/register"})
public class AuthController extends HttpServlet {

    private static final Gson GSON = new Gson();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/api/auth/login".equals(path)) {
            req.getRequestDispatcher("/jsp/user/login.jsp").forward(req, resp);
        } else if ("/api/auth/register".equals(path)) {
            req.getRequestDispatcher("/jsp/user/register.jsp").forward(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getServletPath();
        String email = req.getParameter("email") != null ? req.getParameter("email").trim() : null;
        String password = req.getParameter("password") != null ? req.getParameter("password").trim() : null;

        if (email == null || password == null || email.isEmpty() || password.isEmpty() || !EMAIL_PATTERN.matcher(email).matches()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJsonResponse(resp, Map.of("error", "Invalid or missing fields"));
            return;
        }

        if ("/api/auth/register".equals(path)) {
            handleRegister(req, resp, email, password);
        } else if ("/api/auth/login".equals(path)) {
            handleLogin(req, resp, email, password);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp, String email, String password) throws IOException {
        boolean success = UserDAO.createUser(email, PasswordUtil.hash(password));
        if (success) {
            String token = JwtUtil.generateToken(email);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            sendJsonResponse(resp, Map.of("token", token));
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJsonResponse(resp, Map.of("error", "Registration failed. Email may already exist."));
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp, String email, String password) throws IOException {
        User user = UserDAO.validateUser(email, password);
        if (user != null) {
            String token = JwtUtil.generateToken(email);
            sendJsonResponse(resp, Map.of("token", token));
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            sendJsonResponse(resp, Map.of("error", "Invalid credentials"));
        }
    }

    private void sendJsonResponse(HttpServletResponse resp, Map<String, Object> data) throws IOException {
        try (var out = resp.getWriter()) {
            out.print(GSON.toJson(data));
        }
    }
}