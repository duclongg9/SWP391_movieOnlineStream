package dao.purchase;

import dao.connect.DBConnection;
import model.Purchase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAO {
    public static List<Purchase> listByUser(int userId) {
        String sql = "SELECT * FROM user_purchase WHERE user_id=? ORDER BY purchased_at DESC";
        Connection conn = DBConnection.getConnection();
        List<Purchase> list = new ArrayList<>();
        if (conn == null) return list;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return list;
    }

    public static boolean addPackagePurchase(int userId, int packageId, int durationDays) {
        String sql = "INSERT INTO user_purchase(user_id, package_id, expired_at) VALUES(?,?, DATE_ADD(NOW(), INTERVAL ? DAY))";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, packageId);
            ps.setInt(3, durationDays);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
public static boolean addMoviePurchase(int userId, int filmId) {
        String sql = "INSERT INTO user_purchase(user_id, film_id) VALUES(?, ?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, filmId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    public static boolean hasAccessToFilm(int userId, int filmId) {
        String sql = "SELECT COUNT(*) FROM user_purchase up " +
                "LEFT JOIN package_film pf ON up.package_id = pf.package_id " +
                "WHERE up.user_id=? AND ((up.film_id=? ) OR pf.film_id=?) " +
                "AND (up.expired_at IS NULL OR up.expired_at > NOW())";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, filmId);
            ps.setInt(3, filmId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return false;
    }

    public static Purchase getActivePackage(int userId) {
        String sql = "SELECT * FROM user_purchase WHERE user_id=? AND package_id IS NOT NULL " +
                "AND expired_at > NOW() ORDER BY expired_at DESC LIMIT 1";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return null;
    }

    public static boolean expire(int purchaseId) {
        String sql = "UPDATE user_purchase SET expired_at=NOW() WHERE id=?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, purchaseId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }


    
    private static Purchase map(ResultSet rs) throws SQLException {
        Purchase p = new Purchase();
        p.setId(rs.getInt("id"));
        int film = rs.getInt("film_id");
        if (!rs.wasNull()) p.setFilmId(film);
        int pkg = rs.getInt("package_id");
        if (!rs.wasNull()) p.setPackageId(pkg);
        p.setPurchasedAt(rs.getTimestamp("purchased_at"));
        p.setExpiredAt(rs.getTimestamp("expired_at"));
        return p;
    }
}