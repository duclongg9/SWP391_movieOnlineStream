package dao.promo;

import dao.connect.DBConnection;
import model.Promotion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromotionDAO {
    public static boolean create(Promotion p) {
        String sql = "INSERT INTO promotion(code, discount_pct, apply_to, target_type, target_id, valid_until) VALUES(?,?,?,?,?,?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getCode());
            ps.setDouble(2, p.getDiscountPct());
            ps.setString(3, p.getApplyTo());
            ps.setString(4, p.getTargetType());
            if (p.getTargetId() == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, p.getTargetId());
            if (p.getValidUntil() == null) ps.setNull(6, Types.TIMESTAMP); else ps.setTimestamp(6, p.getValidUntil());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally { DBConnection.closeConnection(conn); }
    }

    public static boolean update(Promotion p) {
        String sql = "UPDATE promotion SET code=?, discount_pct=?, apply_to=?, target_type=?, target_id=?, valid_until=? WHERE id=?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getCode());
            ps.setDouble(2, p.getDiscountPct());
            ps.setString(3, p.getApplyTo());
            ps.setString(4, p.getTargetType());
            if (p.getTargetId() == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, p.getTargetId());
            if (p.getValidUntil() == null) ps.setNull(6, Types.TIMESTAMP); else ps.setTimestamp(6, p.getValidUntil());
            ps.setInt(7, p.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally { DBConnection.closeConnection(conn); }
    }

    public static boolean delete(int id) {
        String sql = "DELETE FROM promotion WHERE id=?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally { DBConnection.closeConnection(conn); }
    }

    public static Promotion findById(int id) {
        String sql = "SELECT * FROM promotion WHERE id=?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DBConnection.closeConnection(conn); }
        return null;
    }

    public static Promotion findByCode(String code) {
        String sql = "SELECT * FROM promotion WHERE code=?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return null;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DBConnection.closeConnection(conn); }
        return null;
    }

    public static List<Promotion> findAll() {
        String sql = "SELECT * FROM promotion";
        Connection conn = DBConnection.getConnection();
        List<Promotion> list = new ArrayList<>();
        if (conn == null) return list;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DBConnection.closeConnection(conn); }
        return list;
    }

    private static Promotion map(ResultSet rs) throws SQLException {
        Promotion p = new Promotion();
        p.setId(rs.getInt("id"));
        p.setCode(rs.getString("code"));
        p.setDiscountPct(rs.getDouble("discount_pct"));
        p.setApplyTo(rs.getString("apply_to"));
        p.setTargetType(rs.getString("target_type"));
        int tid = rs.getInt("target_id");
        if (!rs.wasNull()) p.setTargetId(tid); else p.setTargetId(null);
        p.setValidUntil(rs.getTimestamp("valid_until"));
        return p;
    }
}