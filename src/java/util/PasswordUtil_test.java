package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil_test {
    
    public static void main(String[] args) {
        String pw = args.length > 0 ? args[0] : "admin";
        System.out.println(PasswordUtil.hash(pw));
    }
}