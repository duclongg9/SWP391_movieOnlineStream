package model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String email;
    private String password;
    private String ssoProvider;
    private int pointBalance;
    private boolean isLocked;
    private Timestamp createdAt;

    public User() {}

    public User(int id, String email, String password, String ssoProvider, int pointBalance, boolean isLocked, Timestamp createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.ssoProvider = ssoProvider;
        this.pointBalance = pointBalance;
        this.isLocked = isLocked;
        this.createdAt = createdAt;
    }

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}