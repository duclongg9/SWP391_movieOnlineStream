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

@WebServlet("/api/point/convert")
public class ConvertController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        String email = JwtUtil.verifyToken(token);
        PrintWriter out = resp.getWriter();
        if (email == null) { resp.setStatus(401); out.write("{\"error\":\"unauthorized\"}"); return; }
        String amountStr = req.getParameter("amount");
        try {
            int amount = Integer.parseInt(amountStr);
            int points = amount / util.Constants.VND_PER_POINT;
            boolean ok = UserDAO.addPoints(email, points);
            if (ok) out.write("{\"pointAdded\":" + points + "}");
            else { resp.setStatus(500); out.write("{\"error\":\"update failed\"}"); }
        } catch (Exception e) {
            resp.setStatus(400); out.write("{\"error\":\"invalid amount\"}");
        }
    }
}