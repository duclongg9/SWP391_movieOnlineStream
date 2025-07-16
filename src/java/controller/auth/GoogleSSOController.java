package controller.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import dao.user.UserDAO;
import model.User;
import util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

@WebServlet(urlPatterns = {"/auth/google", "/auth/google/callback"})
public class GoogleSSOController extends HttpServlet {
    private static final String CLIENT_ID = "your-client-id";
    private static final String CLIENT_SECRET = "your-client-secret";
    private static final String REDIRECT_URI = "http://localhost:8080/auth/google/callback";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private GoogleAuthorizationCodeFlow flow;

    @Override
    public void init() {
        flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET,
                Arrays.asList("email", "profile"))
                .setAccessType("offline")
                .build();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/auth/google".equals(path)) {
            String url = new GoogleAuthorizationCodeRequestUrl(CLIENT_ID, REDIRECT_URI,
                    Arrays.asList("email", "profile")).setAccessType("offline").build();
            resp.sendRedirect(url);
        } else if ("/auth/google/callback".equals(path)) {
            String code = req.getParameter("code");
            if (code == null) {
                resp.sendError(400);
                return;
            }
            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
            // Get user info from id_token or access_token
            // Assume fetch email
            String email = "fetched-email@gmail.com"; // Implement fetch
            User user = UserDAO.findByEmail(email);
            if (user == null) {
                // Register new user
                user = new User();
                user.setEmail(email);
                user.setRole("user");
                UserDAO.create(user);
            }
            String token = JwtUtil.generateToken(email);
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.write("{\"token\":\"" + token + "\"}");
        }
    }
}