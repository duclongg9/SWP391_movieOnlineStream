package controller.auth;

import com.google.gson.Gson;
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
import java.util.Map;

@WebServlet(urlPatterns = {"/api/auth/login", "/login"})
public class LoginController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/login".equals(req.getServletPath())) {
            req.getRequestDispatcher("/jsp/user/login.jsp").forward(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJson(resp, Map.of("error", "Missing email or password"));
            return;
        }
        User user = UserDAO.validateUser(email, password);
        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            sendJson(resp, Map.of("error", "Invalid credentials"));
            return;
        }
        String token = JwtUtil.generateToken(email);
        sendJson(resp, Map.of("token", token));
    }

    private void sendJson(HttpServletResponse resp, Map<String, Object> data) throws IOException {
        try (var out = resp.getWriter()) {
            out.print(new Gson().toJson(data));
        }
    }
}