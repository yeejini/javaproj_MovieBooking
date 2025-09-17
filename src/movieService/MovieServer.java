package movieService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;

import com.sun.net.httpserver.HttpServer;

import movieService.server.LoginHandler;
import movieService.server.SignUpHandler;

public class MovieServer {

	public static void main(String[] args) throws Exception {
		// DB 연결 초기화 (실제 서비스라면 커넥션 풀 권장)
		Connection conn = MakeConnection.getConnection();

		// HTTP 서버 시작
		startHttpServer(conn);
	}

	// HTTP 서버 시작 메서드
	private static void startHttpServer(Connection conn) throws IOException {
		int port = 8080;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

		// 핸들러에 DB 커넥션 주입
		server.createContext("/signup", new SignUpHandler(conn));
		server.createContext("/login", new LoginHandler(conn)::handle);

		server.setExecutor(null); // 기본 executor 사용
		System.out.println("HTTP 서버가 " + port + "번 포트에서 시작되었습니다.");
		server.start();
	}
}
