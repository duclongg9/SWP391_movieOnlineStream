package controller.admin;

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

@WebServlet(urlPatterns = {"/api/admin/login", "/admin/login"})
public class AdminLoginController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/admin/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        PrintWriter out = resp.getWriter();
        User admin = UserDAO.findAdminByUsername(username);
        if (admin != null && admin.getPassword().equals(PasswordUtil.hash(password))) {
            String token = JwtUtil.generateToken(admin.getEmail());
            out.write("{\"token\":\"" + token + "\"}");
        } else {
            resp.setStatus(401);
            out.write("{\"error\":\"invalid credentials\"}");
        }
    }
}