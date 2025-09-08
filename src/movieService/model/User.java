package movieService.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import movieService.controller.Context;
import movieService.controller.LoginSession;
import movieService.controller.Reservation;

public class User {

	private String id;
	private String name;
	private int pw;

	public User(String id, String name, int pw, Connection conn) {
		this.id = id;
		this.name = name;
		this.pw = pw;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getPw() {
		return pw;
	}

	// 회원가입 메서드
	public static void signUp(Scanner sc, Context<Reservation> reservContext, Connection conn) throws SQLException {
		// 아이디 문자 4자리만 가능
		// 비밀번호 숫자 4자리만

		System.out.println("사용자 이름을 입력하세요 : ");
		String name = sc.nextLine();
		// id 와일문
		String id;
		while (true) {
			System.out.println("사용할 아이디를 입력하세요(문자 4자리만 입력가능) : ");
			id = sc.nextLine();
			if (id.matches("^[a-zA-Z]{4}")) {
				// 이미 해당 id가 있는지 확인
				if (reservContext.getData().containsKey(id)) {
					System.out.println("이미 존재하는 아이디입니다. 다시 입력해주세요.");
					// continue → 다시 루프 돌기
				} else {
					// 새 아이디 등록 가능
					break;
				}
			} else {
				System.out.println("아이디 형식이 올바르지 않습니다. 다시 입력해주세요.");
			}
		}

		// pw 와일문
		int pw = 0;
		while (true) {
			System.out.println("사용할 비밀번호를 입력하세요(숫자 4자리만 입력가능) : ");
			try {
				pw = Integer.parseInt(sc.nextLine());
				boolean pwresult = (pw >= 1000 && pw <= 9999); // 숫자가 4자리인지 물어보는 조건
				if (pwresult == true) {
					break;
				} else {
					System.out.println("4자리만 입력 가능합니다.");
				}
			} catch (NumberFormatException e) {
				System.out.println("숫자로만 입력 가능합니다.");
			}
		}
		System.out.println(name + "님, 회원가입이 완료되었습니다.");

		User newUser = new User(id, name, pw, null);
		String keyId = id;
		Reservation reservation = new Reservation(keyId, newUser);
		reservContext.getData().put(keyId, reservation);

		// DB에 INSERT
		String sql = "INSERT INTO User (user_id, name, password) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, id);
			pstmt.setString(2, name);
			pstmt.setInt(3, pw);
			pstmt.executeUpdate();
			System.out.println("DB에도 회원정보가 저장되었습니다.");
		} catch (SQLException e) {
			System.out.println("DB 저장 중 오류 발생: " + e.getMessage());
		}

	}

	// 로그인 메서드
	public static void login(Scanner sc, Context<Reservation> reservContext, Connection conn) throws SQLException {
		System.out.println("<로그인 정보 입력>");

		while (true) {
			System.out.println("id를 입력하세요. : ");
			String inputId = sc.nextLine();

			String sql = "SELECT user_id, name, password FROM User WHERE user_id = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);

			try (pstmt) {
				pstmt.setString(1, inputId);
				ResultSet rs = pstmt.executeQuery();

				if (!rs.next()) {
					System.out.println("일치하는 id가 존재하지 않습니다.");
					continue; // 재입력
				}

				String dbId = rs.getString("user_id");
				String dbName = rs.getString("name");
				int dbPw = rs.getInt("password");

				// Map에서 해당 id에 대응되는 User 가져오기
//				Reservation reservation = reservContext.getData().get(inputId); // 여기서 실제 User 객체를 가져오는 것
				//
//				if (reservation == null) {
//					System.out.println("일치하는 id가 존재하지 않습니다.");
//					continue; // 재입력
//				}

//				User user = reservation.getUser(); // null 체크 후 안전하게 가져오기

				while (true) {
					System.out.println("password를 입력하세요. : ");
					try {
						int inputPw = Integer.parseInt(sc.nextLine());
						if (dbPw == inputPw) {
							System.out.println(dbName + "님 로그인 성공!");
							// 외부에서 고유 id값 key로 전달받아 사용하기 위함
							LoginSession.setCurrentId(inputId); // 로그인 완료 시 그 id값 로그인세션에 저장
							break;
						} else {
							System.out.println("비밀번호가 일치하지 않습니다.");
							continue;
						}
					} catch (NumberFormatException e) {
						System.err.println("숫자만 입력 가능합니다.");
					}

				}

				break; // 로그인 성공 시 전체 while 종료
			} catch (SQLException e) {
				System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
			}

		}

	}

}
