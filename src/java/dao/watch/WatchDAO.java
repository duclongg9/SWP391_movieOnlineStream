package dao.watch;

import dao.connect.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WatchDAO {
    public static void log(int userId, int filmId) {
        String sql = "INSERT INTO watch_history(user_id, film_id) VALUES(?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, filmId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
}