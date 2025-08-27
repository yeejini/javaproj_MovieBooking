package movieService;

import java.util.Scanner;

import movieService.controller.Context;
import movieService.controller.LoginSession;
import movieService.controller.Reservation;
import movieService.model.Movie;
import movieService.model.Seat;
import movieService.model.Theater;
import movieService.model.User;

public class MovieDemo {

	public static void main(String[] args) {
		String id = "kang";
		String name = "강예진";
		int pw = 1234;

		Context<Reservation> reservContext = new Context<>();

		User newUser = new User(id, name, pw);
		String keyId = id;
		Reservation reservation = new Reservation(keyId, newUser);
		reservContext.getData().put(keyId, reservation);

		LoginSession.setCurrentId(keyId); //로그인 완료 시 그 id값 로그인세션에 저장



		String menuMsg = """
				---------------------------------------------
				  1. 티켓조회 | 2.영화별예매 | 3.극장별예매 | 4. 종료
				---------------------------------------------
				선택>
				""";
		Scanner sc = new Scanner(System.in);

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
			case "1" -> {
				Reservation.issueTicket(sc, reservContext);
				break;
			}

			// 영화별 예매
			case "2" -> {
				// 영화 선택
				movieName = m.selectMovie(sc, reservContext, theaterName);
//				Movie.selectMovieByTheater(sc, movie, theaterName);
				// 극장 선택
				Theater.selectTheaterTime(sc, reservContext, movieName);
				// 날짜 선택
				Movie.selectDate(sc, reservContext);
				//시간 선택
				Reservation.selectTime(sc, reservContext);
				//인원수 입력
				int peopleNum = Reservation.inputPeople(sc,reservContext);
				System.out.println("입렵된 인원 수 : "+ peopleNum);
				//자리 선택

				//결제
				Reservation.submitPayment(sc, reservContext);
				break;

			}
			// 극장별 예매
			case "3" -> {
				// 극장 선택
				theaterName = t.selectTheater(sc, reservContext);
				// 영화 선택
				movieName = m.selectMovie(sc, reservContext, theaterName);

				// 날짜 선택
				Movie.selectDate(sc, reservContext);

				//시간 선택
				Reservation.selectTime(sc, reservContext);
				//인원수 입력
				int peopleNum = Reservation.inputPeople(sc,reservContext);
				System.out.println("입렵된 인원 수 : "+ peopleNum);
				
				//자리 선택

				//결제
				Reservation.submitPayment(sc, reservContext);

				break;

			}

			
			default -> System.out.println("메뉴 번호 다시 확인하세요.");
			}
		}
		
		
	}


}