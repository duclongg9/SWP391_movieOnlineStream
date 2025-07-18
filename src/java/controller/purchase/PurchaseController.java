package controller.purchase;

import dao.pkg.PackageDAO;
import dao.purchase.PurchaseDAO;
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

@WebServlet("/api/purchase/package/basic/*")
public class PurchaseController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        String email = JwtUtil.verifyToken(token);
        PrintWriter out = resp.getWriter();
        if (email == null) { resp.setStatus(401); out.write("{\"error\":\"unauthorized\"}"); return; }
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) { resp.setStatus(400); return; }
        int packageId = Integer.parseInt(path.substring(1));
        model.Package p = PackageDAO.findById(packageId);
        if (p == null) { resp.setStatus(404); out.write("{\"error\":\"not found\"}"); return; }
        Integer balance = UserDAO.getPointBalance(email);
        if (balance == null || balance < p.getPricePoint()) {
            resp.setStatus(400); out.write("{\"error\":\"insufficient points\"}"); return;
        }
        User u = UserDAO.findByEmail(email);
        boolean ok = UserDAO.addPoints(email, -p.getPricePoint());
        if (ok) {
            PurchaseDAO.addPackagePurchase(u.getId(), p.getId(), p.getDurationDays());
            out.write("{\"status\":\"purchased\"}");
        } else {
            resp.setStatus(500); out.write("{\"error\":\"purchase failed\"}");
        }
    }
}