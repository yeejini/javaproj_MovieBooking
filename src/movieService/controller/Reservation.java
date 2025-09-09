package movieService.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import movieService.model.Movie;
import movieService.model.Seat;
import movieService.model.Theater;
import movieService.model.User;

public class Reservation {

	// 객체 타입
	private User user;
	private Movie movie;
	private Theater theater;

	private String date;
	private String time;
	private int people;

	private String selectedSeat;
	private String scheduleId; // MovieSchedule 참조

	public String getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}

	private String keyId;

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	// 생성자
	public Reservation(String keyId, User user) {
		this.keyId = keyId;
		this.user = user;
	}

	// getter setter
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	public Theater getTheater() {
		return theater;
	}

	public void setTheater(Theater theater) {
		this.theater = theater;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getPeople() {
		return people;
	}

	public void setPeople(int people) {
		this.people = people;
	}

	public String getSeat() {
		return selectedSeat;
	}

	public void setSeat(String seat) {
		this.selectedSeat = seat;
	}

	public static boolean selectTime(Scanner sc, Context<Reservation> reservContext, Connection conn) {
		// 로그인 세션에 임시저장된 id값 가져옴
		String keyId = LoginSession.getCurrentId();

		// 시간+남은 좌석 리스트 출력
		// 앞에서 진행한 reservation객체의 정보 가져오기(극장명, 영화명, 날짜)
		Reservation r = reservContext.getData().get(keyId);
		String theaterId = r.getTheater().getTheaterId();
		String movieId = r.getMovie().getMovieId();
		String date = r.getMovie().getDate();

		int idx = 1;
		ArrayList<String> timeList = new ArrayList<>();
		HashSet<String> addedTime = new HashSet<>(); // 중복 체크용

		System.out.println("\n<시간을 선택하세요>");

		// 사용자가 선택한 극장,영화,날짜와 MovieSchedule리스트에 존재하는 것과 매칭시켜
		// 매칭된 MovieSchedule의 시간+좌석 정보 출력
//		for (MovieSchedule schedule : MovieSchedule.movieS) {
//			if (schedule.getTitle().equals(movie) && schedule.getTheaterName().equals(theater)
//					&& schedule.getDate().equals(date)) {
//				// 이미 추가한 시간이면 건너뜀
//				if (!addedTime.contains(schedule.getTime())) {
//					// 시간별 key 생성
//					String seatKey = movie + "_" + theater + "_" + schedule.getTime();
//					int remainingSeats = Seat.getRemainingSeats(seatKey); // 남은좌석 수
//
//					// 시간 리스트
//					System.out.println(idx + ". " + schedule.getTime() + "(남은좌석 : " + remainingSeats + ")");
//					// 좌석 리스트
//
//					TimeSeatList.add(schedule.getTime());
//					addedTime.add(schedule.getTime());
//					idx++;
//				}
//
//			}
//		}
		try {
			String sql = "SELECT DISTINCT ms.time, ms.schedule_id " + "FROM MovieSchedule ms "
					+ "WHERE ms.movie_id = ? AND ms.theater_id = ? AND ms.date = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, movieId);
				pstmt.setString(2, theaterId);
				pstmt.setString(3, date);
				ResultSet rs = pstmt.executeQuery();

				while (rs.next()) {
					String time = rs.getString("time");
					String scheduleId = rs.getString("schedule_id");

					if (!addedTime.contains(time)) {
						// 좌석 수 계산 (Seat 클래스 or DB 조회)
						int remainingSeats = Seat.getRemainingSeats(scheduleId, conn);

						System.out.println(idx + ". " + time + " (남은좌석 : " + remainingSeats + ")");
						timeList.add(time);
						addedTime.add(time);
						idx++;
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("시간 조회 중 오류 발생: " + e.getMessage());
			return false;
		}

		if (timeList.isEmpty()) {
			System.out.println("해당 날짜에는 상영 시간이 없습니다.");
			return false;
		}

		System.out.println("---------------------");
		System.out.println("0번을 누르면 취소됩니다.");
		System.out.println(); // 마지막에 줄바꿈

		// 시간 선택
		System.out.println("선택>");
		int choice = Integer.parseInt(sc.nextLine());

		if (choice == 0) {
			return false;
		} else {
			String selectTime = timeList.get(choice - 1); // 선택된 날짜
			r.setTime(selectTime);
			// 선택된 시간과 좌석 출력
			System.out.println("선택된 시간: " + selectTime);

			// 좌석 출력
			// System.out.println("선택된 좌석: ");
			return true;
		}

	}

	static int pNum;

	public static int inputPeople(Scanner sc, Context<Reservation> reservContext) {
		String keyId = LoginSession.getCurrentId();
		Reservation r = reservContext.getData().get(keyId);
		// 인원 수 입력

		while (true) {

			System.out.println("\n인원 수를 입력하세요. (숫자로 8명까지만 입력 가능합니다.)");

			int peopleNum = sc.nextInt();
			sc.nextLine();
			pNum = peopleNum;
			if (peopleNum > 8) {
				System.out.println("8명 이하로 설정해야합니다.");
				continue;
			} else {
				r.setPeople(peopleNum);
				return peopleNum;
			}
		}
	}

	public static void submitPayment(Scanner sc, Context<Reservation> reservContext, Seat seatManager,
			Connection conn) {
		// 로그인 세션에 임시저장된 id값 가져옴
		String keyId = LoginSession.getCurrentId();
		Reservation r = reservContext.getData().get(keyId);

		if (r == null) {
			System.out.println("예매 정보가 없습니다.");
			return;
		}

		System.out.println(Reservation.infoTicket(sc, reservContext, conn));

		System.out.println("예매하시겠습니다?");
		System.out.println("1. 예");
		System.out.println("2. 아니오");

		while (true) {
			System.out.println("1. 예");
			System.out.println("2. 아니오");

			String input = sc.nextLine().trim(); // 입력 한 번만 받음
			int n;

			try {
				n = Integer.parseInt(input);
			} catch (NumberFormatException e) {
				System.out.println("\n숫자를 입력하세요>");
				continue; // 다시 while문 처음으로
			}

			if (n == 2) {
				System.out.println("예매가 취소되었습니다. 안녕히가세요.");
				String key = r.getMovie().getTitle() + "_" + r.getTheater().getTheaterName() + "_" + r.getTime();

				if (r != null) {
					r.setMovie(null);
					r.setTheater(null);
					r.setDate(null);
					r.setTime(null);
					r.setPeople(0);
					r.setSeat(null);
				}

				seatManager.cancleSeat(key);
				break;
			} else if (n == 1) {
				System.out.println("예매가 완료되었습니다. 안녕히가세요.");

				break;
			} else {
				System.out.println("번호를 잘못 입력하셨습니다. 다시 입력하세요>");
			}
		}
	}

	// 티켓정보
	public static String infoTicket(Scanner sc, Context<Reservation> reservContext, Connection conn) {
		// 로그인 세션에 임시저장된 id값 가져옴
		String keyId = LoginSession.getCurrentId();
		Reservation r = reservContext.getData().get(keyId);
		// Reservation 객체 자체는 scheduleId, keyId 정도만 있으면 됨
		if (r == null || r.keyId == null) {
			return "티켓 정보가 없습니다.";
		}
		// DB 조회 실패나 조건 불일치 시 기본 메시지를 갖도록 하기 위해
		String reservInfo = "티켓 정보가 없습니다.";
//		String name = r.getUser().getName();
//		String theater = r.getTheater().getTheaterName();
//		String movie = r.getMovie().getTitle();
//		String date = r.getMovie().getDate();
//		String time = r.getTime();
//
//		String seat = r.getSeat();

		// MovieSchedule 리스트에서 매칭되는 schedule 찾기
//		for (MovieSchedule schedule : MovieSchedule.movieS) {
//			if (schedule.getTitle().equals(movie) && schedule.getTheaterName().equals(theater)
//					&& schedule.getDate().equals(date) && schedule.getTime().equals(time)) {
//				screen = schedule.getScreen(); // 상영관 정보 가져오기
//				break;
//			}
//		}

		try {
			String sql = """
					select u.name, t.t_name , m.title , s.s_name, ms.date, ms.time, rs_s.row_num, rs_s.seat_num
					from Reservation r
					join User u on r.user_id = u.user_id
					join movieschedule ms on r.schedule_id = ms.schedule_id
					join theater t on ms.theater_id = t.theater_id
					join movie m on ms.movie_id = m.movie_id
					join screen s on ms.screen_id = s.screen_id
					left join reservationseat rs on r.reserv_id = rs.reserv_id
					left join seat rs_s on rs.seat_id = rs_s.seat_id
					where r.schedule_id = ?
					""";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, r.getScheduleId()); // 스케줄로 조회
				ResultSet rs = stmt.executeQuery();

				// 여러 좌석(row+seat)을 한 줄 문자열로 이어 붙이기 위해 사용
				StringBuilder seatBuilder = new StringBuilder();
				// db에서 한 번에 가져올 각 컬럼값을 저장할 임시변수
				String userName = "", theater = "", movie = "", screen = "", date = "", time = "";

				while (rs.next()) {
					// db에서 모든 값 가져오기
					userName = rs.getString("user_name");
					theater = rs.getString("t_name");
					movie = rs.getString("title");
					screen = rs.getString("s_name");
					date = rs.getString("date");
					time = rs.getString("time");

					// 이어붙이기 위해 안에서 선언
					String row = rs.getString("row_num");
					String seatNum = rs.getString("seat_num");

					// 좌석 값이 null이면 무시
					if (row != null && seatNum != null) {
						seatBuilder.append(row).append(seatNum).append(" ");
					}
				}
				if (!userName.isEmpty()) {
					// 좌석 정보
					reservInfo = """
							    <%s 님의 예약 정보입니다.>
							-------------------------------
							예약자 : %s
							극장명 : %s
							영화명 : %s
							상영관 : %s
							날짜   : %s
							시간   : %s
							인원 수 : %d
							좌석번호: %s


							    """.formatted(userName, userName, theater, movie, screen, date, time, pNum,
							seatBuilder.toString().trim());
				}
			}
		} catch (Exception e) {
			System.err.println("Screen 조회 중 오류 발생: " + e.getMessage());
		}

		return reservInfo;

	}

	// 티켓조회
	public static void issueTicket(Scanner sc, Context<Reservation> reservContext, Seat seatManager, Connection conn) {
		// 로그인 세션에 임시저장된 id값 가져옴
		String keyId = LoginSession.getCurrentId();
		Reservation r = reservContext.getData().get(keyId);

		if (r == null || r.getUser() == null || r.getTheater() == null || r.getMovie() == null || r.getTime() == null) {
			System.out.println("티켓 정보가 없습니다.");
			return;
		}

		// 예매 정보 출력
		System.out.println(infoTicket(sc, reservContext, conn));

		// 취소 여부 확인
		System.out.println("예매를 취소하시려면 0번을 선택하세요. (다른 번호 입력 시 유지됩니다.)");
		System.out.print("선택> ");

		try {
			int choice = Integer.parseInt(sc.nextLine());

			if (choice == 0) {
				String key = r.getMovie().getTitle() + "_" + r.getTheater().getTheaterName() + "_" + r.getTime();

				// 예약 객체 초기화
				r.setMovie(null);
				r.setTheater(null);
				r.setDate(null);
				r.setTime(null);
				r.setPeople(0);
				r.setSeat(null);

				// 좌석 취소
				seatManager.cancleSeat(key);

				System.out.println("예매가 취소되었습니다.");
			} else {
				System.out.println("예매를 유지합니다.");
			}
		} catch (NumberFormatException e) {
			System.out.println("잘못된 입력입니다. 예매를 유지합니다.");
		}
	}

}
