package movieService.model;

import java.security.Key;
import java.util.Scanner;


import movieService.controller.Context;
import movieService.controller.Reservation;

public class User {

	private String id;
	private String name;
	private int pw;

	public User(String id, String name, int pw) {
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
	public static void signUp(Scanner sc, Context<Reservation> reservContext) {
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
				break;
			} else {
				System.out.println("아이디는 문자 4자리만 가능합니다.");
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

		User newUser = new User(id, name, pw);
		String keyId = id;
		Reservation reservation = new Reservation(keyId, newUser);
		reservContext.getData().put(keyId, reservation);

	}

	// 로그인 메서드
	public static void login(Scanner sc, Context<Reservation> reservContext) {
		System.out.println("<로그인 정보 입력>");

		while (true) {
			System.out.println("id를 입력하세요. : ");
			String inputId = sc.nextLine();

			// Map에서 해당 id에 대응되는 User 가져오기
			Reservation reservation = reservContext.getData().get(inputId); // 여기서 실제 User 객체를 가져오는 것
			
			if (reservation == null) {
				System.out.println("일치하는 id가 존재하지 않습니다.");
				continue; //재입력
			} 

			User user = reservation.getUser(); // null 체크 후 안전하게 가져오기
			
				while (true) {
					System.out.println("password를 입력하세요. : ");
					int inputPw = Integer.parseInt(sc.nextLine());

					if (user.getPw() == inputPw) {
						System.out.println(user.getName() + "님 로그인 성공!");
						break;
					} else {
						System.out.println("비밀번호가 일치하지 않습니다.");
						continue;
					}
				}
			
			break;
		}

	}

}
