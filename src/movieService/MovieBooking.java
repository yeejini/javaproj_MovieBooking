package movieService;

import java.util.ArrayList;
import java.util.Scanner;

import movieService.controller.Context;
import movieService.controller.Reservation;
import movieService.data.MovieSchedule;
import movieService.model.Movie;
import movieService.model.Theater;
import movieService.model.User;

public class MovieBooking {

	public static void main(String[] args) {
		ArrayList<MovieSchedule> ms = new ArrayList<>();


  		Context<Reservation> reservContext = new Context<>();


		String loginMsg = """
				영화관에 오신 것을 환영합니다 !
				<먼저 회원가입과 로그인을 완료해주세요>
				-------------------------------
				  1. 회원가입 | 2. 로그인 | 3. 취소
				-------------------------------
				선택>
				""";

		String menuMsg = """
				---------------------------------------------
				  1. 티켓조회 | 2.영화별예매 | 3.극장별예매 | 4. 예매취소
				---------------------------------------------
				선택>
				""";

		Scanner sc = new Scanner(System.in);

		boolean run = true;

		// 회원가입, 로그인 메뉴 기능 로직
		String loginMenu = "";
		while (run) {
			System.out.println(loginMsg);
			loginMenu = sc.nextLine();
			if (loginMenu.equals("3")) {
				System.out.println("안녕히 가세요.");
				return;
			}
			switch (loginMenu) {

			case "1" -> {
				User.signUp(sc, reservContext);
				break;
			}

			case "2" -> {
				User.login(sc, reservContext);
				run = false;
			}
			default -> System.out.println("메뉴 번호 다시 확인하세요.");
			}
		}

		// 메인 메뉴 기능 로직 : mainMenu

		run = true;

		Theater t = new Theater("");
		String theaterName = "";
		Movie m = new Movie();
		String movieName = "";
		String mainMenu = "";

		while (run) {
			System.out.println(menuMsg);
			mainMenu = sc.nextLine();
			if (mainMenu.equals("4")) {
				System.out.println("안녕히 가세요.");
				return;
			}
			switch (mainMenu) {

			case "1" -> {
				User.signUp(sc, reservContext);
				break;
			}

			// 영화별 예매
// 			case "2" -> {
// 				// 영화 선택
// 				movieName = m.selectMovie(sc, reservContext, theaterName);
// //				Movie.selectMovieByTheater(sc, movie, theaterName);
// 				// 극장 선택
// 				Theater.selectTheaterTime(sc, reservContext, movieName);
// 				// 날짜 선택
// 				Movie.selectDate(sc, reservContext, movie);
// 				break;

// 			}
			// 극장별 예매
			case "3" -> {
				// 극장 선택
				theaterName = t.selectTheater(sc, reservContext);
				// 영화 선택
				movieName = m.selectMovie(sc, reservContext, theaterName);

				// 날짜 선택
				Movie.selectDate(sc, reservContext);
				break;

			}

			default -> System.out.println("메뉴 번호 다시 확인하세요.");
			}
		}
	}
}