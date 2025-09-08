package movieService.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import movieService.data.MovieSchedule;
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

	public static boolean selectTime(Scanner sc, Context<Reservation> reservContext) {
		// 로그인 세션에 임시저장된 id값 가져옴
		String keyId = LoginSession.getCurrentId();

		// 시간+남은 좌석 리스트 출력
		// 앞에서 진행한 reservation객체의 정보 가져오기(극장명, 영화명, 날짜)
		Reservation r = reservContext.getData().get(keyId);
		String theater = r.getTheater().getTheaterName();
		String movie = r.getMovie().getTitle();
		String date = r.getMovie().getDate();

		int idx = 1;
		ArrayList<String> TimeSeatList = new ArrayList<>();
		HashSet<String> addedTime = new HashSet<>(); // 중복 체크용

		System.out.println("\n<시간을 선택하세요>");

		// 사용자가 선택한 극장,영화,날짜와 MovieSchedule리스트에 존재하는 것과 매칭시켜
		// 매칭된 MovieSchedule의 시간+좌석 정보 출력
		for (MovieSchedule schedule : MovieSchedule.movieS) {
			if (schedule.getTitle().equals(movie) && schedule.getTheaterName().equals(theater)
					&& schedule.getDate().equals(date)) {
				// 이미 추가한 시간이면 건너뜀
				if (!addedTime.contains(schedule.getTime())) {
					// 시간별 key 생성
					String seatKey = movie + "_" + theater + "_" + schedule.getTime();
					int remainingSeats = Seat.getRemainingSeats(seatKey); // 남은좌석 수

					// 시간 리스트
					System.out.println(idx + ". " + schedule.getTime() + "(남은좌석 : " + remainingSeats + ")");
					// 좌석 리스트

					TimeSeatList.add(schedule.getTime());
					addedTime.add(schedule.getTime());
					idx++;
				}

			}
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
			String selectTime = TimeSeatList.get(choice - 1); // 선택된 날짜
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

	public static void submitPayment(Scanner sc, Context<Reservation> reservContext, Seat seatManager) {
		// 로그인 세션에 임시저장된 id값 가져옴
		String keyId = LoginSession.getCurrentId();

		System.out.println(Reservation.infoTicket(sc, reservContext));

		System.out.println("예매하시겠습니다?");

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
				// Reservation 객체 가져오기
				Reservation r = reservContext.getData().get(keyId);
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
	public static String infoTicket(Scanner sc, Context<Reservation> reservContext) {
		// 로그인 세션에 임시저장된 id값 가져옴
		String keyId = LoginSession.getCurrentId();
		Reservation r = reservContext.getData().get(keyId);
		// Reservation 객체나 필드가 null인지 체크
		if (r == null || r.getUser() == null || r.getTheater() == null || r.getMovie() == null || r.getTime() == null) {
			return "티켓 정보가 없습니다.";
		}
		String name = r.getUser().getName();
		String theater = r.getTheater().getTheaterName();
		String movie = r.getMovie().getTitle();
		String date = r.getMovie().getDate();
		String time = r.getTime();

		String seat = r.getSeat();

		// MovieSchedule 리스트에서 매칭되는 schedule 찾기
		String screen = ""; // 기본값
		for (MovieSchedule schedule : MovieSchedule.movieS) {
			if (schedule.getTitle().equals(movie) && schedule.getTheaterName().equals(theater)
					&& schedule.getDate().equals(date) && schedule.getTime().equals(time)) {
				screen = schedule.getScreen(); // 상영관 정보 가져오기
				break;
			}
		}

		// 좌석 정보

		String reservInfo = """
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


				    """.formatted(name, name, theater, movie, screen, date, time, pNum, seat);

		return reservInfo;

	}

	// 티켓조회
	public static void issueTicket(Scanner sc, Context<Reservation> reservContext, Seat seatManager) {
		// 로그인 세션에 임시저장된 id값 가져옴
		String keyId = LoginSession.getCurrentId();
		Reservation r = reservContext.getData().get(keyId);

		if (r == null || r.getUser() == null || r.getTheater() == null || r.getMovie() == null || r.getTime() == null) {
			System.out.println("티켓 정보가 없습니다.");
			return;
		}

		// 예매 정보 출력
		System.out.println(infoTicket(sc, reservContext));

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
