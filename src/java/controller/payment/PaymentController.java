package controller.payment;

import dao.payment.TransactionDAO;
import dao.user.UserDAO;
import model.User;
import util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/payment/topup")
public class PaymentController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String token = extractToken(req);
        String email = JwtUtil.verifyToken(token);
        PrintWriter out = resp.getWriter();
        if (email == null) {
            resp.setStatus(401);
            out.write("{\"error\":\"unauthorized\"}");
            return;
        }
        String amountStr = req.getParameter("amount");
        if (amountStr == null) {
            resp.setStatus(400);
            out.write("{\"error\":\"missing amount\"}");
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            out.write("{\"error\":\"invalid amount\"}");
            return;
        }
        User u = UserDAO.findByEmail(email);
        if (u == null) {
            resp.setStatus(404);
            out.write("{\"error\":\"not found\"}");
            return;
        }
        int points = amount / 1000;
        boolean ok = UserDAO.addPoints(email, points);
        if (ok) {
            TransactionDAO.logTransaction(u.getId(), amount, points, "SUCCESS");
            out.write("{\"pointAdded\":" + points + "}");
        } else {
            resp.setStatus(500);
            out.write("{\"error\":\"update failed\"}");
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