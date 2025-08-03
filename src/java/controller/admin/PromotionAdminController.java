package controller.admin;

import dao.promo.PromotionDAO;
import dao.user.UserDAO;
import model.Promotion;
import util.JwtUtil;
import util.SimpleJson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/admin/promotions", "/api/admin/promotion/*", "/admin/promotions"})
public class PromotionAdminController extends HttpServlet {
    private boolean isAdmin(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        String email = JwtUtil.verifyToken(token);
        if (email == null) return false;
        return UserDAO.isAdmin(email);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if ("PUT".equalsIgnoreCase(method)) {
            doPut(req, resp);
        } else if ("DELETE".equalsIgnoreCase(method)) {
            doDelete(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/admin/promotions".equals(req.getServletPath())) {
            req.getRequestDispatcher("/jsp/admin/promotions.jsp").forward(req, resp);
            return;
        }
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        int page = 1;
        int size = 10;
        try { page = Integer.parseInt(req.getParameter("page")); } catch (Exception e) {}
        try { size = Integer.parseInt(req.getParameter("size")); } catch (Exception e) {}
        List<Promotion> list = PromotionDAO.findPage((page-1)*size, size);
        int total = PromotionDAO.count();
        java.util.List<java.util.Map<String,Object>> arr = new java.util.ArrayList<>();
        for (Promotion p : list) {
            arr.add(java.util.Map.of(
                "id", p.getId(),
                "code", p.getCode(),
                "discountPct", p.getDiscountPct(),
                "applyTo", p.getApplyTo(),
                "targetType", p.getTargetType(),
                "targetId", p.getTargetId(),
                "validUntil", p.getValidUntil() == null ? null : p.getValidUntil().toString()
            ));
        }
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.write("{\"total\":"+total+",\"page\":"+page+",\"promotions\":"+SimpleJson.listToJson(arr)+"}");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        Promotion p = buildPromotion(req);
        boolean ok = PromotionDAO.create(p);
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        if (ok) {
            out.write("{\"status\":\"created\"}");
        } else {
            resp.setStatus(500);
            out.write("{\"error\":\"create failed\"}");
        }
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) { resp.setStatus(400); return; }
        int id;
        try { id = Integer.parseInt(path.substring(1)); } catch (NumberFormatException e) { resp.setStatus(400); return; }
        Promotion p = buildPromotion(req);
        p.setId(id);
        boolean ok = PromotionDAO.update(p);
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        if (ok) {
            out.write("{\"status\":\"updated\"}");
        } else {
            resp.setStatus(500);
            out.write("{\"error\":\"update failed\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) { resp.setStatus(400); return; }
        int id;
        try { id = Integer.parseInt(path.substring(1)); } catch (NumberFormatException e) { resp.setStatus(400); return; }
        boolean ok = PromotionDAO.delete(id);
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        if (ok) {
            out.write("{\"status\":\"deleted\"}");
        } else {
            resp.setStatus(500);
            out.write("{\"error\":\"delete failed\"}");
        }
    }

    private Promotion buildPromotion(HttpServletRequest req) {
        Promotion p = new Promotion();
        p.setCode(req.getParameter("code"));
        try { p.setDiscountPct(Double.parseDouble(req.getParameter("discountPct"))); } catch (Exception e) { p.setDiscountPct(0); }
        p.setApplyTo(req.getParameter("applyTo"));
        p.setTargetType(req.getParameter("targetType"));
        String tid = req.getParameter("targetId");
        if (tid != null && !tid.isEmpty()) {
            try { p.setTargetId(Integer.parseInt(tid)); } catch (NumberFormatException e) { p.setTargetId(null); }
        }
        String vu = req.getParameter("validUntil");
        if (vu != null && !vu.isEmpty()) {
            try { p.setValidUntil(Timestamp.from(Instant.parse(vu))); } catch (Exception e) { /* ignore */ }
        }
        return p;
    }
}