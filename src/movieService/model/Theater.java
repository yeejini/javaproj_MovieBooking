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

	String theaterName;

	void setTheaterName(String theaterName) {
		this.theaterName = theaterName;
	}

	public Theater(String theaterName) {
		this.theaterName = theaterName;
	}

	public String getTheaterName() {
		return theaterName;
	}

	// 극장 선택 메서드
	public static void selectTheater(Scanner sc, Context<Object> context) {
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
		String selectedTheater = theaterOption.get(inputTheater - 1);
		Theater theater = new Theater(selectedTheater);
		System.out.println(theater.theaterName);

	}

	public static void selectTheaterTime(Scanner sc, Context<Object> context) {
		// 극장선택 후 시간선택으로 넘어가는 로직

		// movie.getTitle의 값 가져오기
		Movie movie = new Movie();
		String title = movie.getTitle();

		// title에 맞는 극장 get.TheaterName을 받아옴
		Stream<MovieSchedule> ms = MovieSchedule.movieS.stream();
		List<String> theaterOption = ms.filter(n -> n.getTitle().equals(title)).map(MovieSchedule::getTheaterName)
				.collect(Collectors.toList());

		// 목록 출력 후 선택
		for (int i = 0; i < theaterOption.size(); i++) {
			System.out.println((i + 1) + "." + theaterOption.get(i));
		}
		System.out.println("\n극장을 선택하세요 : ");
		int inputTheater = Integer.parseInt(sc.nextLine());
		String selectedTheater = theaterOption.get(inputTheater - 1);

		// 입력받은 값 저장
		Theater theater = new Theater(selectedTheater);
		System.out.println(theater.theaterName);

	}

}
