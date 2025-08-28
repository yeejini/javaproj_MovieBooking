package movieService.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import movieService.controller.Context;
import movieService.controller.LoginSession;
import movieService.controller.Reservation;

public class Seat {

	// 영화 + 시간별 좌석 정보를 관리하는 context 호출
	private static Context<Integer[][]> context;
	private List<String> selectedSeats = new ArrayList<>(); // 선택한 좌석 번호
	private static boolean paymentResult;
	
	public Seat(Context<Integer[][]> context) {
		this.context = context;
	}
	
	public void addSeat(String seat){
		selectedSeats.add(seat);
	}
	public List<String> getSeat(){
		return selectedSeats;
	}
	public void clearSeats(){
		selectedSeats.clear();
	}

	public static void setPaymentResult(boolean result) {
		paymentResult = result;
	}

	public static boolean getPaymentResult() {
		return paymentResult;
	}
		//좌석 출력 메서드
	  private void printSeats(String key, Integer[][] seats) {

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

		System.out.println("\n------------------------------------------");
	}
	//좌석 선택 메서드
	public void selectSeat(Scanner sc, Context<Reservation> reservContext) {
		//* 로그인세션에서 아이디 값 받아오기 -> 그 아이디값을 리졀베이션에 넣어서 선택한 영화,극장,시간 꺼내옴*/
		String keyId = LoginSession.getCurrentId();
		Reservation reserv =reservContext.getData().get(keyId);
		

		// 사용자가 선택한 극장, 영화이름, 시간대 키값으로 사용
		String key = reserv.getMovie().getTitle() + "_" + reserv.getTheater().getTheaterName() + "_" + reserv.getTime();
		// 컨텍스트에서 좌석배열 가져오기, 없으면 새배열 생성

		  // 기존 배열 가져오기, 없으면 새로 만들고 바로 context에 저장
        Integer[][] seats = context.getData().get(key);
        if (seats == null) {
            seats = new Integer[5][5];
            context.getData().put(key, seats);
        }
		printSeats(key, seats);
		int peonum=reserv.getPeople(); //선택한 인원수 받아오기
		
		String strCol; // 입력받은 행이름
		char charCol;// 선택한 행을 char로 바꿀때 사용
		int intCol; // 영어로 된 행을 숫자 값으로 저장할때 사용
		int row; // 열번호
		String selectedSeat = ""; // 선택된 좌석번호

		// *****좌석 선택****** start
		for(int i =0; i<peonum; i++){
			if(peonum!=1){
				System.out.println("\n"+(i+1)+"번째 좌석선택");
			}
			while (true) {
			System.out.println("예약하실 좌석의 행을 입력하세요(A~E) : ");
			strCol = sc.nextLine();
			// 공백제거 후 첫번재 글자만 뽑아서 캐릭터로 전환 , A1이렇게 입력할수도 있으니까
			charCol = Character.toUpperCase(strCol.trim().charAt(0));
			if (charCol < 65 || charCol > 69) {
				System.out.println("선택할수 없는 좌석입니다.");
				continue;
			}
			
			intCol = charCol - 65; // 배열인덱스로 변환
			// else 행에 맞는 번호 선택
				System.out.println("예약하실 좌석의 열을 입력하세요(1~5) : ");
				row = Integer.parseInt(sc.nextLine());
				if (row < 1 || row > 5) {
					System.out.println("선택할수 없는 열 번호입니다.");
					continue;
				}
				  // 좌석 예약 가능 여부 확인
				if (seats[intCol][row - 1] == null || seats[intCol][row - 1] == 0) {
					seats[intCol][row - 1] = 1;

					selectedSeat = "" + charCol + row;
					this.addSeat(selectedSeat);// 선택한 좌석 리스트에 추가
					context.getData().put(key, seats); // 좌석 정보 저장
					reserv.setSeat(String.join(",",getSeat())); //reservation에 저장

					break;
				} else {
					System.out.println("이미 예약된 좌석입니다.");
				}
					
			}
		}
		System.out.println();
		printSeats(key, seats);
		System.out.println(String.join(",",getSeat()) + "이 선택되셨습니다.\n");
		
		
	}

	//남은좌석 출력하는 메서드 
	public static int getRemainingSeats(String key) {
		if(context == null) return 0;
			Integer[][] seats = context.getData().getOrDefault(key, new Integer[5][5]);
			int remaining = 0;
			for (int i = 0; i < seats.length; i++) {
				for (int j = 0; j < seats[i].length; j++) {
					if (seats[i][j] == null || seats[i][j] == 0) remaining++;
				}
			}
			return remaining;
		}

	// 예매 취소 메서드
	public void cancleSeat(String key){
			Integer[][] seats = context.getData().get(key);
			if(seats ==null) return;
		for(String seat :selectedSeats){
				int col = Character.toUpperCase(seat.charAt(0))-65;
				  int row = Integer.parseInt(seat.substring(1)) - 1;
				  seats[col][row] = 0;
			}
			context.getData().put(key, seats);
			clearSeats(); // 선택 좌석 리스트 초기화
		}

		//아니요 하면 선택한 좌석 다시 빈좌석으로 출력하는 부분
		//인원수만큼 좌석 출력
		//남은좌석 수 출력ㅁ

}
