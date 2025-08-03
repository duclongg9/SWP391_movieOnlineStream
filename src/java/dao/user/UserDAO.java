package dao.user;

import dao.connect.DBConnection;
import model.User;
import util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public static User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ? AND is_deleted = 0";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setFullName(rs.getString("full_name"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setProfilePic(rs.getString("profile_pic"));
                u.setRole(rs.getString("role"));
                u.setPhone(rs.getString("phone"));
                u.setPhoneVerified(rs.getBoolean("phone_verified"));
                u.setOtpCode(rs.getString("otp_code"));
                u.setOtpExpire(rs.getTimestamp("otp_expire"));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return null;
    }

    public static boolean create(User user) {
        String sql = "INSERT INTO users (email, password, role) VALUES (?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, PasswordUtil.hash(user.getPassword()));
            ps.setString(3, user.getRole());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return false;
    }

    public static boolean updateEmail(String oldEmail, String newEmail) {
        String sql = "UPDATE users SET email = ? WHERE email = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newEmail);
            ps.setString(2, oldEmail);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return false;
    }

    public static boolean updatePhone(String email, String phone) {
        String sql = "UPDATE users SET phone = ?, phone_verified = 0 WHERE email = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setString(2, email);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return false;
    }
    
    public static void updateProfilePic(String email, String picUrl) {
        String sql = "UPDATE users SET profile_pic=? WHERE email=?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, picUrl);
            ps.setString(2, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
    }



    public static void updateOtp(String email, String code, Timestamp expire) {
        String sql = "UPDATE users SET otp_code=?, otp_expire=? WHERE email=?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setTimestamp(2, expire);
            ps.setString(3, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    public static void clearOtp(String email) {
        String sql = "UPDATE users SET otp_code=NULL, otp_expire=NULL WHERE email=?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    public static boolean setPhoneVerified(String email, boolean verified) {
        String sql = "UPDATE users SET phone_verified=? WHERE email=?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, verified);
            ps.setString(2, email);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return false;
    }

    public static boolean isPhoneVerified(String email) {
        String sql = "SELECT phone_verified FROM users WHERE email=?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return false;
    }


    
    public static boolean changePassword(String email, String newHashedPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHashedPassword);
            ps.setString(2, email);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return false;
    }

    public static User validateUser(String email, String password) {
        User user = findByEmail(email);
        if (user != null && user.getPassword().equals(PasswordUtil.hash(password))) {
            return user;
        }
        return null;
    }

    public static int getUserIdByEmail(String email) {
        String sql = "SELECT id FROM users WHERE email = ? AND is_deleted = 0";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return 0;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return 0;
    }

    public static boolean isAdmin(String email) {
        User u = findByEmail(email);
        return u != null && "admin".equals(u.getRole());
    }

    public static List<User> findAllUsers() {
        String sql = "SELECT * FROM users WHERE is_deleted = 0";
        Connection conn = DBConnection.getConnection();
        List<User> list = new ArrayList<>();
        if (conn == null) return list;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role"));
                // Add locked if exists
                list.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return list;
    }

    public static List<User> findPage(int offset, int limit) {
        String sql = "SELECT * FROM users WHERE is_deleted = 0 LIMIT ? OFFSET ?";
        Connection conn = DBConnection.getConnection();
        List<User> list = new ArrayList<>();
        if (conn == null) return list;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role"));
                list.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return list;
    }

    public static int count() {
        String sql = "SELECT COUNT(*) FROM users WHERE is_deleted = 0";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return 0;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return 0;
    }

    public static boolean lockUser(int id, boolean lock) {
        String sql = "UPDATE users SET locked = ? WHERE id = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, lock);
            ps.setInt(2, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return false;
    }

    public static User findAdminByUsername(String username) {
        // Implement if needed
        return null;
    }

    public static boolean addPoints(String email, int points) {
        String sql = "UPDATE users SET point_balance = point_balance + ? WHERE email=? AND is_deleted=0";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, points);
            ps.setString(2, email);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    public static Integer getPointBalance(String email) {
        String sql = "SELECT point_balance FROM users WHERE email=? AND is_deleted=0";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return null;
    }
    
    
    /**
     * Create a new user authenticated via SSO if they do not already exist.
     * The new user will have role "user" and no password.
     */
    public static void createSsoUser(String email, String provider, String fullName, String profilePic) {
        if (email == null || email.isBlank()) return;
        if (findByEmail(email) != null) return;

        String sql = "INSERT INTO users (email, full_name, profile_pic, sso_provider, role) " +
                "VALUES (?, ?, ?, ?, 'user')";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, fullName == null ? "" : fullName);
            ps.setString(3, profilePic);
            ps.setString(4, provider);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
}