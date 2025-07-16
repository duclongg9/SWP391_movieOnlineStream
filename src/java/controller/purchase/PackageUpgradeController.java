package controller.purchase;

import dao.pkg.PackageDAO;
import dao.purchase.PurchaseDAO;
import dao.user.UserDAO;
import model.Package;
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
import java.time.Duration;
import java.time.Instant;

@WebServlet("/api/purchase/upgrade/*")
public class PackageUpgradeController extends HttpServlet {
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
        int newPkgId = Integer.parseInt(path.substring(1));
        Package newPkg = PackageDAO.findById(newPkgId);
        if (newPkg == null) { resp.setStatus(404); out.write("{\"error\":\"not found\"}"); return; }
        User u = UserDAO.findByEmail(email);
        Purchase current = PurchaseDAO.getActivePackage(u.getId());
        if (current == null) { resp.setStatus(400); out.write("{\"error\":\"no active package\"}"); return; }
        Package oldPkg = PackageDAO.findById(current.getPackageId());
        if (oldPkg == null) { resp.setStatus(500); out.write("{\"error\":\"data error\"}"); return; }
        long remaining = Duration.between(Instant.now(), current.getExpiredAt().toInstant()).toDays();
        if (remaining < 0) remaining = 0;
        double credit = (double) oldPkg.getPricePoint() * remaining / oldPkg.getDurationDays();
        int cost = (int) Math.max(0, Math.round(newPkg.getPricePoint() - credit));
        Integer balance = UserDAO.getPointBalance(email);
        if (balance == null || balance < cost) { resp.setStatus(400); out.write("{\"error\":\"insufficient points\"}"); return; }
        boolean ok = true;
        if (cost > 0) ok = UserDAO.addPoints(email, -cost);
        if (ok) {
            PurchaseDAO.expire(current.getId());
            PurchaseDAO.addPackagePurchase(u.getId(), newPkgId, newPkg.getDurationDays());
            out.write("{\"status\":\"upgraded\",\"cost\":" + cost + "}");
        } else {
            resp.setStatus(500); out.write("{\"error\":\"upgrade failed\"}");
        }
    }
}