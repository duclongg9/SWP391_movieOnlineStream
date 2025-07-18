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
import java.util.HashMap;
import java.util.Map;


@WebServlet(urlPatterns = {"/api/user/profile", "/api/user/change-password", "/user/profile"})
public class UserController extends HttpServlet {




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
            Map<String,Object> data = new HashMap<>();
            data.put("email", user.getEmail());
            data.put("fullName", user.getFullName());
            data.put("phone", user.getPhone());
            data.put("phoneVerified", user.isPhoneVerified());
            sendJsonResponse(resp, data);
        });
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"/api/user/profile".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        processAuthenticatedRequest(req, resp, email -> {
            String phone = req.getParameter("phone") != null ? req.getParameter("phone").trim() : null;
            if (phone == null || phone.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendJsonResponse(resp, Map.of("error", "Missing phone"));
                return;
            }
            boolean success = UserDAO.updatePhone(email, phone);
            if (success) {
                sendJsonResponse(resp, Map.of("phone", phone));
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
            out.print(util.SimpleJson.toJson(data));
        }
    }
}