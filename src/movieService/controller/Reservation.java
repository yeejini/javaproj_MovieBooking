package movieService.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

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
		HashMap<String, String> timeToScheduleMap = new HashMap<>(); // time -> schedule_id 매핑
		HashMap<String, Integer> timeToRemainingSeats = new HashMap<>(); // 시간 -> 남은 좌석

		System.out.println("\n<시간을 선택하세요>");

		// 사용자가 선택한 극장,영화,날짜와 MovieSchedule리스트에 존재하는 것과 매칭시켜

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
						// DB 조회 후 while문에서 저장
						timeToRemainingSeats.put(time, remainingSeats);

						System.out.println(idx + ". " + time + " (남은좌석 : " + remainingSeats + ")");
						timeList.add(time);
						addedTime.add(time);

						// schedule_id 매핑 추가
						timeToScheduleMap.put(time, scheduleId);

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
		// 시간 선택 반복
		while (true) {
			System.out.print("선택> ");
			int choice = Integer.parseInt(sc.nextLine());

			if (choice == 0) {
				return false; // 취소
			} else if (choice < 1 || choice > timeList.size()) {
				System.out.println("잘못된 선택입니다. 다시 입력하세요.");
			} else {
				String selectedTime = timeList.get(choice - 1);
				int remainingSeats = timeToRemainingSeats.get(selectedTime);

				if (remainingSeats == 0) {
					System.out.println("현재 남은 좌석이 없습니다. 다른 시간을 선택하세요.");
				} else {
					String selectedScheduleId = timeToScheduleMap.get(selectedTime);
					r.setTime(selectedTime);
					r.setScheduleId(selectedScheduleId);
					System.out.println("선택된 시간: " + selectedTime);
					System.out.println("저장된 schedule_id: " + selectedScheduleId);
					return true; // 올바른 선택 완료
				}
			}
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

	public static boolean submitPayment(Scanner sc, Context<Reservation> reservContext, Seat seatManager,
			Context<Integer[][]> seatCacheContext, Connection conn) {

		String keyId = LoginSession.getCurrentId();
		Reservation r = reservContext.getData().get(keyId);

		if (r == null) {
			System.out.println("예매 정보가 없습니다.");
			return false;
		}

		System.out.println(Reservation.infoTicket(sc, reservContext, seatCacheContext));

		System.out.println("예매하시겠습니다?");

		while (true) {
			System.out.println("1. 예");
			System.out.println("2. 아니오");

			String input = sc.nextLine().trim();
			int n;

			try {
				n = Integer.parseInt(input);
			} catch (NumberFormatException e) {
				System.out.println("\n숫자를 입력하세요>");
				continue;
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
				return false; // 취소 처리 시 false 반환

			} else if (n == 1) {

				PreparedStatement pstmtSeat = null;
				PreparedStatement pstmtReserv = null;
				PreparedStatement pstmtReservSeat = null;

				try {
					conn.setAutoCommit(false); // 트랜잭션 시작

					// ----------------- 1. Seat 테이블 업데이트 -----------------
					String[] seats = r.getSeat().split(","); // "A1,A2" -> ["A1","A2"]

					// scheduleId로 screen_id 조회 필요
					String screenId = null;
					String getScreenSql = "SELECT screen_id FROM MovieSchedule WHERE schedule_id = ?";
					try (PreparedStatement pstmtScreen = conn.prepareStatement(getScreenSql)) {
						pstmtScreen.setString(1, r.getScheduleId());
						ResultSet rsScreen = pstmtScreen.executeQuery();

						if (rsScreen.next()) {
							screenId = rsScreen.getString("screen_id");
						}

						if (screenId == null) {
							throw new SQLException("해당 scheduleId에 대한 screen_id를 찾을 수 없습니다.");
						}

						// 2. 여러 좌석 한 번에 잠금
						StringBuilder placeholders = new StringBuilder();
						for (int i = 0; i < seats.length; i++) {
							placeholders.append("(?, ?)");
							if (i < seats.length - 1) {
								placeholders.append(", ");
							}
						}
						String lockSeatSql = "SELECT * FROM Seat WHERE screen_id = ? AND (row_num, seat_num) IN ("
								+ placeholders + ") FOR UPDATE";

						try (PreparedStatement pstmtLock = conn.prepareStatement(lockSeatSql)) {
							pstmtLock.setString(1, screenId);
							int index = 2;
							for (String seatStr : seats) {
								String row = seatStr.substring(0, 1);
								int seatNum = Integer.parseInt(seatStr.substring(1));
								pstmtLock.setString(index++, row);
								pstmtLock.setInt(index++, seatNum);
							}

							ResultSet rsLock = pstmtLock.executeQuery();

							Set<String> lockedSeats = new HashSet<>();
							while (rsLock.next()) {
								String row = rsLock.getString("row_num");
								int num = rsLock.getInt("seat_num");
								lockedSeats.add(row + num);
								if (!rsLock.getBoolean("is_seats")) {
									System.out.println("이미 예약된 좌석입니다: " + row + num);
									seatManager.selectSeat(sc, reservContext, seatCacheContext, conn);
									return false; // 이미 예약된 좌석이면 false
								}
							}

							// DB에 없는 좌석 체크
							for (String seatStr : seats) {
								if (!lockedSeats.contains(seatStr)) {
									throw new SQLException("좌석 정보를 찾을 수 없습니다: " + seatStr);
								}
							}
						}

						// 3. 좌석 상태 업데이트
						String updateSeatSql = "UPDATE Seat SET is_seats = FALSE WHERE screen_id = ? AND row_num = ? AND seat_num = ?";
						pstmtSeat = conn.prepareStatement(updateSeatSql);

						for (String seatStr : seats) {
							String row = seatStr.substring(0, 1);
							int seatNum = Integer.parseInt(seatStr.substring(1));
							pstmtSeat.setString(1, screenId);
							pstmtSeat.setString(2, row);
							pstmtSeat.setInt(3, seatNum);
							pstmtSeat.addBatch();
						}
						pstmtSeat.executeBatch();
					}

					// ----------------- 4. Reservation 테이블 삽입 -----------------
					String insertReservSql = "INSERT INTO Reservation (user_id, schedule_id) VALUES (?, ?)";
					pstmtReserv = conn.prepareStatement(insertReservSql, Statement.RETURN_GENERATED_KEYS);
					pstmtReserv.setString(1, keyId);
					pstmtReserv.setString(2, r.getScheduleId());
					pstmtReserv.executeUpdate();

					ResultSet rs = pstmtReserv.getGeneratedKeys();
					int reservId = 0;
					if (rs.next()) {
						reservId = rs.getInt(1);
					}

					// ----------------- 5. ReservationSeat 테이블 삽입 -----------------
					String insertReservSeatSql = "INSERT INTO ReservationSeat (reserv_id, seat_id) VALUES (?, ?)";

					// ----------------- 3. ReservationSeat 테이블 삽입 -----------------

					pstmtReservSeat = conn.prepareStatement(insertReservSeatSql);

					for (String seatStr : seats) {
						String seatId = screenId + "-" + seatStr; // S1-A1
						pstmtReservSeat.setInt(1, reservId);
						pstmtReservSeat.setString(2, seatId);
						pstmtReservSeat.addBatch();
					}
					pstmtReservSeat.executeBatch();

					conn.commit(); // **변경**: commit 한 번만 호출
					// 사용자+스케줄 단위의 캐시 키

					System.out.println("예매가 완료되었습니다. 안녕히 가세요.");

					// 사용자+스케줄 단위의 캐시 키
					String cacheKey = keyId + ":" + r.getScheduleId();
					System.out.println("삭제할 캐시 키: " + cacheKey);
					System.out.println("삭제 전 캐시 존재 여부: " + seatCacheContext.getData().containsKey(cacheKey));

					// 캐시 삭제
					seatCacheContext.getData().remove(cacheKey);

					System.out.println("삭제 후 캐시 존재 여부: " + seatCacheContext.getData().containsKey(cacheKey));

					return true; // 결제 성공 시 true 반환

				} catch (SQLException e) {
					try {
						conn.rollback(); // 문제 발생 시 롤백
						System.out.println("예매 처리 중 오류 발생, 롤백되었습니다.");
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
					e.printStackTrace();
				} finally {
					try {
						if (pstmtSeat != null) {
							pstmtSeat.close();
						}
					} catch (SQLException e) {
					}
					try {
						if (pstmtReserv != null) {
							pstmtReserv.close();
						}
					} catch (SQLException e) {
					}
					try {
						if (pstmtReservSeat != null) {
							pstmtReservSeat.close();
						}
					} catch (SQLException e) {
					}
					try {
						conn.setAutoCommit(true);
					} catch (SQLException e) {
					}
				}

				break;

			} else {
				System.out.println("번호를 잘못 입력하셨습니다. 다시 입력하세요>");
			}
		}
		return false;
	}

	// 티켓정보
	// infoticket: 임시 저장값 기반 티켓 정보
	public static String infoTicket(Scanner sc, Context<Reservation> reservContext,
			Context<Integer[][]> seatCacheContext) {
		String keyId = LoginSession.getCurrentId();
		if (keyId == null || keyId.isEmpty()) {
			return "로그인 정보가 없습니다.";
		}

		Reservation r = reservContext.getData().get(keyId);
		if (r == null) {
			return "예매 정보가 없습니다.";
		}

		String userName = LoginSession.getCurrentId();
		String theaterName = r.getTheater().getTheaterName();
		String movieTitle = r.getMovie().getTitle();
		String date = r.getMovie().getDate();
		String time = r.getTime();
		int people = r.getPeople();
		String seatStr = r.getSeat();

// 좌석 캐시 가져오기
		String cacheKey = keyId + ":" + (r.getScheduleId() != null ? r.getScheduleId() : "");
		Integer[][] seats = seatCacheContext.getData().get(cacheKey);

		String info = String.format("""
				<%s 님의 임시 예약 정보입니다.>
				-------------------------------
				예약자 : %s
				극장명 : %s
				영화명 : %s
				날짜   : %s
				시간   : %s
				인원 수 : %d
				좌석번호: %s
				""", userName, userName, theaterName, movieTitle, date, time, people, seatStr);

		if (seats != null) {
			StringBuilder seatLayout = new StringBuilder("\n=== 좌석 배치 ===\n");
			for (int i = 0; i < seats.length; i++) {
				seatLayout.append((char) ('A' + i)).append("  ");
				for (int j = 0; j < seats[i].length; j++) {
					seatLayout.append((seats[i][j] != null && seats[i][j] == 1) ? "[■]" : "[□]");
				}
				seatLayout.append("\n");
			}
			seatLayout.append("-------------------------------\n");
			info += seatLayout.toString();
		}

		return info;
	}

	// 티켓조회
	public static void issueTicket(Scanner sc, Connection conn) {
		// 로그인 세션에 임시저장된 id값 가져옴
		String keyId = LoginSession.getCurrentId();
		// 1. 로그인 세션 확인
		if (keyId == null || keyId.isEmpty()) {
			System.out.println("로그인 정보가 없습니다. 먼저 로그인해주세요.");
			return;
		}

		// 2. DB 연결 확인
		if (conn == null) {
			System.out.println("DB 연결이 없습니다.");
			return;
		}

		String sql = """
				    select r.reserv_id, u.name as user_name, t.t_name, m.title, ms.date, ms.time, ms.screen_id,
				           group_concat(concat(se.row_num, se.seat_num)SEPARATOR ',') as seats, count(*) as people
				    from reservation r
				    join user u on r.user_id = u.user_id
				    join movieschedule ms on r.schedule_id = ms.schedule_id
				    join theater t on ms.theater_id = t.theater_id
				    join movie m on ms.movie_id = m.movie_id
				    join reservationseat rs on r.reserv_id = rs.reserv_id
				    join seat se on rs.seat_id = se.seat_id
				    where r.user_id = ?
				    group by r.reserv_id, u.name, t.t_name, m.title, ms.date, ms.time, ms.screen_id
				""";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, keyId);
			ResultSet rs = stmt.executeQuery();
			if (!rs.next()) {
				System.out.println("예매 정보가 없습니다.");
				return;
			}
			// 안전하게 컬럼 읽기
			int reservId = rs.getInt("reserv_id");
			String userName = rs.getString("user_name") != null ? rs.getString("user_name") : ""; // **변경: null 처리**
			String theaterName = rs.getString("t_name") != null ? rs.getString("t_name") : ""; // **변경: null
																								// 처리**
			String movieTitle = rs.getString("title") != null ? rs.getString("title") : ""; // **변경: null
																							// 처리**
			String date = rs.getString("date") != null ? rs.getString("date") : ""; // **변경: null 처리**
			String time = rs.getString("time") != null ? rs.getString("time") : ""; // **변경: null 처리**
			String screenId = rs.getString("screen_id") != null ? rs.getString("screen_id") : ""; // **변경: null 처리**
			String seats = rs.getString("seats") != null ? rs.getString("seats") : ""; // **변경: null 처리**
			int people = rs.getInt("people"); // **변경: count(*)로 int 바로 가져오기**

			// 티켓 출력
			String ticketInfo = """
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
					""".formatted(userName, userName, theaterName, movieTitle, screenId, date, time, people, seats);

			System.out.println(ticketInfo);

			// 취소 여부 확인
			System.out.println("예매를 취소하시려면 0번을 선택하세요. (다른 번호 입력 시 유지됩니다.)");
			System.out.print("선택> ");
			int choice;
			try {
				choice = Integer.parseInt(sc.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("잘못된 입력입니다. 예매를 유지합니다.");
				return;
			}

			if (choice == 0) {
				try {
					conn.setAutoCommit(false);

					// 1.reservation seat 삭제
					String delReservSeat = "delete from reservationseat where reserv_id = ?";
					try (PreparedStatement ps1 = conn.prepareStatement(delReservSeat)) {
						ps1.setInt(1, reservId);
						ps1.executeUpdate(); // **
					}

					// 2. reservation 삭제
					String delReserve = "delete from reservation where reserv_id = ?";
					try (PreparedStatement ps2 = conn.prepareStatement(delReserve)) {
						ps2.setInt(1, reservId);
						ps2.executeUpdate();
					}

					// 3. seat원복(is_seat== true)
					String[] seatArr = seats.isEmpty() ? new String[0] : seats.split(",");
					String updateSeat = "UPDATE Seat SET is_seats = TRUE WHERE screen_id = ? AND row_num = ? AND seat_num = ?";
					try (PreparedStatement ps3 = conn.prepareStatement(updateSeat)) {
						for (String s : seatArr) { // **
							if (s.isEmpty()) {
								continue; // 안전하게 skip
							}
							String row = s.substring(0, 1); // **
							int seatNum = Integer.parseInt(s.substring(1)); // **
							ps3.setString(1, screenId);
							ps3.setString(2, row);
							ps3.setInt(3, seatNum);
							ps3.addBatch(); // **
						}
						ps3.executeBatch(); // **
					}
					conn.commit();// **
					System.out.println("예매가 취소되었습니다.");

				} catch (SQLException e) {
					try {
						conn.rollback();
						System.out.println("예매 취소 중 오류 발생");
					} catch (SQLException e2) {
						e2.printStackTrace();
					}
					e.printStackTrace();
				} finally { // **
					try {
						conn.setAutoCommit(true);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			} else {
				System.out.println("예매를 유지합니다.");
			}

		} catch (SQLException e) {
			System.out.println("티켓 조회 중 오류 발생 : " + e.getMessage());
			e.printStackTrace();
		}
//		Reservation r = reservContext.getData().get(keyId);
//
//		if (r == null || r.getUser() == null || r.getTheater() == null || r.getMovie() == null || r.getTime() == null) {
//			System.out.println("티켓 정보가 없습니다.");
//			return;
//		}
//
//		// 예매 정보 출력
//		System.out.println(infoTicket(sc, reservContext));
//
//		// 취소 여부 확인
//		System.out.println("예매를 취소하시려면 0번을 선택하세요. (다른 번호 입력 시 유지됩니다.)");
//		System.out.print("선택> ");
//
//		try {
//			int choice = Integer.parseInt(sc.nextLine());
//
//			if (choice == 0) {
//				String key = r.getMovie().getTitle() + "_" + r.getTheater().getTheaterName() + "_" + r.getTime();
//
//				// 예약 객체 초기화
//				r.setMovie(null);
//				r.setTheater(null);
//				r.setDate(null);
//				r.setTime(null);
//				r.setPeople(0);
//				r.setSeat(null);
//
//				// 좌석 취소
//				seatManager.cancleSeat(key);
//
//				System.out.println("예매가 취소되었습니다.");
//			} else {
//				System.out.println("예매를 유지합니다.");
//			}
//		} catch (NumberFormatException e) {
//			System.out.println("잘못된 입력입니다. 예매를 유지합니다.");
//		}
////	
//		
//		try {
//			int choice = Integer.parseInt(sc.nextLine());
//			if(choice == 0) {
//				
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//
//}
	}
}
