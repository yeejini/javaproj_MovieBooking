package movieService.model;

import java.util.ArrayList;
import java.util.Scanner;

import movieService.controller.Context;
import movieService.controller.Reservation;
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

	public String selectMovie(Scanner sc, Context<Reservation> reservContext, String theaterName) {
		boolean found = false;
		int idx = 1;
		ArrayList<String> movieList = new ArrayList<>();

//		Theater theater = new Theater(theaterName);
		System.out.println("선택된 극장" + theaterName);

		for (MovieSchedule schedule : MovieSchedule.movieS) {
			if (theaterName == null || theaterName.isEmpty()) {
				// theaterName이 없으면 모든 영화 제목 출력
				System.out.println(idx + ". " + schedule.getTitle());
				movieList.add(schedule.getTitle());
				idx++;
			} else if (schedule.getTheaterName().equals(theaterName)) {
//				System.out.println(schedule.getTheaterName().equals(theaterName));

				// theaterName이 존재하면 해당 극장에서 상영하는 영화만 출력
				System.out.println(idx + ". " + schedule.getTitle());
				movieList.add(schedule.getTitle());
				idx++;
			}
		}
		System.out.println(); // 마지막에 줄바꿈

		// 영화 선택
		System.out.println("영화를 선택하세요.>");
		int choice = Integer.parseInt(sc.nextLine());
		String selectedMovie = movieList.get(choice - 1); // 선택된 영화 제목

		found = false;

		// MovieSchedule에서 선택된 영화와 일치하는지 확인
		for (MovieSchedule schedule : MovieSchedule.movieS) {
			if (schedule.getTitle().equals(selectedMovie)) {
				// 선택한 영화 정보로 Movie 객체 생성
				Movie sMovie = new Movie(selectedMovie, "");
				// context에 저장
				// reservContext.getData().put("NoDateSelectMovie", sMovie);
				found = true;
				System.out.println("선택된 영화: " + selectedMovie);
				return selectedMovie; // 실제 영화 제목 반환
			}
		}

		if (!found) {
			System.out.println("해당 영화는 존재하지 않습니다.");
			System.out.println("다시 영화를 선택하세요> ");
		}

		return selectedMovie; // 선택한 영화 제목 반환

	}

	// 영화 선택 후에만 날짜 선택 받을 수 있다.
	public static void selectDate(Scanner sc, Context<Theater> theaterContext, Context<Movie> movieContext) {

		// Context에서 "selectMovie" Key로 저장된 Movie 객체 가져오기
		Movie movie = movieContext.getData().get("NoDateSelectMovie");

		String selectMovie = movie.getTitle();
		System.out.println("선택된 영화: " + selectMovie);

		// Context에서 "selectTheater" Key로 저장된 Theater 객체 가져오기
		Theater theater = theaterContext.getData().get("selectTheater");

		String selectTheater = theater.getTheaterName();
		System.out.println("선택된 극장: " + selectTheater);

		int idx = 1;
		ArrayList<String> movieList = new ArrayList<>();

		// MovieSchedule에서 선택된 영화와 극장 일치하는 날짜 리스트만 출력
		for (MovieSchedule schedule : MovieSchedule.movieS) {
			if (schedule.getTitle().equals(selectMovie) && schedule.getTheaterName().equals(selectTheater)) {
				System.out.println(idx + ". " + schedule.getDate());
				movieList.add(schedule.getDate());
				idx++;
			}
		}

		System.out.println("날짜를 선택하세요>");
		int choice = Integer.parseInt(sc.nextLine());
		String selectDate = movieList.get(choice - 1); // 선택된 날짜

		// 선택한 영화 정보로 Movie 객체 생성
		Movie sMovie = new Movie(selectMovie, selectDate);
		// context에 저장
		movieContext.getData().put("PullMovie", sMovie);
		System.out.println("선택된 날짜: " + selectDate);

		if (selectDate == null || selectDate == "") {
			System.out.println("해당 날짜에 상영하는 영화는 존재하지 않습니다.");
		}
	}

	@Override
	public String toString() {
		return this.title;
	}

}
