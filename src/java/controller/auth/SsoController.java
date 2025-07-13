package controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet(urlPatterns = {
        "/api/auth/sso/google",
        "/api/auth/sso/facebook"
})
public class SsoController extends HttpServlet {
    private static final String OAUTH_STATE = "oauth_state";

    private static final String GOOGLE_CLIENT_ID =
            System.getenv().getOrDefault("GOOGLE_CLIENT_ID", "142934635795-5ra2ujg4nuvdo6e9p6m79jauncijb7qg.apps.googleusercontent.com");
    private static final String GOOGLE_REDIRECT_URI =
            System.getenv().getOrDefault("GOOGLE_REDIRECT_URI",
                    "http://localhost:9999/SWP391_movieOnlineStream/api/auth/sso/google/callback");

    private static final String FACEBOOK_CLIENT_ID =
            System.getenv().getOrDefault("FACEBOOK_CLIENT_ID", "1044612457872751");
    private static final String FACEBOOK_REDIRECT_URI =
            System.getenv().getOrDefault("FACEBOOK_REDIRECT_URI",
                    "http://localhost:9999/SWP391_movieOnlineStream/api/auth/sso/facebook/callback");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/api/auth/sso/google".equals(path)) {
            handleGoogleRedirect(req, resp);
        } else if ("/api/auth/sso/facebook".equals(path)) {
            handleFacebookRedirect(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleGoogleRedirect(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (GOOGLE_CLIENT_ID.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Google SSO not configured");
            return;
        }
        String state = generateState(req);
        String url = "https://accounts.google.com/o/oauth2/v2/auth" +
                "?scope=email%20profile" +
                "&access_type=offline" +
                "&include_granted_scopes=true" +
                "&response_type=code" +
                "&redirect_uri=" + URLEncoder.encode(GOOGLE_REDIRECT_URI, StandardCharsets.UTF_8) +
                "&client_id=" + GOOGLE_CLIENT_ID +
                "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8);
        resp.sendRedirect(url);
    }

    private void handleFacebookRedirect(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (FACEBOOK_CLIENT_ID.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Facebook SSO not configured");
            return;
        }
        String state = generateState(req);
        String url = "https://www.facebook.com/dialog/oauth" +
                "?client_id=" + FACEBOOK_CLIENT_ID +
                "&redirect_uri=" + URLEncoder.encode(FACEBOOK_REDIRECT_URI, StandardCharsets.UTF_8) +
                "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8);
        resp.sendRedirect(url);
    }

    private String generateState(HttpServletRequest req) {
        String state = java.util.UUID.randomUUID().toString();
        req.getSession(true).setAttribute(OAUTH_STATE, state);
        return state;
    }
}