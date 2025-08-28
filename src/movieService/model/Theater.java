package movieService.model;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import movieService.controller.Context;
import movieService.controller.LoginSession;
import movieService.controller.Reservation;
import movieService.data.MovieSchedule;

public class Theater {
//	public static void main(String[] args) {
//		Scanner sc = new Scanner(System.in);
//		Context<Object> context = new Context();
//		Theater.selectTheater(sc, context);
//		Theater.selectTheaterTime(sc, context);
//
//	}
	static String selectTheater;

	String theaterName;

	void setTheaterName(String theaterName) {
		this.theaterName = theaterName;
	}

	// 생성자
	public Theater(String theaterName) {
		this.theaterName = theaterName;
	}

	public String getTheaterName() {
		return theaterName;
	}

	// 극장 선택 메서드
public String selectTheater(Scanner sc, Context<Reservation> reservContext) {
    // 무비스케줄 스트림 선언 + 중복 제거
    List<String> theaterOption = MovieSchedule.movieS.stream()
                                        .map(MovieSchedule::getTheaterName)
                                        .distinct() // 중복 제거
                                        .collect(Collectors.toList());

	System.out.println("<극장을 선택하세요>");
    // 극장 목록 출력 후 선택
    for (int i = 0; i < theaterOption.size(); i++) {
        System.out.println((i + 1) + ". " + theaterOption.get(i));
    }

	System.out.println("선택>");
    int inputTheater = Integer.parseInt(sc.nextLine());

    // 입력받은 값 저장
    String selectTheater = theaterOption.get(inputTheater - 1);
    Theater sTheater = new Theater(selectTheater);

    // 로그인 세션에 임시저장된 id값 가져옴
    String keyId = LoginSession.getCurrentId();

    // 해당 id가 key로 저장되어 있는 reservContext 객체 가져와서 극장명이 담긴 극장 객체 저장
    reservContext.getData().get(keyId).setTheater(sTheater);

    // 방금 선택한 Theater 객체 출력
    Theater retrievedTheater = reservContext.getData().get(keyId).getTheater();
    // System.out.println("저장된 극장: " + retrievedTheater.toString());

    return retrievedTheater.toString();
}


	public static boolean selectTheaterTime(Scanner sc, Context<Reservation> reservContext, String movieName) {
		// 극장선택 후 시간선택으로 넘어가는 로직


		// title에 맞는 극장 get.TheaterName을 받아옴
		Stream<MovieSchedule> ms = MovieSchedule.movieS.stream();
		List<String> theaterOption = ms.filter(n -> n.getTitle().equals(movieName)).map(MovieSchedule::getTheaterName)
		.distinct() // 중복 제거
				.collect(Collectors.toList());

		System.out.println("<극장을 선택하세요>");
		// 목록 출력 후 선택
		for (int i = 0; i < theaterOption.size(); i++) {
			System.out.println((i + 1) + "." + theaterOption.get(i));
		}

		System.out.println("---------------------");
		System.out.println("0번을 누르면 예매 취소됩니다.");
		System.out.println(); // 마지막에 줄바꿈


		System.out.println("선택>");
		int inputTheater = Integer.parseInt(sc.nextLine());
		if(inputTheater == 0){
			return false;
		}else{
			selectTheater = theaterOption.get(inputTheater - 1);

			//로그인 세션에 임시저장된 id값 가져옴
			String keyId = LoginSession.getCurrentId();


			// 입력받은 값 저장
			Theater sTheater = new Theater(selectTheater);
			reservContext.getData().get(keyId).setTheater(sTheater);

			// 저장된 값 출력
			// 방금 선택한 Theater 객체 출력
			Theater retrievedTheater = reservContext.getData().get(keyId).getTheater();
			// System.out.println("저장된 극장: " + retrievedTheater.toString());

			return true;
		}
		
	}

	@Override
	public String toString() {
		return this.theaterName;
	}

}
