package movieService.model;

import java.util.ArrayList;
import java.util.Scanner;

import movieService.controller.Context;
import movieService.data.MovieSchedule;

public class Movie {
	private String title;
	private String date;

	ArrayList<MovieSchedule> ms = new ArrayList<>();

	// 생성자
	public Movie() {

	}

	public Movie(String title, String date) {
		this.title = title;
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

//	public static void selectMovieByTitle(Scanner sc, Context<Movie> context, String theaterName) {
//		boolean found = false;
//
//		if (selectedTheater != null) {
//			for (MovieSchedule schedule : MovieSchedule.movieS) {
//				if (schedule.getTheaterName().equals(selectedTheater.getName())) {
//					System.out.println(schedule.getTitle() + "\t");
//				}
//			}
//		} else {
//			System.out.println("선택된 극장이 없습니다.");
//		}
//		System.out.println();
//		// 영화 선택
//		System.out.println("영화를 선택하세요.>");
//		String m = sc.nextLine();
//
//		for (MovieSchedule schedule : MovieSchedule.movieS) {
//			// 사용자에게 입력받은 영화와 영화리스트 값 중 하나가 일치하면,
//			if (schedule.getTitle().equals(m)) {
//				Movie sMovie = new Movie(m, "");
//				// context로 movie타입의 객체로 사용자에게 입력받은 영화제목과 날짜는 빈 문자열로 저장
//				context.getData().put(m, sMovie);
//				found = true;
//				return;
//			}
//		}
//		if (!found) {
//			System.out.println("해당 영화는 존재하지 않습니다.");
//			System.out.println("다시 영화를 선택하세요> ");
//		}
//
//	}

	public String selectMovie(Scanner sc, Context<Movie> context, String theaterName) {
		boolean found = false;

//		Theater theater = new Theater(theaterName);

		for (MovieSchedule schedule : MovieSchedule.movieS) {
			if (theaterName == null || theaterName.isEmpty()) {
				// theaterName이 없으면 모든 영화 제목 출력
				System.out.print(schedule.getTitle() + "\t");
			} else if (schedule.getTheaterName().equals(theaterName)) {
				// theaterName이 존재하면 해당 극장에서 상영하는 영화만 출력
				System.out.print(schedule.getTitle() + "\t");
			}
		}
		System.out.println(); // 마지막에 줄바꿈

		// 영화 선택
		System.out.println("영화를 선택하세요.>");
		String m = sc.nextLine();

		for (MovieSchedule schedule : MovieSchedule.movieS) {
			// 사용자에게 입력받은 영화와 영화리스트 값 중 하나가 일치하면,
			if (schedule.getTitle().equals(m)) {
				Movie sMovie = new Movie(m, "");
				// context로 movie타입의 객체로 사용자에게 입력받은 영화제목과 날짜는 빈 문자열로 저장
				context.getData().put(m, sMovie);
//				System.out.println(context.getData());
				found = true;
				return m;
			}
		}
		if (!found) {
			System.out.println("해당 영화는 존재하지 않습니다.");
			System.out.println("다시 영화를 선택하세요> ");
		}
		return m;

	}

	// 영화 선택 후에만 날짜 선택 받을 수 있다.
	public static void selectDate(Scanner sc, Context<User> context) {
		boolean found = false;

//		System.out.println("날짜를 선택하세요>");
//		String d = sc.nextLine();
//		
//		for (MovieSchedule schedule : MovieSchedule.movieS) {
//			// 사용자에게 입력받은 영화와 영화리스트 값 중 하나가 일치하면,
//			if (schedule.getTitle().equals(d)) {
//				Movie sMovie = new Movie(d, "");
//				//context로 movie타입의 객체로 사용자에게 입력받은 영화제목과 날짜는 빈 문자열로 저장 
//				context.getData().put(d, sMovie);
//				found = true;
//				return;
//			}
//		}
//		if (!found) {
//			System.out.println("해당 영화는 존재하지 않습니다.");
//			System.out.println("다시 영화를 선택하세요> ");
//		}
//		
//		

	}

}
