package movieService.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import movieService.controller.Context;
import movieService.controller.LoginSession;
import movieService.controller.Reservation;

public class Seat {
	private static Context<Integer[][]> context;// 영화 + 시간별 좌석 정보를 관리하는 context 호출
	private List<String> selectedSeats = new ArrayList<>(); // 선택한 좌석 번호
	private static boolean paymentResult; // 예매 여부를 물은 답을 담은 불리안타입의 변수 
	
	public Seat(Context<Integer[][]> context) { //생성자
		this.context = context;
	}
	
	public void addSeat(String seat){ //사용자가 선택한 좌석 저장할때 사용
		selectedSeats.add(seat);
	}
	public List<String> getSeat(){ // 선택한 좌석리스트 반환
		return selectedSeats;
	}
	public void clearSeats(){ // 예매 취소시 선택한 좌석 리스트 초기화
		selectedSeats.clear();
	}
	public static void setPaymentResult(boolean result) { //결제 결과 저장//메인에서 사용함
		paymentResult = result;
	}
	public static boolean getPaymentResult() { // 결제결과 반환
		return paymentResult;
	}

	//좌석 출력 메서드
	private void printSeats(String key, Integer[][] seats) {

			System.out.println("\n" + key);
			System.out.println();
			System.out.println("------------------Screen------------------");
			System.out.println();
			System.out.print("       ");
			for (int i = 0; i < seats.length; i++) { // [1],[2]... 이부분 작성
				System.out.print(" [ " + (i + 1) + " ] ");

			}
			System.out.println();
			for (int i = 0; i < seats.length; i++) {
				System.out.println();
				System.out.print(" [ " + (char) (i + 65) + " ] "); // char에서 A=65,B=66,C=67...
				for (int j = 0; j < seats[i].length; j++) { // [i]=A행 B행 C행 , [j]=A행의 1열, B행 3열...
					if (seats[i][j] == null || seats[i][j]==0) {
						// 좌석이 null값(배열이 안만들어졌거나)이거나 좌석이 비었으면 빈칸으로 채우기
						System.out.print(" [ □ ] ");
					}else {
						// 둘다 아닐경우 있는좌석
						System.out.print(" [ ■ ] ");
					}
				}
				System.out.println();
			}

		System.out.println("\n------------------------------------------");
	}
	//랜덤으로 좌석 채우는 메서드
	private Integer[][] createRandomSeat(int rows, int cols){
		Integer[][] seats = new Integer[rows][cols]; // 좌석 배열 초기화 
		Random random = new Random();

		int preReservedCount = random.nextInt(11); // 0부터 10까지 랜덤으로 예약개수 설정해놓은거
		int filled =0; // 예약된 좌석 수 

		while (filled<preReservedCount) { // 이미 예약된 좌석수가 랜덤카운트보다 커지기 전까지 반복가능
			int r = random.nextInt(rows); 
			int c = random.nextInt(cols); //열과 행을 랜덤으로 받게함
			if(seats[r][c] == null || seats[r][c] ==0){ // 열과 행이 눌값이거나 0이면(좌석이 안만들어져있거나 빈좌석으로 되어있으면)
				seats[r][c] =1; // 1로 채워지고
				filled++; // filled는 증가하게 
			}
		}
		//나머지 좌석은(위에 랜덤으로 1을 채웠으니 나머지는 null값) 0으로 초기화 
		for(int i =0; i< rows; i++){
			for(int j =0; j<cols; j++){
				if(seats[i][j]==null)seats[i][j]=0;
			}
		}


		return seats;
	}
	//좌석 선택 메서드
	public void selectSeat(Scanner sc, Context<Reservation> reservContext) {

		clearSeats(); //좌석 정보 초기화 

		//* 로그인세션에서 아이디 값 받아오기 -> 그 아이디값을 리졀베이션에 넣어서 선택한 영화,극장,시간 꺼내옴*/
		String keyId = LoginSession.getCurrentId();
		Reservation reserv =reservContext.getData().get(keyId);
		

		// 사용자가 선택한 극장, 영화이름, 시간대 키값으로 사용
		String key = reserv.getMovie().getTitle() + "_" + reserv.getTheater().getTheaterName() + "_" + reserv.getTime();

        Integer[][] seats = context.getData().get(key);// 기존 배열 가져오기
        if (seats == null) { //null값이면 
            seats = createRandomSeat(5, 5); //랜덤seat받아와서
            context.getData().put(key, seats);//그 값 저장
        }
		printSeats(key, seats); // 랜덤메서드 반영한 좌석 출력

		int peonum=reserv.getPeople(); //선택한 인원수 받아오기
		String strRow; // 입력받은 행이름
		char charRow;// 선택한 행을 char로 바꿀때 사용
		int intRow; // 영어로 된 행을 숫자 값으로 저장할때 사용
		int Col; // 열번호
		String selectedSeat = ""; // 선택된 좌석번호

		// *****좌석 선택****** start
		for(int i =0; i<peonum; i++){ // 인원수만큼 돌리기
			if(peonum!=1){ //한명이 아니면 설명을 위해 몇번째 좌석 선택인지 출력
				System.out.println("\n"+"<"+(i+1)+"번째 좌석선택>");
			}
			while (true) {
			System.out.println("예약하실 좌석의 행을 입력하세요(A~E) : ");
			strRow = sc.nextLine();
			// 공백제거 후 첫번재 글자만 뽑아서 캐릭터로 전환 , A1이렇게 입력할수도 있으니까
			charRow = Character.toUpperCase(strRow.trim().charAt(0));
			if (charRow < 65 || charRow > 69) { //A~E까지
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
					seats[intRow][Col - 1] = 1;

					selectedSeat = "" + charRow + Col;
					this.addSeat(selectedSeat);// 선택한 좌석 리스트에 추가
					context.getData().put(key, seats); // 좌석 정보 Context에 저장
					reserv.setSeat(String.join(",",getSeat())); //reservation에 저장

					break;
				} else {
					System.out.println("이미 예약된 좌석입니다.\n");
				}
					
			}
		}
		System.out.println();
		printSeats(key, seats);
		System.out.println(String.join(",",getSeat()) + "이 선택되셨습니다.\n"); //선택 확인하기 위해 다시 출력
	}

