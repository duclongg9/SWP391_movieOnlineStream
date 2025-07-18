package model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String username;
    private String fullName;
    private String phone;
    private String email;
    private String password;
    private String ssoProvider;
    private int pointBalance;
    private boolean isLocked;
    private boolean phoneVerified;
    private String otpCode;
    private Timestamp otpExpire;
    private boolean deleted;
    private String role;
    private Timestamp createdAt;

    public User() {}

    public User(int id, String username, String fullName, String phone, String email,
                String password, String ssoProvider, int pointBalance,
                boolean isLocked, boolean phoneVerified, String otpCode, Timestamp otpExpire,
                boolean deleted, String role, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.ssoProvider = ssoProvider;
        this.pointBalance = pointBalance;
        this.isLocked = isLocked;
        this.phoneVerified = phoneVerified;
        this.otpCode = otpCode;
        this.otpExpire = otpExpire;
        this.deleted = deleted;
        this.role = role;
        this.createdAt = createdAt;
    }

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSsoProvider() { return ssoProvider; }
    public void setSsoProvider(String ssoProvider) { this.ssoProvider = ssoProvider; }

    public int getPointBalance() { return pointBalance; }
    public void setPointBalance(int pointBalance) { this.pointBalance = pointBalance; }

    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean locked) { isLocked = locked; }

    public boolean isPhoneVerified() { return phoneVerified; }
    public void setPhoneVerified(boolean phoneVerified) { this.phoneVerified = phoneVerified; }

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }

    public Timestamp getOtpExpire() { return otpExpire; }
    public void setOtpExpire(Timestamp otpExpire) { this.otpExpire = otpExpire; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
