package movieService.server;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

import com.sun.net.httpserver.HttpExchange;

import movieService.model.User;

public class LoginHandler {
	private final Connection conn;

	// 생성자로 커넥션 주입
	public LoginHandler(Connection conn) {
		this.conn = conn;
	}

	public void handle(HttpExchange exchange) throws IOException {
		String method = exchange.getRequestMethod();

		// CORS 프리플라이트
		if ("OPTIONS".equalsIgnoreCase(method)) {
			exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
			exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
			exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
			exchange.sendResponseHeaders(200, -1);
			exchange.getResponseBody().close();
			return;
		}

		if ("POST".equalsIgnoreCase(method)) {
			exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
			exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
			exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

			String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
			String[] params = body.split("&");
			String id = null;
			String pwStr = null;
			for (String param : params) {
				String[] kv = param.split("=");
				if (kv.length == 2) {
					if (kv[0].equals("id")) {
						id = kv[1];
					}
					if (kv[0].equals("pw")) {
						pwStr = kv[1];
					}
				}
			}

			String response;
			int status = 200;
			try {
				if (id == null || pwStr == null) {
					response = "Missing id or password";
					status = 400;
				} else {
					int pw = Integer.parseInt(pwStr);
					response = User.loginWeb(id, pw, conn);

					if (response.equals("User not found")) {
						status = 404; // 회원 없음
						response = """
								존재하지 않는 회원입니다.
								회원가입을 먼저 진행해주세요!
								""";
					} else if (response.equals("Invalid password")) {
						status = 401; // 인증 실패
						response = """
								비밀번호가 일치하지 않습니다.
								아이디 또는 비밀번호를 다시 확인해주세요.
								""";
					}
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

}
