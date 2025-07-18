package controller.admin;

import dao.report.ReportDAO;
import util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/admin/report")
public class ReportController extends HttpServlet {
    private boolean isAdmin(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) token = token.substring(7);
        String email = JwtUtil.verifyToken(token);
        return "admin@example.com".equals(email); // Update to use UserDAO
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/admin/report".equals(req.getServletPath())) {
            req.getRequestDispatcher("/jsp/admin/report.jsp").forward(req, resp);
            return;
        }
        if (!isAdmin(req)) { resp.setStatus(401); return; }
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        if (from == null || to == null) {
            from = "1970-01-01";
            to = "2100-01-01";
        }
        int total = ReportDAO.totalPointEarned(from + " 00:00:00", to + " 23:59:59");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.write("{\"totalPoint\":" + total + "}");
    }
}