package controller.auth;

import dao.user.UserDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;


@WebServlet(urlPatterns = {"/api/auth/login", "/login"})
public class LoginController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/user/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            req.setAttribute("msg", "Missing email or password");
            req.getRequestDispatcher("/jsp/user/login.jsp").forward(req, resp);
            return;
        }

        User user = UserDAO.validateUser(email, password);
        if (user == null) {
            req.setAttribute("msg", "Invalid credentials");
            req.getRequestDispatcher("/jsp/user/login.jsp").forward(req, resp);
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("loggedInUser", user);
        session.setAttribute("email", user.getEmail());
        session.setAttribute("username", user.getUsername());

        Cookie emailCookie = new Cookie("email", user.getEmail());
        emailCookie.setMaxAge(24 * 60 * 60);
        emailCookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
        resp.addCookie(emailCookie);

        if (user.getUsername() != null) {
            Cookie userCookie = new Cookie("username", user.getUsername());
            userCookie.setMaxAge(24 * 60 * 60);
            userCookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
            resp.addCookie(userCookie);
        }

        resp.sendRedirect(req.getContextPath() + "/index.jsp");
    }
}
