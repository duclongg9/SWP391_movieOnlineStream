package controller.pkg;

import dao.pkg.PackageDAO;
import model.Package;
import util.SimpleJson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = {"/api/packages", "/packages"})
public class PublicPackageController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/packages".equals(req.getServletPath())) {
            req.getRequestDispatcher("/jsp/user/packages.jsp").forward(req, resp);
            return;
        }
        List<Package> list = PackageDAO.findAll();
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.write(SimpleJson.packagesToJson(list));
    }
}