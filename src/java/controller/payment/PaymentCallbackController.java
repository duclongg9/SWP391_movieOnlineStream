package controller.payment;

import dao.payment.TransactionDAO;
import dao.user.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import util.JwtUtil;

@WebServlet("/api/payment/callback")
public class PaymentCallbackController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String code = req.getParameter("vnp_ResponseCode");
        String amountStr = req.getParameter("amount");
        String email = req.getParameter("email");
        if (code == null || amountStr == null || email == null) {
            resp.setStatus(400);
            out.write("{\"error\":\"missing params\"}");
            return;
        }
        if (!"00".equals(code)) {
            out.write("{\"status\":\"failed\"}");
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
        int points = amount / 1000;
        boolean ok = UserDAO.addPoints(email, points);
        if (ok) {
            model.User u = UserDAO.findByEmail(email);
            if (u != null) TransactionDAO.logTransaction(u.getId(), amount, points, "SUCCESS", "topup");
            String token = JwtUtil.generateToken(email);
            out.write("{\"token\":\"" + token + "\",\"pointAdded\":" + points + "}");
        } else {
            resp.setStatus(500);
            out.write("{\"error\":\"update failed\"}");
        }
    }
}