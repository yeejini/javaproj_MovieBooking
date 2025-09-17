package movieService.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import movieService.controller.LoginSession;
import movieService.model.User;
import movieService.MakeConnection;

public class SimpleHttpServer {
    // DB 커넥션 준비 (실제 서비스는 커넥션 풀 등 사용 권장)
    private static Connection conn = MakeConnection.getConnection();
    private static LoginSession loginSession = new LoginSession();

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/signup", new SignupHandler());
            // server.createContext("/login", new LoginHandler());

            server.start();
            System.out.println(">> My Http Server started on port 8080");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 회원가입 처리 (DB 기반)
    static class SignupHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            // CORS 프리플라이트 처리
            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(200, -1);
                exchange.getResponseBody().close();
                return;
            }

            // POST 처리
            if ("POST".equalsIgnoreCase(method)) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parseFormData(body);

                String id = params.get("id");
                String name = params.get("name");
                String pwStr = params.get("pw");

                // URL 디코딩 추가
                if (id != null) id = URLDecoder.decode(id, StandardCharsets.UTF_8);
                if (name != null) name = URLDecoder.decode(name, StandardCharsets.UTF_8);

                String response;
                int status = 200;
                try {
                    int pw = Integer.parseInt(pwStr);
                    response = User.signUpWeb(id, name, pw, conn);
                    if (!"Signup successful".equals(response)) {
                        status = 400;
                    }
                } catch (NumberFormatException e) {
                    response = "Password must be 4 digits";
                    status = 400;
                }

                exchange.sendResponseHeaders(status, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.getResponseBody().close();
            }
        }
    }

    // 폼 데이터 파싱 유틸리티
    private static Map<String, String> parseFormData(String body) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }
}