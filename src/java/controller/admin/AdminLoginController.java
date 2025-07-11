package controller.admin;

import util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/admin/login")
public class AdminLoginController extends HttpServlet {
    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final String ADMIN_PASS = "admin123";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        PrintWriter out = resp.getWriter();
        if (ADMIN_EMAIL.equals(email) && ADMIN_PASS.equals(password)) {
            String token = JwtUtil.generateToken(email);
            out.write("{\"token\":\"" + token + "\"}");
        } else {
            resp.setStatus(401);
            out.write("{\"error\":\"invalid credentials\"}");
        }
    }
}