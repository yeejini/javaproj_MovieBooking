package movieService.model;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import movieService.controller.Context;
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
	public String selectTheater(Scanner sc, Context<Theater> context) {
		// 극장선택 후 영화로 넘어가는 로직

		// 무비스케줄 스트림 선언
		Stream<MovieSchedule> ms = MovieSchedule.movieS.stream();
		List<String> theaterOption = ms.map(MovieSchedule::getTheaterName).collect(Collectors.toList());

		// 극장 목록 출력 후 선택
		for (int i = 0; i < theaterOption.size(); i++) {
			System.out.println((i + 1) + ". " + theaterOption.get(i));
		}
		System.out.println("\n극장을 선택하세요 : ");
		int inputTheater = Integer.parseInt(sc.nextLine());

		// 입력받은 값 저장
		String selectTheater = theaterOption.get(inputTheater - 1);
//		Theater theater = new Theater(selectedTheater);
//		System.out.println(theater.theaterName);
		Theater sTheater = new Theater(selectTheater);
		context.getData().put("selectTheater", sTheater);

		// 저장된 값 출력
		Theater retrievedTheater = context.getData().get("selectTheater");
		System.out.println("저장된 극장: " + retrievedTheater.toString());

		return retrievedTheater.toString();

	}

	public static void selectTheaterTime(Scanner sc, Context<Theater> context, String movieName) {
		// 극장선택 후 시간선택으로 넘어가는 로직

		// movie.getTitle의 값 가져오기
//		Movie movie = new Movie();
//		Movie title = context.getData().get();
//		

		// title에 맞는 극장 get.TheaterName을 받아옴
		Stream<MovieSchedule> ms = MovieSchedule.movieS.stream();
		List<String> theaterOption = ms.filter(n -> n.getTitle().equals(movieName)).map(MovieSchedule::getTheaterName)
				.collect(Collectors.toList());

		// 목록 출력 후 선택
		for (int i = 0; i < theaterOption.size(); i++) {
			System.out.println((i + 1) + "." + theaterOption.get(i));
		}
		System.out.println("\n극장을 선택하세요 : ");
		int inputTheater = Integer.parseInt(sc.nextLine());
		selectTheater = theaterOption.get(inputTheater - 1);

		// 입력받은 값 저장
//		Theater theater = new Theater(selectedTheater);
//		System.out.println(theater.theaterName);
		Theater sTheater = new Theater(selectTheater);
		context.getData().put("selectTheater", sTheater);

		// 저장된 값 출력
		Theater retrievedTheater = context.getData().get("selectTheater");
		System.out.println("저장된 극장: " + retrievedTheater.toString());

	}

	@Override
	public String toString() {
		return this.theaterName;
	}

}
