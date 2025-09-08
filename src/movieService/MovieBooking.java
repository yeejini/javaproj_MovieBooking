package movieService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import movieService.controller.Context;
import movieService.controller.LoginSession;
import movieService.controller.Reservation;
import movieService.model.Movie;
import movieService.model.Seat;
import movieService.model.Theater;
import movieService.model.User;

public class MovieBooking {

	public static void main(String[] args) throws SQLException {
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
		Connection conn = MakeConnection.getConnection();

		// welcom 메세지 출력
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
				User.signUp(sc, reservContext, conn);
				System.out.println("<예매를 위해서 로그인을 먼저 진행해주세요.>");
				break;
			}

			case "2" -> {
				mainMenu(sc, reservContext, conn);
				User.login(sc, reservContext, conn);

				// run = false;
			}
			default -> System.out.println("메뉴 번호 다시 확인하세요.");
			}
		}

	}

	enum Step {
		MOVIE, THEATER, DATE, TIME, PEOPLE, SEAT, PAYMENT, EXIT
	}


	public static void mainMenu(Scanner sc, Context<Reservation> reservContext, Connection conn) {

		String menuMsg = """
				-----------------------------------------------------
				1. 티켓조회 | 2.영화별예매 | 3.극장별예매 | 4. 로그아웃
				-----------------------------------------------------
				선택>
				""";

		Context<Integer[][]> seatContext = new Context<>();
		Seat seatManager = new Seat(seatContext);

		Theater t = new Theater("");
		Movie m = new Movie();

		while (true) {
			System.out.println(menuMsg);
			String mainMenu = sc.nextLine();

			switch (mainMenu) {
			case "1" -> { // 티켓 조회
				Reservation.issueTicket(sc, reservContext, seatManager);
				// System.out.println(ticketInfo);

			}

			case "2" -> { // 영화별 예매

				runReservation(sc, reservContext, seatManager, m, t, true, mainMenu, conn);
			}

			case "3" -> { // 극장별 예매
				runReservation(sc, reservContext, seatManager, m, t, false, mainMenu, conn);
			}

			case "4" -> { // 로그아웃
				System.out.println("로그아웃 합니다. 로그인 메뉴로 돌아갑니다.");
				return;
			}

			default -> System.out.println("메뉴 번호 다시 확인하세요.");
			}
		}
	}

	private static void runReservation(Scanner sc, Context<Reservation> reservContext, Seat seatManager, Movie m,
			Theater t, boolean movieFirst, // true면 영화별 예매, false면 극장별 예매

			String mainMenu, Connection conn) {

		Step step = movieFirst ? Step.MOVIE : Step.THEATER;
		String theaterName = "";
		String movieName = "";

		while (step != Step.EXIT) {
			switch (step) {

			case MOVIE -> {
				// 영화별예매 선택 시
				if (movieFirst) {
					// 영화 선택
					movieName = m.selectMovie(sc, reservContext, theaterName);
					if (movieName == null) {
						step = Step.EXIT; // 취소 시 메뉴로 돌아감
					} else {
						// 영화별 선택 (영화선택 후 극장 선택 -> selectTheaterTime호출)
						step = Step.THEATER;
					}
					// 극장별 예매일경우(극장 선택됨 -> 영화선택후 날짜)
				} else {
					movieName = m.selectMovie(sc, reservContext, theaterName);
					// 취소시 극장 선택으로 돌아감
					if (movieName == null) {
						step = Step.THEATER;
					} else {
						// 영화 선택까지됨 -> 날짜선택
						step = Step.DATE;
					}

				}
			}

			case THEATER -> {
				if (!movieFirst) {
					// 극장별 예매: 영화 선택 + 시간 선택

					theaterName = t.selectTheater(sc, reservContext, conn);

					if (theaterName == null) {
						step = Step.EXIT; // 취소 시 메뉴로 돌아감
					} else {
						// 극장 선택 시, 영화 선택으로 감
						step = Step.MOVIE;
					}
				} else {
					// 영화별 예매: 영화선택 된 상태로 극장 선택

					boolean ok = Theater.selectTheaterTime(sc, reservContext, movieName, conn);

					step = ok ? Step.DATE : Step.MOVIE;
				}
			}

			case DATE -> {
				boolean ok = Movie.selectDate(sc, reservContext);
				step = ok ? Step.TIME : (movieFirst ? Step.THEATER : Step.MOVIE);
			}

			case TIME -> {
				boolean ok = Reservation.selectTime(sc, reservContext);
				step = ok ? Step.PEOPLE : Step.DATE;
			}

			case PEOPLE -> {
				int peopleNum = Reservation.inputPeople(sc, reservContext);
				System.out.println("입렵된 인원 수 : " + peopleNum);
				// 인원수를 남은좌석에 반영하기 위한 코드 추가*
				Reservation reserv = reservContext.getData().get(LoginSession.getCurrentId());
				int seatnum = Seat.getRemainingSeats(reserv.getMovie().getTitle() + "_"
						+ reserv.getTheater().getTheaterName() + "_" + reserv.getTime());
				int result = seatnum - peopleNum;

				if (result < 0) {
					System.out.println("남은 좌석이 없습니다.");
					continue;
				} else {
					step = Step.SEAT;
				}
			}

			case SEAT -> {
				seatManager.selectSeat(sc, reservContext);
				step = Step.PAYMENT;
			}

			case PAYMENT -> {
				Reservation.submitPayment(sc, reservContext, seatManager);
				step = Step.EXIT;
			}
			}
		}
	}

}
