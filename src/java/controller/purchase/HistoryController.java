package controller.purchase;

import dao.purchase.PurchaseDAO;
import dao.user.UserDAO;
import model.Purchase;
import model.User;
import util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = {"/api/purchase/history", "/history"})
public class HistoryController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/history".equals(req.getServletPath())) {
            req.getRequestDispatcher("/jsp/user/history.jsp").forward(req, resp);
            return;
        }

        resp.setContentType("application/json;charset=UTF-8");
        String token = extractToken(req);
        String email = JwtUtil.verifyToken(token);
        PrintWriter out = resp.getWriter();
        if (email == null) {
            resp.setStatus(401);
            out.write("{\"error\":\"unauthorized\"}");
            return;
        }
        User u = UserDAO.findByEmail(email);
        if (u == null) {
            resp.setStatus(404);
            out.write("{\"error\":\"not found\"}");
            return;
        }
        List<Purchase> list = PurchaseDAO.listByUser(u.getId());
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i=0;i<list.size();i++) {
            Purchase p = list.get(i);
            sb.append('{');
            if (p.getFilmId() != null) sb.append("\"filmId\":").append(p.getFilmId()).append(',');
            if (p.getPackageId() != null) sb.append("\"packageId\":").append(p.getPackageId()).append(',');
            sb.append("\"purchasedAt\":\"").append(p.getPurchasedAt()).append("\"");
            if (p.getExpiredAt() != null) sb.append(",\"expiredAt\":\"").append(p.getExpiredAt()).append("\"");
            sb.append('}');
            if (i < list.size()-1) sb.append(',');
        }
        sb.append(']');
        out.write(sb.toString());
    }

    private String extractToken(HttpServletRequest req) {
        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return req.getParameter("token");
    }
}