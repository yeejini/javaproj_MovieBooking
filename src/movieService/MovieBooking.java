package movieService;

import java.util.ArrayList;
import java.util.Scanner;

import movieService.controller.Context;
import movieService.controller.LoginSession;
import movieService.controller.Reservation;
import movieService.data.MovieSchedule;
import movieService.model.Movie;
import movieService.model.Seat;
import movieService.model.Theater;
import movieService.model.User;

public class MovieBooking {

	public static void main(String[] args) {
		// ArrayList<MovieSchedule> ms = new ArrayList<>();


  		Context<Reservation> reservContext = new Context<>();


		String welcomMsg = """
				영화관에 오신 것을 환영합니다 !
				<먼저 회원가입과 로그인을 완료해주세요>
				""";

		String loginMsg = """
				----------------------------------
				  1. 회원가입 | 2. 로그인 | 3. 취소
				----------------------------------
				선택>
				""";

		

		Scanner sc = new Scanner(System.in);

		//welcom 메세지 출력
		System.out.println(welcomMsg);

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
				System.out.println("<예매를 위해서 로그인을 먼저 진행해주세요.>");
				break;
			}

			case "2" -> {
				User.login(sc, reservContext);
				mainMenu(sc, reservContext);
				// run = false;
			}
			default -> System.out.println("메뉴 번호 다시 확인하세요.");
			}
		}

		
		
	}
	
	// 메인 메뉴 기능 로직 : mainMenu
	public static void mainMenu(Scanner sc, Context<Reservation> reservContext){
		String menuMsg = """
				-----------------------------------------------------
				  1. 티켓조회 | 2.영화별예매 | 3.극장별예매 | 4. 로그아웃
				-----------------------------------------------------
				선택>
				""";

		
			Context<Integer[][]> seatContext = new Context<>();
			Seat seatManager = new Seat(seatContext);

		boolean run = true;

		Theater t = new Theater("");
		String theaterName = "";
		Movie m = new Movie();
		String movieName = "";
		String mainMenu = "";

		while (run) {
			System.out.println(menuMsg);
			mainMenu = sc.nextLine();
			if (mainMenu.equals("4")) {
				System.out.println("로그아웃 합니다. 로그인 메뉴로 돌아갑니다.");
				return;
			}
			switch (mainMenu) {
			case "1" -> {
				String ticketInfo = Reservation.issueTicket(sc, reservContext);
				System.out.println(ticketInfo);
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
				while (true) {
				int peopleNum = Reservation.inputPeople(sc,reservContext);
				System.out.println("입렵된 인원 수 : "+ peopleNum);

				// 인원수를 남은좌석에 반영하기 위한 코드 추가*
				Reservation reserv = reservContext.getData().get(LoginSession.getCurrentId());
				int seatnum = Seat.getRemainingSeats(
					reserv.getMovie().getTitle() + "_" + reserv.getTheater().getTheaterName() + "_" + reserv.getTime()
				);
				int result = seatnum - peopleNum;
				if(result<0){
					System.out.println("남은 좌석이 없습니다.");
					continue;
				}else{
				//자리 선택
				seatManager.selectSeat(sc, reservContext);
				//결제
				Reservation.submitPayment(sc, reservContext,seatManager);
				break;
				}
				}
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
				while (true) {
				int peopleNum = Reservation.inputPeople(sc,reservContext);
				System.out.println("입렵된 인원 수 : "+ peopleNum);
				// 인원수를 남은좌석에 반영하기 위한 코드 추가*
				Reservation reserv = reservContext.getData().get(LoginSession.getCurrentId());
				int seatnum = Seat.getRemainingSeats(
					reserv.getMovie().getTitle() + "_" + reserv.getTheater().getTheaterName() + "_" + reserv.getTime()
				);
				int result = seatnum - peopleNum;
				if(result<0){
					System.out.println("남은 좌석이 없습니다.");
					continue;
				}else{
				//자리 선택
				seatManager.selectSeat(sc, reservContext);
				//결제
				Reservation.submitPayment(sc, reservContext,seatManager);

				break;
				}
				}
				break;

			}

			
			default -> System.out.println("메뉴 번호 다시 확인하세요.");
			}
		}
	}
}