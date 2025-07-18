package controller.payment;

import dao.payment.TransactionDAO;
import dao.user.UserDAO;
import util.JwtUtil;
import util.VNPayConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@WebServlet(urlPatterns = {"/api/user/payment/create", "/vnpay_return", "/vnpay_ipn"})
public class PaymentController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/api/user/payment/create".equals(path)) {
            createPayment(req, resp);
        } else if ("/vnpay_ipn".equals(path)) {
            handleIPN(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/vnpay_return".equals(path)) {
            handleReturn(req, resp);
        }
    }

    private void createPayment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String token = extractToken(req);
        String email = JwtUtil.verifyToken(token);
        if (email == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"error\":\"unauthorized\"}");
            return;
        }
        int userId = UserDAO.getUserIdByEmail(email);
        if (userId == 0) {
            resp.setStatus(404);
            resp.getWriter().write("{\"error\":\"user not found\"}");
            return;
        }

        String amountStr = req.getParameter("amount");
        int amountVnd;
        try {
            amountVnd = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"invalid amount\"}");
            return;
        }

        // Calculate points, assume 1 VND = 1 Point for simplicity
        int points = amountVnd;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amountVnd * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", String.valueOf(System.currentTimeMillis())); // Unique order id
        vnp_Params.put("vnp_OrderInfo", "Nap tien cho user " + userId);
        vnp_Params.put("vnp_OrderType", "topup");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", getIpAddress(req));

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

        // Log transaction pending
        TransactionDAO.logTransaction(userId, amountVnd, points, "pending", "topup");

        resp.setContentType("application/json");
        resp.getWriter().write("{\"paymentUrl\":\"" + paymentUrl + "\"}");
    }

    private void handleReturn(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Process return from VNPay
        Map<String, String> fields = new HashMap<>();
        for (Enumeration params = req.getParameterNames(); params.hasMoreElements();) {
            String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
            String fieldValue = URLEncoder.encode(req.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = req.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }

        String signValue = hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            String txnRef = req.getParameter("vnp_TxnRef");
            String rspCode = req.getParameter("vnp_ResponseCode");
            if ("00".equals(rspCode)) {
                // Success, update user points
                // Extract userId from orderInfo or txnRef, assume stored
                // For simplicity, redirect to success page
                resp.sendRedirect("/payment/success");
            } else {
                resp.sendRedirect("/payment/fail");
            }
        } else {
            resp.sendRedirect("/payment/error");
        }
    }

    private void handleIPN(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Similar to handleReturn, but for server-to-server notification
        // Verify hash, update database if success
        // Respond with JSON status
        Map<String, String> fields = new HashMap<>();
        // ... similar code ...
        String signValue = hashAllFields(fields);
        if (signValue.equals(req.getParameter("vnp_SecureHash"))) {
            if ("00".equals(req.getParameter("vnp_ResponseCode"))) {
                // Update transaction status to success, add points to user
                int amount = Integer.parseInt(req.getParameter("vnp_Amount")) / 100;
                int points = amount; // assume 1:1
                // Find transaction by txnRef, get userId, update UserDAO.addPoints(userId, points)
                // TransactionDAO.updateStatus(txnRef, "success")
            }
            resp.getWriter().write("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
        } else {
            resp.getWriter().write("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
        }
    }

    private String hashAllFields(Map fields) {
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName).append("=").append(fieldValue);
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        return hmacSHA512(VNPayConfig.vnp_HashSecret, sb.toString());
    }

    private static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    private String extractToken(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private String getIpAddress(HttpServletRequest request) {
        String ipAdress = request.getHeader("X-FORWARDED-FOR");
        if (ipAdress == null) {
            ipAdress = request.getRemoteAddr();
        }
        return ipAdress;
    }
}