//package controller.auth;
//
//import com.google.gson.Gson;
//import dao.user.UserDAO;
//import util.JwtUtil;
//import util.PasswordUtil;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.regex.Pattern;
//
//@WebServlet(urlPatterns = {"/api/auth/register", "/register"})
//public class RegisterController extends HttpServlet {
//    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        if ("/register".equals(req.getServletPath())) {
//            req.getRequestDispatcher("/jsp/user/register.jsp").forward(req, resp);
//        } else {
//            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
//        }
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setContentType("application/json;charset=UTF-8");
//        String username = req.getParameter("username");
//        String fullName = req.getParameter("fullName");
//        String phone = req.getParameter("phone");
//        String email = req.getParameter("email");
//        String password = req.getParameter("password");
//
//        if (username == null || fullName == null || phone == null || email == null || password == null ||
//            username.isBlank() || fullName.isBlank() || phone.isBlank() || email.isBlank() || password.isBlank()) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            sendJson(resp, Map.of("error", "Missing required fields"));
//            return;
//        }
//        if (!EMAIL_PATTERN.matcher(email).matches()) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            sendJson(resp, Map.of("error", "Invalid email"));
//            return;
//        }
//        boolean ok = UserDAO.createUser(username, fullName, phone, email, PasswordUtil.hash(password));
//        if (!ok) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            sendJson(resp, Map.of("error", "Registration failed"));
//            return;
//        }
//        String token = JwtUtil.generateToken(email);
//        resp.setStatus(HttpServletResponse.SC_CREATED);
//        sendJson(resp, Map.of("token", token));
//    }
//
//    private void sendJson(HttpServletResponse resp, Map<String, Object> data) throws IOException {
//        try (var out = resp.getWriter()) {
//            out.print(new Gson().toJson(data));
//        }
//    }
//}