package dao.user;

import dao.connect.DBConnection;
import model.User;
import util.PasswordUtil;

import java.sql.*;

public class UserDAO {
    public static boolean createUser(String username, String fullName, String phone, String email, String passwordHash) {
        String sql = "INSERT INTO users(username, full_name, phone, email, password) VALUES(?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            System.err.println("DB connection is null when creating user");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, fullName);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setString(5, passwordHash);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            DBConnection.closeConnection(conn);
        }
    }

    public static boolean createSsoUser(String email, String provider) {
        String sql = "INSERT INTO users(email, sso_provider) VALUES(?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            System.err.println("DB connection is null when creating sso user");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, provider);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    
    public static User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ? AND is_deleted=0";
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            System.err.println("DB connection is null when finding user");
            return null;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }    finally {
            DBConnection.closeConnection(conn);
        }
        return null;
    }

    public static User findAdminByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username=? AND role='admin' AND is_deleted=0";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return null;
    }

    public static User validateUser(String email, String password) {
        User u = findByEmail(email);
        if (u != null && !u.isDeleted() && u.getPassword().equals(PasswordUtil.hash(password)) && !u.isLocked()) {
            return u;
        }
        return null;
    }

    public static boolean updateEmail(String currentEmail, String newEmail) {
        String sql = "UPDATE users SET email=? WHERE email=? AND is_deleted=0";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newEmail);
            ps.setString(2, currentEmail);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    public static boolean changePassword(String email, String newPassHash) {
        String sql = "UPDATE users SET password=? WHERE email=? AND is_deleted=0";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassHash);
            ps.setString(2, email);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
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

    public static boolean setLocked(int id, boolean locked) {
        String sql = "UPDATE users SET is_locked=? WHERE id=? AND is_deleted=0";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, locked);
            ps.setInt(2, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    public static boolean softDelete(int id) {
        String sql = "UPDATE users SET is_deleted=1 WHERE id=?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    public static java.util.List<User> findAll() {
        String sql = "SELECT * FROM users WHERE is_deleted=0";
        Connection conn = DBConnection.getConnection();
        java.util.List<User> list = new java.util.ArrayList<>();
        if (conn == null) return list;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return list;
    }


    
    private static User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setFullName(rs.getString("full_name"));
        u.setPhone(rs.getString("phone"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setSsoProvider(rs.getString("sso_provider"));
        u.setPointBalance(rs.getInt("point_balance"));
        u.setLocked(rs.getBoolean("is_locked"));
        u.setDeleted(rs.getBoolean("is_deleted"));
        u.setRole(rs.getString("role"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }
}