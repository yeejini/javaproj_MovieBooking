package movieService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MakeConnection {
    public static Connection getConnection() {
		// endpoint
		String url = "jdbc:mysql://localhost:3306/madang?serverTimezone=Asia/Seoul";
		Connection conn = null;

		// 1. 드라이버 로드
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("데이터베이스 연결 중...");
			// 2. 데이터베이스 연결
			conn = DriverManager.getConnection(url, "madang", "madang"); // 연결정보
			System.out.println("데이터베이스 연결 성공");

		} catch (ClassNotFoundException e) {
			System.out.println("JDBC 드라이버를 찾지 못했습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("데이터베이스 연결 중 오류발생");
		}
		//

		return conn;
	}
}
