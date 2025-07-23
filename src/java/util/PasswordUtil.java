/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import org.mindrot.jbcrypt.BCrypt;
/**
 *
 * @author Dell-PC
 */
public class PasswordUtil {
    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    public static boolean check(String password, String hashed) {
        return hashed != null && BCrypt.checkpw(password, hashed);
    }
    
    /**
     * Command line helper to generate a BCrypt hash.
     * Usage: java util.PasswordUtil <password>
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java util.PasswordUtil <password>");
            return;
        }
        System.out.println(hash(args[0]));
    }
}
