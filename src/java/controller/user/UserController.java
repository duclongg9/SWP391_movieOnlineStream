 package controller.user;

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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = {"/api/user/profile", "/api/user/change-password", "/user/profile"})
public class UserController extends HttpServlet {


    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/user/profile".equals(path)) {
            req.getRequestDispatcher("/jsp/user/profile.jsp").forward(req, resp);
            return;
        }
        if (!"/api/user/profile".equals(path)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        processAuthenticatedRequest(req, resp, email -> {
            User user = UserDAO.findByEmail(email);
            if (user == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendJsonResponse(resp, Map.of("error", "User not found"));
                return;
            }
            sendJsonResponse(resp, Map.of("email", user.getEmail()));
        });
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"/api/user/profile".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        processAuthenticatedRequest(req, resp, email -> {
            String newEmail = req.getParameter("email") != null ? req.getParameter("email").trim() : null;
            if (newEmail == null || newEmail.isEmpty() || !EMAIL_PATTERN.matcher(newEmail).matches()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendJsonResponse(resp, Map.of("error", "Invalid or missing email"));
                return;
            }
            boolean success = UserDAO.updateEmail(email, newEmail);
            if (success) {
                sendJsonResponse(resp, Map.of("email", newEmail));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendJsonResponse(resp, Map.of("error", "Update failed"));
            }
        });
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"/api/user/change-password".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        processAuthenticatedRequest(req, resp, email -> {
            String oldPassword = req.getParameter("oldPassword");
            String newPassword = req.getParameter("newPassword");
            if (oldPassword == null || newPassword == null || oldPassword.isEmpty() || newPassword.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendJsonResponse(resp, Map.of("error", "Missing fields"));
                return;
            }
            User user = UserDAO.validateUser(email, oldPassword);
            if (user == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                sendJsonResponse(resp, Map.of("error", "Invalid old password"));
                return;
            }
            boolean success = UserDAO.changePassword(email, PasswordUtil.hash(newPassword));
            if (success) {
                sendJsonResponse(resp, Map.of("status", "ok"));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendJsonResponse(resp, Map.of("error", "Update failed"));
            }
        });
    }

    private void processAuthenticatedRequest(HttpServletRequest req, HttpServletResponse resp, AuthenticatedAction action) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String token = extractToken(req);
        String email = JwtUtil.verifyToken(token);
        if (email == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            sendJsonResponse(resp, Map.of("error", "Unauthorized"));
            return;
        }
        action.execute(email);
    }

    private interface AuthenticatedAction {
        void execute(String email) throws IOException;
    }

    private String extractToken(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        // Removed token from query parameter for security reasons
        return null;
    }

    private void sendJsonResponse(HttpServletResponse resp, Map<String, Object> data) throws IOException {
        try (var out = resp.getWriter()) {
            out.print(new Gson().toJson(data));
        }
    }
}