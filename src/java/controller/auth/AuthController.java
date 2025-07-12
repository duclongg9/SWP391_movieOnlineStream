package controller.auth;

import com.google.gson.Gson;
import dao.user.UserDAO;
import model.User;
import util.JwtUtil;
import util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = {
    "/api/auth/login",
    "/api/auth/register",
    "/api/auth/sso/google",
    "/api/auth/sso/google/callback",
    "/api/auth/sso/facebook",
    "/api/auth/sso/facebook/callback",
    
})
public class AuthController extends HttpServlet {

    private static final Gson GSON = new Gson();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    // Google OAuth2 configuration (replace with actual values)
    // Google OAuth2 configuration loaded from environment variables
    private static final String GOOGLE_CLIENT_ID =
            System.getenv().getOrDefault("GOOGLE_CLIENT_ID", "");
    private static final String GOOGLE_CLIENT_SECRET =
            System.getenv().getOrDefault("GOOGLE_CLIENT_SECRET", "");
    private static final String GOOGLE_REDIRECT_URI =
            System.getenv().getOrDefault(
                    "GOOGLE_REDIRECT_URI",
                    "http://localhost:9999/SWP391_movieOnlineStream/api/auth/sso/google/callback");

    // Facebook OAuth2 configuration (replace with actual values)
    private static final String FACEBOOK_CLIENT_ID =
            System.getenv().getOrDefault("FACEBOOK_CLIENT_ID", "");
    private static final String FACEBOOK_CLIENT_SECRET =
            System.getenv().getOrDefault("FACEBOOK_CLIENT_SECRET", "");
    private static final String FACEBOOK_REDIRECT_URI =
            System.getenv().getOrDefault(
                    "FACEBOOK_REDIRECT_URI",
                    "http://localhost:9999/SWP391_movieOnlineStream/api/auth/sso/facebook/callback");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        switch (path) {
            case "/api/auth/login":
                req.getRequestDispatcher("/jsp/user/login.jsp").forward(req, resp);
                break;
            case "/api/auth/register":
                req.getRequestDispatcher("/jsp/user/register.jsp").forward(req, resp);
                break;
            case "/api/auth/sso/google":
                handleGoogleRedirect(resp);
                break;
            case "/api/auth/sso/google/callback":
                handleGoogleCallback(req, resp);
                break;
            case "/api/auth/sso/facebook":
                handleFacebookRedirect(resp);
                break;
            case "/api/auth/sso/facebook/callback":
                handleFacebookCallback(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getServletPath();
        

        String email = req.getParameter("email") != null ? req.getParameter("email").trim() : null;
        String password = req.getParameter("password") != null ? req.getParameter("password").trim() : null;

        if (email == null || password == null || email.isEmpty() || password.isEmpty() || !EMAIL_PATTERN.matcher(email).matches()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJsonResponse(resp, Map.of("error", "Invalid or missing fields"));
            return;
        }

        if ("/api/auth/register".equals(path)) {
            handleRegister(req, resp, email, password);
        } else if ("/api/auth/login".equals(path)) {
            handleLogin(req, resp, email, password);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleGoogleRedirect(HttpServletResponse resp) throws IOException {
        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth?scope=email%20profile&access_type=offline&include_granted_scopes=true&response_type=code&redirect_uri=" +
                URLEncoder.encode(GOOGLE_REDIRECT_URI, StandardCharsets.UTF_8) + "&client_id=" + GOOGLE_CLIENT_ID;
        resp.sendRedirect(authUrl);
    }

    private void handleGoogleCallback(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getParameter("code");
        if (code == null || code.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJsonResponse(resp, Map.of("error", "Missing code"));
            return;
        }

        // Exchange code for access token
        String tokenUrl = "https://oauth2.googleapis.com/token";
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", GOOGLE_CLIENT_ID);
        params.put("client_secret", GOOGLE_CLIENT_SECRET);
        params.put("redirect_uri", GOOGLE_REDIRECT_URI);
        params.put("grant_type", "authorization_code");

        String tokenResponse = sendPostRequest(tokenUrl, params);
        Map<String, Object> tokenJson = GSON.fromJson(tokenResponse, Map.class);
        String accessToken = (String) tokenJson.get("access_token");

        if (accessToken == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(resp, Map.of("error", "Failed to get access token"));
            return;
        }

        // Get user info
        String userInfoUrl = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json";
        String userInfoResponse = sendGetRequest(userInfoUrl, accessToken);
        Map<String, Object> userInfo = GSON.fromJson(userInfoResponse, Map.class);
        String email = (String) userInfo.get("email");

        if (email == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(resp, Map.of("error", "Failed to get user email"));
            return;
        }

        // Find or create user
        User user = UserDAO.findByEmail(email);
        if (user == null) {
            boolean created = UserDAO.createSsoUser(email, "google");
            if (!created) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendJsonResponse(resp, Map.of("error", "Failed to create user"));
                return;
            }
            user = UserDAO.findByEmail(email);
        }

        String token = JwtUtil.generateToken(email);
        sendTokenRedirect(resp, token, req.getContextPath());
    }

    private void handleFacebookRedirect(HttpServletResponse resp) throws IOException {
        String authUrl = "https://www.facebook.com/dialog/oauth?client_id=" + FACEBOOK_CLIENT_ID +
                "&redirect_uri=" + URLEncoder.encode(FACEBOOK_REDIRECT_URI, StandardCharsets.UTF_8) +
                "&scope=public_profile";
        resp.sendRedirect(authUrl);
    }

    private void handleFacebookCallback(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getParameter("code");
        if (code == null || code.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJsonResponse(resp, Map.of("error", "Missing code"));
            return;
        }

        // Exchange code for access token
        String tokenUrl = "https://graph.facebook.com/oauth/access_token?client_id=" + FACEBOOK_CLIENT_ID +
                "&redirect_uri=" + URLEncoder.encode(FACEBOOK_REDIRECT_URI, StandardCharsets.UTF_8) +
                "&client_secret=" + FACEBOOK_CLIENT_SECRET +
                "&code=" + code;

        String tokenResponse = sendGetRequest(tokenUrl, null);
        Map<String, Object> tokenJson = GSON.fromJson(tokenResponse, Map.class);
        String accessToken = tokenJson != null ? (String) tokenJson.get("access_token") : null;

        if (accessToken == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(resp, Map.of("error", "Failed to get access token"));
            return;
        }

        // Get user info
        String userInfoUrl = "https://graph.facebook.com/me?fields=id,name,email&access_token=" + accessToken;
        String userInfoResponse = sendGetRequest(userInfoUrl, null);
        Map<String, Object> userInfo = GSON.fromJson(userInfoResponse, Map.class);
        String email = (String) userInfo.get("email");

        if (email == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJsonResponse(resp, Map.of("error", "Failed to get user email"));
            return;
        }

        // Find or create user
        User user = UserDAO.findByEmail(email);
        if (user == null) {
            boolean created = UserDAO.createSsoUser(email, "facebook");
            if (!created) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendJsonResponse(resp, Map.of("error", "Failed to create user"));
                return;
            }
            user = UserDAO.findByEmail(email);
        }

        String token = JwtUtil.generateToken(email);
        sendTokenRedirect(resp, token, req.getContextPath());
    }

    private String sendPostRequest(String urlStr, Map<String, String> params) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8));
            postData.append('=');
            postData.append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8));
        }

        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.writeBytes(postData.toString());
        }

        return readResponse(conn);
    }

    private String sendGetRequest(String urlStr, String accessToken) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        if (accessToken != null) {
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        }

        return readResponse(conn);
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        } catch (IOException e) {
            // Handle error response
            if (conn.getResponseCode() >= 400) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    return content.toString();
                }
            }
            throw e;
        }
    }

    private void sendTokenRedirect(HttpServletResponse resp, String token, String contextPath) throws IOException {
        resp.setContentType("text/html");
        resp.getWriter().write("<script>localStorage.setItem('token', '" + token + "'); window.location.href = '" + contextPath + "/index.jsp';</script>");
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp, String email, String password) throws IOException {
        boolean success = UserDAO.createUser(email, PasswordUtil.hash(password));
        if (success) {
            String token = JwtUtil.generateToken(email);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            sendJsonResponse(resp, Map.of("token", token));
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJsonResponse(resp, Map.of("error", "Registration failed. Email may already exist."));
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp, String email, String password) throws IOException {
        User user = UserDAO.validateUser(email, password);
        if (user != null) {
            String token = JwtUtil.generateToken(email);
            sendJsonResponse(resp, Map.of("token", token));
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            sendJsonResponse(resp, Map.of("error", "Invalid credentials"));
        }
    }

    private void sendJsonResponse(HttpServletResponse resp, Map<String, Object> data) throws IOException {
        try (var out = resp.getWriter()) {
            out.print(GSON.toJson(data));
        }
    }
}