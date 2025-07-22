/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import dao.user.UserDAO;
import util.JwtUtil;

import java.io.IOException;

@WebFilter("/api/*")
public class JwtFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getServletPath();

        // Skip auth check for login and SSO endpoints
        if ("/api/auth/login".equals(path)
                || path.startsWith("/api/auth/sso")
                || "/api/admin/login".equals(path)) {
            chain.doFilter(request, response);
            return;
        }


        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) auth = auth.substring(7);
        String email = JwtUtil.verifyToken(auth);
        if (email == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"error\":\"unauthorized\"}");
            return;
        }
        User user = UserDAO.findByEmail(email);
        if (user != null) {
            req.getSession(true).setAttribute("authUser", user);
        }
        chain.doFilter(request, response);
    }
}
