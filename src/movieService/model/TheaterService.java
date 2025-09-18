package movieService.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TheaterService {
    private final Connection conn;

    public TheaterService(Connection conn) {
        this.conn = conn;
    }

    // 전체 극장 조회
    public List<Theater> getAllTheaters() throws SQLException {
        List<Theater> theaters = new ArrayList<>();
        String sql = "SELECT theater_id, t_name FROM Theater";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                theaters.add(new Theater(
                    rs.getString("theater_id"),
                    rs.getString("t_name")));
            }
        }
        return theaters;
    }

    // 특정 영화가 상영되는 극장 조회
    public List<Theater> getTheaterByMovie(String movieId) throws SQLException {
        List<Theater> theaters = new ArrayList<>();
        String sql = """
                select distinct t.theater_id ,t.t_name
					from MovieSchedule ms
					join Theater t on ms.theater_id = t.theater_id
					where ms.movie_id = ?;
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, movieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    theaters.add(new Theater(
                        rs.getString("theater_id"),
                        rs.getString("t_name")));
                }
            }
        } catch (SQLException e) {
            System.err.println("극장 조회 중 오류 발생: " + e.getMessage());
        }
        return theaters;
    }
}
