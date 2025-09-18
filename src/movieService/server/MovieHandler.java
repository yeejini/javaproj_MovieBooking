package movieService.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import movieService.model.Movie;
import movieService.model.MovieService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;

public class MovieHandler implements HttpHandler {
    private final Connection conn;

    public MovieHandler(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        // CORS 프리플라이트
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.sendResponseHeaders(204, -1); // No Content
            exchange.getResponseBody().close();
            return;
        }

        if ("GET".equalsIgnoreCase(method)) {
            // GET 응답에도 CORS 헤더 추가
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

            URI requestUri = exchange.getRequestURI();
            String path = requestUri.getPath(); // e.g. /movie or /movie/T1
            String[] segments = path.split("/");

            String response;
            int status = 200;

            try {
                MovieService movieService = new MovieService(conn);
                List<Movie> movies;

                if (segments.length == 2) { 
                    // /movie
                    movies = movieService.getAllMovies();
                } else if (segments.length == 3) { 
                    // /movie/{theater_id}
                    String theaterId = segments[2];
                    movies = movieService.getMoviesByTheater(theaterId);
                } else {
                    response = "{\"error\":\"Invalid endpoint\"}";
                    exchange.sendResponseHeaders(404, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    return;
                }

                // JSON 변환
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < movies.size(); i++) {
                    Movie m = movies.get(i);
                    json.append("{")
                        .append("\"movie_id\":\"").append(m.getMovieId()).append("\",")
                        .append("\"title\":\"").append(m.getTitle()).append("\"")
                        .append("}");
                    if (i < movies.size() - 1) json.append(",");
                }
                json.append("]");

                response = json.toString();
                exchange.sendResponseHeaders(status, response.getBytes(StandardCharsets.UTF_8).length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }

            } catch (Exception e) {
                response = "{\"error\":\"" + e.getMessage() + "\"}";
                exchange.sendResponseHeaders(500, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            }

        } else {
            exchange.sendResponseHeaders(405, 0);
            exchange.getResponseBody().close();
        }
    }
}
