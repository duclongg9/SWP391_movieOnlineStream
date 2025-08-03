package dao.pkg;

import dao.connect.DBConnection;
import model.Package;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackageDAO {
    public static boolean create(Package p) {
        String sql = "INSERT INTO package(name, description, duration_days, price_point) VALUES(?,?,?,?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setInt(3, p.getDurationDays());
            ps.setInt(4, p.getPricePoint());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    public static boolean update(Package p) {
        String sql = "UPDATE package SET name=?, description=?, duration_days=?, price_point=? WHERE id=?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setInt(3, p.getDurationDays());
            ps.setInt(4, p.getPricePoint());
            ps.setInt(5, p.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    public static boolean delete(int id) {
        String sql = "UPDATE package SET is_deleted=1 WHERE id=?";
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

    public static Package findById(int id) {
        String sql = "SELECT * FROM package WHERE id=? AND is_deleted=0";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return null;
    }

    public static List<Package> findAll() {
        String sql = "SELECT * FROM package WHERE is_deleted=0";
        Connection conn = DBConnection.getConnection();
        List<Package> list = new ArrayList<>();
        if (conn == null) return list;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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

    public static List<Package> findPage(int offset, int limit) {
        String sql = "SELECT * FROM package WHERE is_deleted=0 LIMIT ? OFFSET ?";
        Connection conn = DBConnection.getConnection();
        List<Package> list = new ArrayList<>();
        if (conn == null) return list;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
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

    public static int count() {
        String sql = "SELECT COUNT(*) FROM package WHERE is_deleted=0";
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

    private static Package map(ResultSet rs) throws SQLException {
        Package p = new Package();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setDurationDays(rs.getInt("duration_days"));
        p.setPricePoint(rs.getInt("price_point"));
        p.setDeleted(rs.getBoolean("is_deleted"));
        return p;
    }
}