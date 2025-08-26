package movieService.model;

import java.util.Scanner;

import movieService.controller.Context;

public class Movie {
	private String title;
	private String date;

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

	public static void selectMovie(Scanner sc, Context<User> context) {
		// 영화 리스트 출력

		// 영화 선택
		System.out.println("영화를 선택하세요.");
		String m = sc.nextLine();

		// 극장 리스트 출력
		// 극장 선택

	}

	public static void selectDate(Scanner sc, Context<User> context) {

	}

}
