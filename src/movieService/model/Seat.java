package movieService.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import movieService.controller.Context;
import movieService.controller.LoginSession;
import movieService.controller.Reservation;

public class Seat {
	private static Context<Integer[][]> context;// 영화 + 시간별 좌석 정보를 관리하는 context 호출
	private List<String> selectedSeats = new ArrayList<>(); // 선택한 좌석 번호
	private static boolean paymentResult; // 예매 여부를 물은 답을 담은 불리안타입의 변수

	public Seat(Context<Integer[][]> context) { // 생성자
		this.context = context;
	}

	public void addSeat(String seat) { // 사용자가 선택한 좌석 저장할때 사용
		selectedSeats.add(seat);
	}

	public List<String> getSeat() { // 선택한 좌석리스트 반환
		return selectedSeats;
	}

	public void clearSeats() { // 예매 취소시 선택한 좌석 리스트 초기화
		selectedSeats.clear();
	}

	public static void setPaymentResult(boolean result) { // 결제 결과 저장//메인에서 사용함
		paymentResult = result;
	}

	public static boolean getPaymentResult() { // 결제결과 반환
		return paymentResult;
	}

	// 좌석 출력 메서드
	private void printSeats(String key, Integer[][] seats) {

		System.out.println("\n" + key);
		System.out.println();
		System.out.println("------------------Screen------------------");
		System.out.println();

		// 열번호 출력
		System.out.print("       ");
		for (int i = 0; i < seats.length; i++) { // [1],[2]... 이부분 작성
			System.out.print(" [ " + (i + 1) + " ] ");

		}
		System.out.println();

		// 좌석 행 출력
		for (int i = 0; i < seats.length; i++) {
			System.out.println();
			System.out.print(" [ " + (char) (i + 65) + " ] "); // char에서 A=65,B=66,C=67...
			for (int j = 0; j < seats[i].length; j++) { // [i]=A행 B행 C행 , [j]=A행의 1열, B행 3열...
				if (seats[i][j] == null || seats[i][j] == 0) {
					// 좌석이 null값(배열이 안만들어졌거나)이거나 좌석이 비었으면 빈칸으로 채우기
					System.out.print(" [ □ ] ");
				} else {
					// 둘다 아닐경우 있는좌석
					System.out.print(" [ ■ ] ");
				}
			}
			System.out.println();
		}

		System.out.println("\n------------------------------------------");
	}

