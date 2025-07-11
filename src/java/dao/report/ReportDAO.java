package dao.report;

import dao.connect.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportDAO {
    public static int totalPointEarned(String from, String to) {
        String sql = "SELECT SUM(point_earned) FROM transaction WHERE created_at BETWEEN ? AND ?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return 0;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, from);
            ps.setString(2, to);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return 0;
    }
}