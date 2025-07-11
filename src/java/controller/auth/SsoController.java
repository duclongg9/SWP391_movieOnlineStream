package controller.auth;

import dao.user.UserDAO;
import util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/api/auth/sso/google", "/api/auth/sso/facebook"})
public class SsoController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String email = req.getParameter("email");
        if (email == null) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"missing email\"}");
            return;
        }
        String provider = "google";
        if (req.getServletPath().contains("facebook")) provider = "facebook";
        if (UserDAO.findByEmail(email) == null) {
            UserDAO.createUserSso(email, provider);
        }
        String token = JwtUtil.generateToken(email);
        PrintWriter out = resp.getWriter();
        out.write("{\"token\":\"" + token + "\"}");
    }
}