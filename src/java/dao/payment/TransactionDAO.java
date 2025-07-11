package dao.payment;

import dao.connect.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionDAO {
    public static boolean logTransaction(int userId, int amountVnd, int pointEarned, String status) {
        String sql = "INSERT INTO transaction(user_id, amount_vnd, point_earned, gateway_status) VALUES(?,?,?,?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, amountVnd);
            ps.setInt(3, pointEarned);
            ps.setString(4, status);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
}