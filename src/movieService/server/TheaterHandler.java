package movieService.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import movieService.model.Movie;
import movieService.model.MovieService;
import movieService.model.Theater;
import movieService.model.TheaterService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;

public class TheaterHandler implements HttpHandler {
    private final Connection conn;

    public TheaterHandler(Connection conn) {
        this.conn = conn;
    }

    public void handle (HttpExchange exchange) throws IOException {
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
            String path = requestUri.getPath(); // e.g. /theater or /theater/M1
            String[] segments = path.split("/");

            String response;
            int status = 200;

            // INSERT INTO Theater (theater_id, t_name, total_screen) VALUES
            // ('T1', '롯데시네마', 5),
            // ('T2', 'CGV', 5),
            // ('T3', '메가박스', 5);
        try {
            TheaterService theaterService = new TheaterService(conn);
            List<Theater> theaters;

            if(segments.length == 2){
                // /theater
                theaters = theaterService.getAllTheaters();

            }else if(segments.length == 3){
                // /theater/{movie_id}
                String movieId = segments[2];
                theaters = theaterService.getTheaterByMovie(movieId);
            }else {
                response = "{\"error\":\"Invalid endpoint\"}";
                exchange.sendResponseHeaders(404, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }

                // JSON 변환
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < theaters.size(); i++) {
                    Theater t = theaters.get(i);
                    json.append("{")
                        .append("\"theaterId\":\"").append(t.getTheaterId()).append("\",")
                        .append("\"theaterName\":\"").append(t.getTheaterName()).append("\"")
                        .append("}");
                    if (i < theaters.size() - 1) {
                        json.append(",");
                    }
                }
                json.append("]");

                response = json.toString();
                exchange.sendResponseHeaders(status, response.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            }catch (Exception e) {
                response = "{\"error\":\"" + e.getMessage() + "\"}";
                exchange.sendResponseHeaders(500, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
            }

        }else {
            exchange.sendResponseHeaders(405, 0);
            exchange.getResponseBody().close();
        }
    }
}
