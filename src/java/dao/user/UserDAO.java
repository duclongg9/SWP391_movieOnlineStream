package dao.user;

import dao.connect.DBConnection;
import model.User;
import util.PasswordUtil;

import java.sql.*;

public class UserDAO {
    public static boolean createUser(String email, String passwordHash) {
        String sql = "INSERT INTO users(email, password) VALUES(?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            System.err.println("DB connection is null when creating user");
            return false;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            DBConnection.closeConnection(conn);
        }
    }

    public static User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            System.err.println("DB connection is null when finding user");
            return null;
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = mapRow(rs);
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }    finally {
            DBConnection.closeConnection(conn);
        }
        return null;
    }

    public static User validateUser(String email, String password) {
        User u = findByEmail(email);
        if (u != null && u.getPassword().equals(PasswordUtil.hash(password)) && !u.isLocked()) {
            return u;
        }
        return null;
    }

    private static User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setSsoProvider(rs.getString("sso_provider"));
        u.setPointBalance(rs.getInt("point_balance"));
        u.setLocked(rs.getBoolean("is_locked"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }
}