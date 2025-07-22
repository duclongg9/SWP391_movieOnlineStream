package util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpUtil {
    public static String postForm(String urlString, Map<String, String> params) throws IOException {
        byte[] postDataBytes = buildQuery(params).getBytes(StandardCharsets.UTF_8);
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
            out.write(postDataBytes);
        }
        return readResponse(conn);
    }

    public static String get(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        return readResponse(conn);
    }

    private static String buildQuery(Map<String, String> params) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : params.entrySet()) {
            if (sb.length() > 0) sb.append('&');
            sb.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8));
            sb.append('=');
            sb.append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    private static String readResponse(HttpURLConnection conn) throws IOException {
        java.io.InputStream stream;
        try {
            stream = conn.getInputStream();
        } catch (IOException ex) {
            stream = conn.getErrorStream();
            if (stream == null) {
                throw ex;
            }
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
    
    /**
     * Parse a URL encoded query string (e.g. "a=1&b=2") into a map.
     */
    public static java.util.Map<String, String> parseQuery(String query) {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        if (query == null || query.isEmpty()) return map;
        String[] pairs = query.split("&");
        for (String p : pairs) {
            int idx = p.indexOf('=');
            String key;
            String val = "";
            if (idx >= 0) {
                key = p.substring(0, idx);
                if (idx < p.length() - 1) val = p.substring(idx + 1);
            } else {
                key = p;
            }
            key = java.net.URLDecoder.decode(key, java.nio.charset.StandardCharsets.UTF_8);
            val = java.net.URLDecoder.decode(val, java.nio.charset.StandardCharsets.UTF_8);
            map.put(key, val);
        }
        return map;
    }
}