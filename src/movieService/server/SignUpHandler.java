package movieService.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import movieService.model.User;

public class SignUpHandler implements HttpHandler {
	private final Connection conn;

	// 생성자로 커넥션 주입
	public SignUpHandler(Connection conn) {
		this.conn = conn;
	}

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

			// URL 디코딩
			if (id != null) {
				id = URLDecoder.decode(id, StandardCharsets.UTF_8);
			}
			if (name != null) {
				name = URLDecoder.decode(name, StandardCharsets.UTF_8);
			}

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

			exchange.sendResponseHeaders(status, response.getBytes().length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(response.getBytes());
			}
		} else {
			exchange.sendResponseHeaders(405, 0);
			exchange.getResponseBody().close();
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