	//남은좌석 출력하는 메서드 
	public static int getRemainingSeats(String key) {
		if(context == null) return 0; //context에 정보 저장안되어있으면 0리턴

			Integer[][] seats = context.getData().get(key);
			//배열이 없으면 초기화
			if(seats == null){
				Seat s = new Seat(context);
				seats= s.createRandomSeat(5, 5);//랜덤으로 몇개 예매
				context.getData().put(key, seats); //context에 저장
			}

			int remaining = 0; // 남은 좌석 수 
			for (int i = 0; i < seats.length; i++) {
				for (int j = 0; j < seats[i].length; j++) {
					if (seats[i][j] == null || seats[i][j] == 0) remaining++; //좌석이 null이거나 0이면 비어있는 좌석이니 remaining++
				}
			}
			return remaining;
		}

	// 예매 취소 메서드
	public void cancleSeat(String key){
			Integer[][] seats = context.getData().get(key);
			if(seats ==null) return;
		for(String seat :selectedSeats){
				int col = Character.toUpperCase(seat.charAt(0))-65; // A1이렇게 되어있는거 0번째 인덱스만 빼와서 인덱스 번호로 변환
				  int row = Integer.parseInt(seat.substring(1)) - 1; //숫자부분만 가져와서 숫자로 변환
				  seats[col][row] = 0; // 좌석을 빈칸으로 
			}
			context.getData().put(key, seats); // 저장
			clearSeats(); // 선택한 좌석들 리스트에서 초기화 
		}
}