	public Integer[][] getSeatsFromDB(String scheduleId, Connection conn) {
		final int ROWS = 5; // A~E
		final int COLS = 5; // 1~5
		Integer[][] seats = new Integer[ROWS][COLS];
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				seats[i][j] = 0; // ★ null 방지
			}
		}

		String sql = "SELECT s.row_num, s.seat_num, "
				+ "       COALESCE(MAX(CASE WHEN r.reserv_id IS NOT NULL THEN 1 ELSE 0 END),0) AS reserved "
				+ "FROM Seat s " + "LEFT JOIN ReservationSeat rs ON rs.seat_id = s.seat_id "
				+ "LEFT JOIN Reservation r ON r.reserv_id = rs.reserv_id "
				+ "                         AND r.is_canceled = FALSE "
				+ "                         AND r.schedule_id = ? " + // ★
				"WHERE s.screen_id = (SELECT ms.screen_id FROM MovieSchedule ms WHERE ms.schedule_id = ?) " + // ★
				"GROUP BY s.row_num, s.seat_num " + "ORDER BY s.row_num, s.seat_num";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, scheduleId); // r.schedule_id = ?
			pstmt.setString(2, scheduleId); // screen_id subquery
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					char rowChar = Character.toUpperCase(rs.getString("row_num").charAt(0));
					int row = rowChar - 'A'; // A->0, B->1, ...
					int col = rs.getInt("seat_num") - 1; // 1->0, 5->4
					int reserved = rs.getInt("reserved");

					if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
						seats[row][col] = reserved; // 0 or 1
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("좌석 조회 실패: " + e.getMessage());
		}
		return seats;
	}

	// 좌석 선택 메서드
	public void selectSeat(Scanner sc, Context<Reservation> reservContext, Context<Integer[][]> seatCacheContext,
			Connection conn) {

		clearSeats(); // 좌석 정보 초기화

		// * 로그인세션에서 아이디 값 받아오기 -> 그 아이디값을 리졀베이션에 넣어서 선택한 영화,극장,시간 꺼내옴*/
		String keyId = LoginSession.getCurrentId();
		Reservation reserv = reservContext.getData().get(keyId);

		// 사용자가 앞에서 선택한 영화 스케줄의 Id가져오기
		String scheduleId = reserv.getScheduleId();
		System.out.println("현재스케줄 ID:" + scheduleId);

		// 사용자+스케줄 단위의 캐시 키
		String cacheKey = keyId + ":" + scheduleId;

		// 캐시 조회
		Integer[][] cachedSeats = seatCacheContext.getData().get(cacheKey);

		// 항상 새로운 배열을 생성
		Integer[][] seats;
		if (cachedSeats == null) {
			// DB에서 불러오기
			seats = getSeatsFromDB(scheduleId, conn);
		} else {
			// 캐시가 있으면 기존 배열 복사
			seats = new Integer[cachedSeats.length][cachedSeats[0].length];
			for (int i = 0; i < cachedSeats.length; i++) {
				seats[i] = cachedSeats[i].clone();
			}
		}

		// 캐시에 새 배열 저장 (참조 분리)
		seatCacheContext.getData().put(cacheKey, seats);

		// selectSeat 내부
		printSeats(cacheKey, seats);

		int peonum = reserv.getPeople(); // 선택한 인원수 받아오기
		String strRow; // 입력받은 행이름
		char charRow;// 선택한 행을 char로 바꿀때 사용
		int intRow; // 영어로 된 행을 숫자 값으로 저장할때 사용
		int Col; // 열번호
		String selectedSeat = ""; // 선택된 좌석번호

		// *****좌석 선택****** start
		for (int i = 0; i < peonum; i++) { // 인원수만큼 돌리기
			if (peonum != 1) { // 한명이 아니면 설명을 위해 몇번째 좌석 선택인지 출력
				System.out.println("\n" + "<" + (i + 1) + "번째 좌석선택>");
			}
			while (true) {
				System.out.println("예약하실 좌석의 행을 입력하세요(A~E) : ");
				strRow = sc.nextLine();
				if (strRow.isEmpty()) {
					continue;
				}

				// 공백제거 후 첫번재 글자만 뽑아서 캐릭터로 전환 , A1이렇게 입력할수도 있으니까
				charRow = Character.toUpperCase(strRow.trim().charAt(0));
				if (charRow < 65 || charRow > 69) { // A~E까지
					System.out.println("선택할수 없는 좌석입니다.\n");
					continue;
				}

				intRow = charRow - 65; // 배열인덱스로 변환
				// else 행에 맞는 번호 선택
				System.out.println("예약하실 좌석의 열을 입력하세요(1~5) : ");
				Col = Integer.parseInt(sc.nextLine());
				if (Col < 1 || Col > 5) {
					System.out.println("선택할수 없는 열 번호입니다.\n");
					continue;
				}
				// 좌석 예약 가능 여부 확인
				if (seats[intRow][Col - 1] == null || seats[intRow][Col - 1] == 0) {
//					좌석 예약
					seats[intRow][Col - 1] = 1;

					selectedSeat = "" + charRow + Col;
					this.addSeat(selectedSeat);// 선택한 좌석 리스트에 추가

					// 캐시 갱신 (참조 분리)
					Integer[][] newCacheSeats = new Integer[seats.length][seats[0].length];
					for (int r = 0; r < seats.length; r++) {
						newCacheSeats[r] = seats[r].clone();
					}
					seatCacheContext.getData().put(cacheKey, newCacheSeats);

					// 선택 좌석 출력
					System.out.println("캐시에 저장된 좌석: ");
					for (int r = 0; r < seats.length; r++) {
						for (int c = 0; c < seats[r].length; c++) {
							System.out.print(seats[r][c] + " ");
						}
						System.out.println();
					}

					reserv.setSeat(String.join(",", getSeat())); // reservation에 저장

					break;
				} else {
					System.out.println("이미 예약된 좌석입니다.\n");
				}

			}
		}
		System.out.println();
//		printSeats(key, seats);
		printSeats(cacheKey, seats);
		System.out.println(String.join(",", getSeat()) + "이 선택되셨습니다.\n"); // 선택 확인하기 위해 다시 출력
	}

	// 특정 schedule_id 기준 남은 좌석 수 구하기
	public static int getRemainingSeats(String scheduleId, Connection conn) {
		String sql = "SELECT COUNT(*) AS remaining_seats " + "FROM Seat s " + "WHERE s.screen_id = ( "
				+ "   SELECT ms.screen_id " + "   FROM MovieSchedule ms " + "   WHERE ms.schedule_id = ? ) "
				+ "AND s.seat_id NOT IN ( " + "   SELECT rs.seat_id " + "   FROM ReservationSeat rs "
				+ "   JOIN Reservation r ON rs.reserv_id = r.reserv_id "
				+ "   WHERE r.schedule_id = ? AND r.is_canceled = FALSE )";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, scheduleId);
			pstmt.setString(2, scheduleId);

			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("remaining_seats");
			}
		} catch (SQLException e) {
			System.err.println("남은 좌석 조회 중 오류 발생: " + e.getMessage());
		}
		return 0;
	}

	// 예매 취소 메서드
	public void cancleSeat(String key) {
		Integer[][] seats = context.getData().get(key);
		if (seats == null) {
			return;
		}
		for (String seat : selectedSeats) {
			int col = Character.toUpperCase(seat.charAt(0)) - 65; // A1이렇게 되어있는거 0번째 인덱스만 빼와서 인덱스 번호로 변환
			int row = Integer.parseInt(seat.substring(1)) - 1; // 숫자부분만 가져와서 숫자로 변환
			seats[col][row] = 0; // 좌석을 빈칸으로
		}
		context.getData().put(key, seats); // 저장
		clearSeats(); // 선택한 좌석들 리스트에서 초기화
	}
}
