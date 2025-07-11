package controller.movie;

import dao.movie.MovieDAO;
import model.Movie;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/movie/search")
public class SearchController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String q = req.getParameter("q");
        if (q == null) q = "";
        List<Movie> list = MovieDAO.searchByKeyword(q);
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i=0;i<list.size();i++) {
            Movie m = list.get(i);
            sb.append('{')
              .append("\"id\":").append(m.getId()).append(',')
              .append("\"title\":\"").append(m.getTitle()).append("\",")
              .append("\"price\":").append(m.getPricePoint())
              .append('}');
            if (i < list.size()-1) sb.append(',');
        }
        sb.append(']');
        PrintWriter out = resp.getWriter();
        out.write(sb.toString());
    }
}