package movieService.model;

import java.util.Scanner;


import movieService.controller.Context;
import movieService.controller.Reservation;
import movieService.data.MovieSchedule;

public class Seat {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Context<Integer[][]> context = new Context<>();
		Context<Reservation> reservContext = new Context<>();

		// 영화 일정 선택 (여기서는 첫 번째 영화로 예시)
		MovieSchedule movie = MovieSchedule.movieS.get(0); // "좀비딸" 14:00
		Seat seat = new Seat(context, movie);
		seat.selectSeat(sc, reservContext);
		seat.selectSeat(sc, reservContext);
	}

	// 영화 + 시간별 좌석 정보를 관리하는 context 호출
	private static Context<Integer[][]> context;

	// 선택된 영화 일정
	private static MovieSchedule movie;

	public Seat(Context<Integer[][]> context, MovieSchedule movie) {
		this.context = context;
		this.movie = movie;
	}

	public static void selectSeat(Scanner sc, Context<Reservation> reservContext) {
		// toString으로 좌석 합쳐서 보여주기
		// 선택된 값을 좌석에 저장해서 선택된 값은 검정네모로 보여지게
		// 취소됐다는 값이 들어오면 검정네모 다시 흰색네모로 바구기

		// 무비스케줄 일정 -> 남은 좌석정보가 value 값이 될거고
		// 사용자가 선택한 값들을 가져옴 -> 리졀베이션에서 가져오면될거고
		// 그 값들을 무비스케줄 무비스에서 매칭시켜서 매칭된 일정을 가져옴 키값으로
		// 그 키값에 value값으로 좌석을 출력하고 선택받고 저장받음
		// reservation -> 선택된 좌석번호만 저장되면 됨

		// *****사용자 아이디 가져오기 // 바꿔야댐
		Reservation result = reservContext.getData().get(0);
		// 사용자가 선택한 극장, 영화이름, 시간대-> 일정에 매칭시킬것임 //바꿔야댐
		String key = movie.getTitle() + "_" + movie.getTheaterName() + "_" + movie.getTime();

		MovieSchedule machedS = null; // 매칭할 변수 생성
		// 선택한 값들이랑 무비스케줄에 있는 일정이랑 매칭시키기
		for (MovieSchedule ms : MovieSchedule.movieS) {
			if (ms.getTitle().equals(movie.getTitle()) && ms.getTheaterName().equals(movie.getTheaterName())
					&& ms.getTime().equals(movie.getTime())) {
				machedS = ms;
				break;
			}
		}

		// 컨텍스트에서 좌석배열 가져오기, 없으면 새배열 생성
		Integer[][] seats = context.getData().getOrDefault(key, new Integer[5][5]);

		String strCol; // 입력받은 행이름
		char charCol;// 선택한 행을 char로 바꿀때 사용
		int intCol; // 영어로 된 행을 숫자 값으로 저장할때 사용
		int row; // 열번호
		String selectedSeat = ""; // 선택된 좌석번호

		// ****좌석 출력 부분**** start
		while (true) {
			System.out.println("\n" + key);
			System.out.println();
			System.out.println("------------------Screen------------------");
			System.out.println();
			System.out.print("       ");
			for (int i = 0; i < seats.length; i++) {
				System.out.print(" [ " + (i + 1) + " ] ");

			}
			System.out.println();
			for (int i = 0; i < seats.length; i++) {
				System.out.println();
				System.out.print(" [ " + (char) (i + 65) + " ] "); // char에서 A=65,B=66,C=67...
				for (int j = 0; j < seats[i].length; j++) { // [i]=A행 B행 C행 , [j]=A행의 1열, B행 3열...
					if (seats[i][j] == null) {
						// 좌석이 null값이면 = 배열이 안만들어져있으면 빈칸으로 채워라
						System.out.print(" [ □ ] ");
					} else if (seats[i][j] == 0) {
						// 좌석이 비어있으면
						System.out.print(" [ □ ] ");
					} else {
						// 둘다 아닐경우 있는좌석
						System.out.print(" [ ■ ] ");
					}
				}
				System.out.println();
			}
			break;
		}
		System.out.println("\n------------------------------------------");
		// ****좌석 출력 부분**** end

		// *****좌석 선택****** start
		while (true) {
			System.out.println("예약하실 좌석의 행을 입력하세요(A~E) : ");
			strCol = sc.nextLine();
			// 공백제거 후 첫번재 글자만 뽑아서 캐릭터로 전환 , A1이렇게 입력할수도 있으니까
			charCol = Character.toUpperCase(strCol.trim().charAt(0));
			System.out.println("입력한 행" + charCol);
			if (charCol < 65 || charCol > 69) {
				System.out.println("선택할수 없는 좌석입니다.");
				continue;
			}
			intCol = charCol - 65; // 배열인덱스로 변환
			// else 행에 맞는 번호 선택
			while (true) {
				System.out.println("예약하실 좌석의 열을 입력하세요(1~5) : ");
				row = Integer.parseInt(sc.nextLine());
				if (row < 1 || row > 5) {
					System.out.println("선택할수 없는 열 번호입니다.");
					continue;
				}
				if (seats[intCol][row - 1] == null || seats[intCol][row - 1] == 0) {
					seats[intCol][row - 1] = 1;
					System.out.println("선택한 행:" + charCol + "선택한 열" + row);
					System.out.println("예약완료");
					selectedSeat = "" + charCol + row;
					break;
				} else if (seats[intCol][row - 1] == 1) {
					System.out.println("이미 예약된 좌석입니다.");
					continue;
				}
			}
			break;
		}
//		if (result != null) {
//			result.setSeat(selectedSeat);
//		} else {
//			System.out.println("예약 객체가 없습니다!");
//		}
//		context.getData().put(key, seats); // 좌석 정보 저장
//
//		result.setSeat(selectSeat);
		// *****좌석 선택****** end

	}

}
