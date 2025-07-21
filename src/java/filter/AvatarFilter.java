/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;

@WebFilter("/*")
public class AvatarFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpSession session = httpReq.getSession(false);
        String avatar = null;
        if (session != null) {
            avatar = (String) session.getAttribute("avatarUrl");
            if (avatar == null) {
                User u = (User) session.getAttribute("authUser");
                if (u != null) avatar = u.getProfilePic();
            }
        }
        if (avatar == null || avatar.isBlank()) {
            avatar = req.getServletContext().getContextPath() + "/assets/img/avatar-placeholder.png";
        }
        req.setAttribute("avatarUrl", avatar);
        chain.doFilter(req, res);
    }
}