package controller.auth;

import dao.user.UserDAO;
import util.JwtUtil;
import util.HttpUtil;
import util.SimpleJson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/api/auth/sso/google/callback",
        "/api/auth/sso/facebook/callback"
})
public class SsoCallbackController extends HttpServlet {
    private static final String OAUTH_STATE = "oauth_state";
    private static final String GOOGLE_CLIENT_ID =
            System.getenv().getOrDefault("GOOGLE_CLIENT_ID", "142934635795-2bv77h97iq762n671qclb69f8b3dkf52.apps.googleusercontent.com"); // Updated from new JSON
    private static final String GOOGLE_CLIENT_SECRET =
            System.getenv().getOrDefault("GOOGLE_CLIENT_SECRET", "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"); // Updated from new JSON
    private static final String GOOGLE_REDIRECT_URI =
            System.getenv().getOrDefault("GOOGLE_REDIRECT_URI",
                    "http://localhost:9999/SWP391_movieOnlineStream/api/auth/sso/google/callback");

    private static final String FACEBOOK_CLIENT_ID =
            System.getenv().getOrDefault("FACEBOOK_CLIENT_ID", "1044612457872751");
    private static final String FACEBOOK_CLIENT_SECRET =
            System.getenv().getOrDefault("FACEBOOK_CLIENT_SECRET", "");
    private static final String FACEBOOK_REDIRECT_URI =
            System.getenv().getOrDefault("FACEBOOK_REDIRECT_URI",
                    "http://localhost:9999/SWP391_movieOnlineStream/api/auth/sso/facebook/callback");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String state = req.getParameter("state");
        String code = req.getParameter("code");
        String sessionState = (String) req.getSession().getAttribute(OAUTH_STATE);

        if (state == null || !state.equals(sessionState) || code == null || code.isBlank()) {
            renderErrorPage(resp, req.getContextPath(), "Invalid SSO callback");
            return;
        }

        String provider = req.getServletPath().contains("google") ? "google" : "facebook";
        req.getSession().removeAttribute(OAUTH_STATE);
        String email = null;
        String name = null;
        try {
            if ("google".equals(provider)) {
                System.out.println("=== DEBUG: Starting Google token exchange with code: " + code);
                String tokenJson = HttpUtil.postForm(
                        "https://oauth2.googleapis.com/token",
                        Map.of(
                                "code", code,
                                "client_id", GOOGLE_CLIENT_ID,
                                "client_secret", GOOGLE_CLIENT_SECRET,
                                "redirect_uri", GOOGLE_REDIRECT_URI,
                                "grant_type", "authorization_code"
                        ));
                System.out.println("=== DEBUG: Token JSON: " + tokenJson);
                String accessToken = SimpleJson.getString(tokenJson, "access_token");
                if (accessToken != null) {
                    System.out.println("=== DEBUG: Access Token: " + accessToken);
                    String userJson = HttpUtil.get(
                            "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" +
                                    java.net.URLEncoder.encode(accessToken, java.nio.charset.StandardCharsets.UTF_8));
                    System.out.println("=== DEBUG: User JSON: " + userJson);
                    email = SimpleJson.getString(userJson, "email");
                    name = SimpleJson.getString(userJson, "name");
                } else {
                    System.out.println("=== DEBUG: Access Token null - Kiểm tra tokenJson");
                }
            } else {
                // Facebook giữ nguyên
                String tokenJson = HttpUtil.get(
                        "https://graph.facebook.com/v10.0/oauth/access_token" +
                                "?client_id=" + FACEBOOK_CLIENT_ID +
                                "&redirect_uri=" + java.net.URLEncoder.encode(FACEBOOK_REDIRECT_URI, java.nio.charset.StandardCharsets.UTF_8) +
                                "&client_secret=" + FACEBOOK_CLIENT_SECRET +
                                "&code=" + java.net.URLEncoder.encode(code, java.nio.charset.StandardCharsets.UTF_8));
                String accessToken = SimpleJson.getString(tokenJson, "access_token");
                if (accessToken != null) {
                    String userJson = HttpUtil.get(
                            "https://graph.facebook.com/me?fields=email,name&access_token=" +
                                    java.net.URLEncoder.encode(accessToken, java.nio.charset.StandardCharsets.UTF_8));
                    email = SimpleJson.getString(userJson, "email");
                    name = SimpleJson.getString(userJson, "name");
                }
            }
        } catch (IOException e) {
            System.err.println("=== ERROR: SSO request failed: " + e.getMessage());
            e.printStackTrace();
            renderErrorPage(resp, req.getContextPath(), "SSO request failed: " + e.getMessage());
            return;
        }

        if (email == null || email.isBlank()) {
            renderErrorPage(resp, req.getContextPath(), "Unable to retrieve user info");
            return;
        }

        UserDAO.createSsoUser(email, provider, name == null ? "" : name);
        String token = JwtUtil.generateToken(email);

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<html><body>");
            out.println("<script>");
            out.println("localStorage.setItem('token', '" + token.replace("'", "\\'") + "');");
            out.println("window.location.href = '" + req.getContextPath() + "/index.jsp';");
            out.println("</script>");
            out.println("</body></html>");
        }
    }

    private void renderErrorPage(HttpServletResponse resp, String contextPath, String errorMessage) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<html><body style='background: #1a1a1a; color: #fff; font-family: Poppins, sans-serif; text-align: center; padding: 50px;'>");
            out.println("<h2 style='color: #ffcc00;'>Lỗi SSO</h2>");
            out.println("<p>" + errorMessage + "</p>");
            out.println("<button onclick=\"window.location.href='" + contextPath + "/index.jsp'\" style='background: #ffcc00; color: #000; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer;'>Quay về Trang chủ</button>");
            out.println("</body></html>");
        }
    }
}