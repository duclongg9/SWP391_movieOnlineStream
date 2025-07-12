package dao.pkg;

import dao.connect.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PackageFilmDAO {
    public static boolean addFilmToPackage(int filmId, int packageId) {
        String sql = "INSERT INTO package_film(package_id, film_id) VALUES(?,?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, packageId);
            ps.setInt(2, filmId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    public static boolean removeFilmFromPackage(int filmId, int packageId) {
        String sql = "DELETE FROM package_film WHERE package_id=? AND film_id=?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, packageId);
            ps.setInt(2, filmId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
}
