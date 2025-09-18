package movieService.model;

import movieService.model.Movie;
import java.sql.*;
import java.util.*;

public class MovieService {
    private final Connection conn;

    public MovieService(Connection conn) {
        this.conn = conn;
    }

    // 전체 영화 조회
    public List<Movie> getAllMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT DISTINCT movie_id, title, genre FROM Movie";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                movies.add(new Movie(
                    rs.getString("movie_id"),
                    rs.getString("title"), "")
                    // rs.getString("genre")
                    );
            }
        }
        return movies;
    }

    // 특정 극장에서 상영하는 영화 조회
    public List<Movie> getMoviesByTheater(String theaterId) throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String sql = """
                SELECT DISTINCT m.movie_id, m.title
                FROM MovieSchedule ms
                JOIN Movie m ON ms.movie_id = m.movie_id
                WHERE ms.theater_id = ?
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, theaterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    movies.add(new Movie(rs.getString("movie_id"), rs.getString("title"), ""));
                }
            }
        }
        return movies;
    }
}

