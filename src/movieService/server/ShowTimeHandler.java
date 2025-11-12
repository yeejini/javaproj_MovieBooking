// package movieService.server;

// import com.sun.net.httpserver.HttpExchange;
// import com.sun.net.httpserver.HttpHandler;
// import movieService.model.Showtime;
// import movieService.model.ShowtimeService;

// import java.io.IOException;
// import java.io.OutputStream;
// import java.net.URI;
// import java.nio.charset.StandardCharsets;
// import java.sql.Connection;
// import java.util.List;

// public class ShowTimeHandler implements HttpHandler {

//     private final Connection conn;

//     public ShowTimeHandler(Connection conn) {
//         this.conn = conn;
//     }

//     @Override
//     public void handle(HttpExchange exchange) throws IOException {
//         String method = exchange.getRequestMethod();

//         // CORS 프리플라이트
//         if ("OPTIONS".equalsIgnoreCase(method)) {
//             exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
//             exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
//             exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
//             exchange.sendResponseHeaders(204, -1); // No Content
//             exchange.getResponseBody().close();
//             return;
//         }

//         if ("GET".equalsIgnoreCase(method)) {
//             // CORS 및 Content-Type 헤더
//             exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
//             exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
//             exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
//             exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

//             URI requestUri = exchange.getRequestURI();
//             String path = requestUri.getPath(); 
//             String[] segments = path.split("/"); // ["", "showtime", "{theaterId}", "{movieId}", "{date}?"]

//             String response;
//             int status = 200;

//             try {
//                 ShowtimeService service = new ShowtimeService(conn);
//                 List<Showtime> showtimes;

//                 if (segments.length == 4) {
//                     // /showtime/{theater_id}/{movie_id} → 날짜 리스트 조회
//                     String theaterId = segments[2];
//                     String movieId = segments[3];
//                     showtimes = service.getShowtimeDates(theaterId, movieId);

//                 } else if (segments.length == 5) {
//                     // /showtime/{theater_id}/{movie_id}/{date} → 시간표 조회
//                     String theaterId = segments[2];
//                     String movieId = segments[3];
//                     String date = segments[4];
//                     showtimes = service.getShowtimesByDate(theaterId, movieId, date);
  
//                 } else {
//                     status = 400;
//                     response = "{\"error\":\"Invalid request path\"}";
//                     sendResponse(exchange, status, response);
//                     return;
//                 }

//                 // 결과 JSON 변환
//                 StringBuilder sb = new StringBuilder();
//                 sb.append("[");
//                 for (int i = 0; i < showtimes.size(); i++) {
//                     Showtime s = showtimes.get(i);
//                     sb.append(s.toJson());
//                     if (i < showtimes.size() - 1) sb.append(",");
//                 }
//                 sb.append("]");

//                 response = sb.toString();
//                 sendResponse(exchange, status, response);

//             } catch (Exception e) {
//                 status = 500;
//                 response = "{\"error\":\"" + e.getMessage() + "\"}";
//                 sendResponse(exchange, status, response);
//             }
//         }
//     }

//     private void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
//         byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
//         exchange.sendResponseHeaders(status, bytes.length);
//         try (OutputStream os = exchange.getResponseBody()) {
//             os.write(bytes);
//         }
//     }
// }
