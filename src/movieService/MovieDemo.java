package movieService;

import java.util.Scanner;

import movieService.controller.Context;
import movieService.controller.Reservation;
import movieService.model.Movie;
import movieService.model.Theater;
import movieService.model.User;

public class MovieDemo {

	public static void main(String[] args) {

		String menuMsg = """
				---------------------------------------------
				  1. 티켓조회 | 2.영화별예매 | 3.극장별예매 | 4. 종료
				---------------------------------------------
				선택>
				""";
		Scanner sc = new Scanner(System.in);
		Context<Reservation> reservContext = new Context<>();

		Context<User> account = new Context<>();
		boolean run = true;

		Theater t = new Theater("");
		String theaterName = "";

		Movie m = new Movie();
		String movieName = "";

		String mainMenu = "";
		while (run != false) {
			System.out.println(menuMsg);
			mainMenu = sc.nextLine();
			if (mainMenu.equals("4")) {
				System.out.println("안녕히 가세요.");
				return; // 프로그램 종료
			}
			switch (mainMenu) {
			// case "1" -> {
			// 	User.signUp(sc, account);
			// 	break;
			// }

			// 영화별 예매
// 			case "2" -> {
// 				// 영화 선택
// 				movieName = m.selectMovie(sc, movie, theaterName);
// //				Movie.selectMovieByTheater(sc, movie, theaterName);
// 				// 극장 선택
// 				Theater.selectTheaterTime(sc, theater, movieName);
// 				// 날짜 선택
// 				Movie.selectDate(sc, theater, movie);
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
