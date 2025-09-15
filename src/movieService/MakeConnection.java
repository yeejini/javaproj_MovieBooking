package movieService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MakeConnection {
    public static Connection getConnection() {
         // 하드코딩된 DB 정보
        String host = "moviemysql";   // 컨테이너 이름
        String port = "3306";
        String dbName = "movieservice";
        String user = "root";
        String password = "1111";
        
        // String url = "jdbc:mysql://testmysql:3306/movieservice?serverTimezone=Asia/Seoul";
        // String url = "jdbc:mysql://host.docker.internal:3377/movieservice?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true";
        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName +
                     "?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";

       

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("❌ MySQL 드라이버 로드 실패", e);
        }

        for (int i = 0; i < 15; i++) { // 최대 15번 재시도
            try {
                Connection conn = DriverManager.getConnection(url, user, password);
                System.out.println("✅ DB 연결 성공");
                return conn;
            } catch (SQLException e) {
                System.out.println("⏳ DB 연결 재시도 중... " + (i + 1));
                try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
            }
        }

        throw new RuntimeException("❌ DB 연결 실패");
    }
}
