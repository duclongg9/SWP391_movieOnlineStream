package controller.point;

import dao.user.UserDAO;
import util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/point/balance")
public class PointController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String token = extractToken(req);
        String email = JwtUtil.verifyToken(token);
        PrintWriter out = resp.getWriter();
        if (email == null) {
            resp.setStatus(401);
            out.write("{\"error\":\"unauthorized\"}");
            return;
        }
        Integer balance = UserDAO.getPointBalance(email);
        if (balance == null) {
            resp.setStatus(500);
            out.write("{\"error\":\"cannot read balance\"}");
        } else {
            out.write("{\"balance\":" + balance + "}");
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