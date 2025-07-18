package controller.purchase;

import dao.pkg.PackageDAO;
import dao.promo.PromotionDAO;
import dao.purchase.PurchaseDAO;
import dao.user.UserDAO;
import model.Package;
import model.Promotion;
import model.User;
import util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/purchase/package/*")
public class PackagePurchaseController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        String email = JwtUtil.verifyToken(token);
        PrintWriter out = resp.getWriter();
        if (email == null) {
            resp.setStatus(401);
            out.write("{\"error\":\"unauthorized\"}");
            return;
        }
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) { resp.setStatus(400); return; }
        int pkgId = Integer.parseInt(path.substring(1));
        Package pkg = PackageDAO.findById(pkgId);
        if (pkg == null) { resp.setStatus(404); out.write("{\"error\":\"not found\"}"); return; }
        int price = pkg.getPricePoint();
        String promoCode = req.getParameter("promoCode");
        if (promoCode != null && !promoCode.isEmpty()) {
            Promotion promo = PromotionDAO.findByCode(promoCode);
            if (promo != null && (promo.getValidUntil() == null || promo.getValidUntil().after(new java.util.Date()))) {
                if ("package".equals(promo.getApplyTo()) && (promo.getTargetId() == null || promo.getTargetId() == pkgId)) {
                    price = (int) (price * (1 - promo.getDiscountPct() / 100.0));
                }
            }
        }
        Integer balance = UserDAO.getPointBalance(email);
        if (balance == null || balance < price) {
            resp.setStatus(400); out.write("{\"error\":\"insufficient points\"}"); return;
        }
        User u = UserDAO.findByEmail(email);
        boolean ok = UserDAO.addPoints(email, -price);
        if (ok) {
            PurchaseDAO.addPackagePurchase(u.getId(), pkgId, pkg.getDurationDays());
            out.write("{\"status\":\"purchased\",\"price\":" + price + "}");
        } else {
            resp.setStatus(500); out.write("{\"error\":\"purchase failed\"}");
        }
    }
}