/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class JwtUtil {
    private static final String SECRET = "secret-key";

    public static String generateToken(String subject) {
        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = "{\"sub\":\"" + subject + "\",\"iat\":" + (System.currentTimeMillis()/1000) + "}";
        String header = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signature = hmacSha256(header + "." + payload, SECRET);
        return header + "." + payload + "." + signature;
    }

    public static String verifyToken(String token) {
        if (token == null) return null;
        String[] parts = token.split("\\.");
        if (parts.length != 3) return null;
        String expectedSig = hmacSha256(parts[0] + "." + parts[1], SECRET);
        if (!expectedSig.equals(parts[2])) return null;
        try {
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            int idx = payloadJson.indexOf("\"sub\":\"");
            if (idx == -1) return null;
            idx += 7;
            int end = payloadJson.indexOf('"', idx);
            if (end == -1) return null;
            return payloadJson.substring(idx, end);
        } catch (Exception e) {
            return null;
        }
    }


    
    private static String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
