package movieService;

import java.util.ArrayList;
import java.util.Scanner;

import movieService.controller.Context;
import movieService.data.MovieSchedule;
import movieService.model.Movie;
import movieService.model.User;

public class MovieBooking {

	public static void main(String[] args) {
		ArrayList<MovieSchedule> ms = new ArrayList<>();
//		for (MovieSchedule schedule : MovieSchedule.movieS) {
//			System.out.println(schedule.getTitle());
//
//		}

		String loginMsg = """
				영화관에 오신 것을 환영합니다 !
				<먼저 회원가입과 로그인을 완료해주세요>
				-------------------------------
				  1. 회원가입 | 2. 로그인 | 3. 종료
				-------------------------------
				선택>
				""";
		String menuMsg = """
				---------------------------------------------
				  1. 티켓조회 | 2.영화별예매 | 3.극장별예매 | 4. 종료
				---------------------------------------------
				선택>
				""";
		Scanner sc = new Scanner(System.in);

		Context<User> account = new Context<>();

		boolean run = true;

		// 회원가입, 로그인 메뉴 기능 로직 : loginMenu
		String loginMenu = "";
		while (run != false) {
			System.out.println(loginMsg);
			loginMenu = sc.nextLine();
			if (loginMenu.equals("3")) {
				System.out.println("안녕히 가세요.");
				break;
			}
			switch (loginMenu) {
			case "1" -> {
				User.signUp(sc, account);
				break;
			}

			case "2" -> {
				User.login(sc, account);
				run = false;
				break;

			}

			default -> System.out.println("메뉴 번호 다시 확인하세요.");
			}
		}

		// 회원가입, 로그인 메뉴 기능 로직 : mainMenu
		String mainMenu = "";
		while (run != false) {
			System.out.println(menuMsg);
			mainMenu = sc.nextLine();
			if (mainMenu.equals("4")) {
				System.out.println("안녕히 가세요.");
				break;
			}
			switch (mainMenu) {
			case "1" -> {
				User.signUp(sc, account);
				break;
			}

			case "2" -> {
				Movie.selectMovie(sc, account);
				break;

			}
			case "3" -> {

				break;

			}
			default -> System.out.println("메뉴 번호 다시 확인하세요.");
			}
		}

	}

}
