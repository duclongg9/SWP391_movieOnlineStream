package controller.user;

import dao.user.UserDAO;
import model.User;
import util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Random;

@WebServlet(urlPatterns = {"/api/user/send-otp", "/api/user/verify-otp"})
public class OtpController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/api/user/send-otp".equals(path)) {
            sendOtp(req, resp);
        } else if ("/api/user/verify-otp".equals(path)) {
            verifyOtp(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void sendOtp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String token = extractToken(req);
        String email = JwtUtil.verifyToken(token);
        if (email == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"unauthorized\"}");
            return;
        }
        User user = UserDAO.findByEmail(email);
        if (user == null || user.getPhone() == null || user.getPhone().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"missing phone\"}");
            return;
        }
        String otp = String.format("%06d", new Random().nextInt(1000000));
        Timestamp expire = new Timestamp(System.currentTimeMillis() + 5*60*1000);
        UserDAO.updateOtp(email, otp, expire);
        // Simulate sending SMS
        System.out.println("Send OTP " + otp + " to phone " + user.getPhone());
        resp.getWriter().write("{\"status\":\"sent\"}");
    }

    private void verifyOtp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String token = extractToken(req);
        String email = JwtUtil.verifyToken(token);
        if (email == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"unauthorized\"}");
            return;
        }
        String code = req.getParameter("code");
        if (code == null || code.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"missing code\"}");
            return;
        }
        User user = UserDAO.findByEmail(email);
        if (user == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"user not found\"}");
            return;
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (user.getOtpCode() != null && user.getOtpCode().equals(code)
                && user.getOtpExpire() != null && user.getOtpExpire().after(now)) {
            UserDAO.setPhoneVerified(email, true);
            UserDAO.clearOtp(email);
            resp.getWriter().write("{\"verified\":true}");
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"invalid otp\"}");
        }
    }

    private String extractToken(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}