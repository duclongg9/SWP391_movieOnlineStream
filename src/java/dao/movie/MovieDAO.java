/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.movie;

import dao.connect.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Movie;

/**
 *
 * @author Dell-PC
 */


public class MovieDAO {
    private static final List<Movie> SAMPLE_MOVIES = new ArrayList<>();

    static {
        SAMPLE_MOVIES.add(new Movie(1, "The Northman", "assets/images/upcoming-1.png", "HD", 2022, "8.5", 50));
        SAMPLE_MOVIES.get(0).setGenre("Action");
        SAMPLE_MOVIES.get(0).setActor("Alexander Skarsg\u00e5rd");
        SAMPLE_MOVIES.get(0).setDescription("A young Viking prince seeks revenge for his father's murder.");

        SAMPLE_MOVIES.add(new Movie(2, "Doctor Strange in the Multiverse of Madness",
                "assets/images/upcoming-2.png", "4K", 2022, "NR", 60));
        SAMPLE_MOVIES.get(1).setGenre("Fantasy");
        SAMPLE_MOVIES.get(1).setActor("Benedict Cumberbatch");
        SAMPLE_MOVIES.get(1).setDescription("Doctor Strange faces the unknown dangers of the multiverse.");

        SAMPLE_MOVIES.add(new Movie(3, "Memory", "assets/images/upcoming-3.png", "2K", 2022, "NR", 40));
        SAMPLE_MOVIES.get(2).setGenre("Thriller");
        SAMPLE_MOVIES.get(2).setActor("Liam Neeson");
        SAMPLE_MOVIES.get(2).setDescription("An assassin-for-hire becomes a target after refusing a job.");

        SAMPLE_MOVIES.add(new Movie(4, "The Unbearable Weight of Massive Talent",
                "assets/images/upcoming-4.png", "HD", 2022, "NR", 30));
        SAMPLE_MOVIES.get(3).setGenre("Comedy");
        SAMPLE_MOVIES.get(3).setActor("Nicolas Cage");
        SAMPLE_MOVIES.get(3).setDescription("A fictionalized Nicolas Cage accepts a bizarre gig.");

        }

    public static List<Movie> getUpcomingMovies() {
        return new ArrayList<>(SAMPLE_MOVIES);
    }
    
    
    public static boolean create(Movie m) {
        String sql = "INSERT INTO movies(title, genre, actor, video_path, description, price_point) " +
                "VALUES(?,?,?,?,?,?)";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getGenre());
            ps.setString(3, m.getActor());
            ps.setString(4, m.getVideoPath());
            ps.setString(5, m.getDescription());
            ps.setInt(6, m.getPricePoint());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    public static boolean update(Movie m) {
        String sql = "UPDATE movies SET title=?, genre=?, actor=?, video_path=?, description=?, price_point=? WHERE id=?";
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getGenre());
            ps.setString(3, m.getActor());
            ps.setString(4, m.getVideoPath());
            ps.setString(5, m.getDescription());
            ps.setInt(6, m.getPricePoint());
            ps.setInt(7, m.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    public static boolean softDelete(int id) {
        String sql = "UPDATE movies SET is_deleted=1 WHERE id=?";
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


       public static Movie getMovieById(int id) {
        String sql = "SELECT * FROM movies WHERE id=?";
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    Movie m = map(rs);
                    DBConnection.closeConnection(conn);
                    return m;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
        for (Movie m : SAMPLE_MOVIES) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }
       
    public static List<Movie> searchByKeyword(String keyword) {
        List<Movie> list = new ArrayList<>();
        String sql = "SELECT * FROM movies WHERE title LIKE ? OR genre LIKE ? OR actor LIKE ?";
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String q = "%" + keyword + "%";
                ps.setString(1, q);
                ps.setString(2, q);
                ps.setString(3, q);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    list.add(map(rs));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
        if (list.isEmpty()) {
            for (Movie m : SAMPLE_MOVIES) {
                if (m.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                    list.add(m);
                }
            }
        }
        return list;
    }

    private static Movie map(ResultSet rs) throws SQLException {
        Movie m = new Movie();
        m.setId(rs.getInt("id"));
        m.setTitle(rs.getString("title"));
        m.setGenre(rs.getString("genre"));
        m.setActor(rs.getString("actor"));
        m.setVideoPath(rs.getString("video_path"));
        m.setDescription(rs.getString("description"));
        m.setPricePoint(rs.getInt("price_point"));
        return m;
    }
}