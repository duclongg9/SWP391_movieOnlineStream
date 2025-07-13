package controller.auth;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import com.google.gson.Gson;

@WebServlet("/api/auth/logout")
public class LogoutController extends HttpServlet {



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        sendJsonResponse(resp, Map.of("status", "logged out"));
    }

    private void sendJsonResponse(HttpServletResponse resp, Map<String, Object> data) throws IOException {
        try (var out = resp.getWriter()) {
            out.print(new Gson().toJson(data));
        }
    }
}