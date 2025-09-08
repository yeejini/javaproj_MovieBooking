package movieService.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import movieService.controller.Context;
import movieService.controller.LoginSession;
import movieService.controller.Reservation;
import movieService.data.MovieSchedule;

public class Movie {
	private String movieId;
	private String title;
	private String date;

	ArrayList<MovieSchedule> ms = new ArrayList<>();
	// 로그인 세션에 임시저장된 id값 가져옴
	String keyId = LoginSession.getCurrentId();

	// 생성자
	public Movie() {

	}

	public Movie(String title, String date) {
		this.title = title;
		this.date = date;
	}

	public Movie(String movieId, String title, String date) {
		this.movieId = movieId;
		this.title = title;
		this.date = date;
	}

	public String getMovieId() {
		return movieId;
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

	public String selectMovie(Scanner sc, Context<Reservation> reservContext, String theaterId, Connection conn) {
		boolean found = false;
		int idx = 1;
		ArrayList<String> movieList = new ArrayList<>();
		HashSet<String> addedMovies = new HashSet<>(); // 중복 체크용

		boolean theaterExists = false;

		// DB에서 theaterId 존재 여부 확인
		try {
			String sqlCheck = "SELECT COUNT(*) AS cnt FROM Theater WHERE theater_id = ?";
			try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
				pstmtCheck.setString(1, theaterId);
				ResultSet rsCheck = pstmtCheck.executeQuery();
				if (rsCheck.next() && rsCheck.getInt("cnt") > 0) {
					theaterExists = true;
				}
			}
		} catch (SQLException e) {
			System.err.println("극장 확인 중 오류 발생: " + e.getMessage());
			return null;
		}

		System.out.println("<영화를 선택하세요>");
		// DB에서 theaterId 기준으로 영화 목록 조회
		try {
			if (theaterExists) {
				// theaterId 존재 → MovieSchedule 기준으로 영화 목록 조회
				String sql = "SELECT DISTINCT m.movie_id, m.title " + "FROM MovieSchedule ms "
						+ "JOIN Movie m ON ms.movie_id = m.movie_id " + "WHERE ms.theater_id = ?";

				PreparedStatement pstmt = conn.prepareStatement(sql);

				try (pstmt) {
					pstmt.setString(1, theaterId);
					ResultSet rs = pstmt.executeQuery();

					while (rs.next()) {
						String title = rs.getString("title");
						if (!addedMovies.contains(title)) {
							System.out.println(idx + ". " + title);
							movieList.add(title);
							addedMovies.add(title);
							idx++;
						}
					}
				}
			} else {
				// theaterId가 DB에 없으면 → 모든 영화 목록 조회
				String sql = "SELECT title FROM Movie";
				try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
					ResultSet rs = pstmt.executeQuery();
					while (rs.next()) {
						String title = rs.getString("title");
						if (!addedMovies.contains(title)) {
							System.out.println(idx + ". " + title);
							movieList.add(title);
							addedMovies.add(title);
							idx++;
						}
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
			return null;
		}

		if (movieList.isEmpty()) {
			System.out.println("조회 가능한 영화가 없습니다.");
			return null;
		}

		System.out.println("---------------------");
		System.out.println("0번을 누르면 취소됩니다.");
		System.out.println(); // 마지막에 줄바꿈

		// 영화 선택

		System.out.println("선택>");
		int choice = Integer.parseInt(sc.nextLine());

		if (choice == 0) {
			return null;
		}

		String selectedMovie = movieList.get(choice - 1); // 선택된 영화 제목

		String scheduleId = ""; // 나중에 Reservation에 저장할 schedule_id
		String movieId = "";
		try {
			String sql = "SELECT schedule_id, m.movie_id " + "FROM MovieSchedule ms "
					+ "JOIN Movie m ON ms.movie_id = m.movie_id " + "WHERE m.title = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, selectedMovie);
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {
					scheduleId = rs.getString("schedule_id"); // 일치하는 스케줄 id 가져오기
					movieId = rs.getString("movie_id"); // movie_id도 가져옴
				} else {
					System.out.println("해당 영화는 선택한 극장에서 상영되지 않습니다.");
					return null;
				}
			}
		} catch (SQLException e) {
			System.err.println("DB 조회 중 오류 발생: " + e.getMessage());
			return null;
		}

		// 선택한 영화 정보로 Movie 객체 생성
		Movie sMovie = new Movie(movieId, selectedMovie, "");
		// Reservation 객체에 저장
		String keyId = LoginSession.getCurrentId();
		Reservation reservation = reservContext.getData().get(keyId);
		if (reservation != null) {
			reservation.setMovie(sMovie);
			// schedule_id도 Reservation 객체에 저장할 필드가 있으면 같이 저장
			reservation.setScheduleId(scheduleId); // 필요 시 Reservation에 scheduleId 필드 추가
		}

		System.out.println(selectedMovie + " 영화가 선택되었습니다.");

		return movieId;

	}

	// 영화 선택 후에만 날짜 선택 받을 수 있다.
	public static boolean selectDate(Scanner sc, Context<Reservation> reservContext, Connection conn) {
		String keyId = LoginSession.getCurrentId();
		Reservation reservation = reservContext.getData().get(keyId);
		Movie movie = reservation.getMovie();
		Theater theater = reservation.getTheater();

		if (movie == null || theater == null) {
			System.out.println("영화 또는 극장이 선택되지 않았습니다.");
			return false;
		}

		String movieId = movie.getMovieId();
		String selectMovie = movie.getTitle();
		String selectTheaterId = theater.getTheaterId();

		int idx = 1;
		ArrayList<String> dateList = new ArrayList<>();
		HashSet<String> addedDate = new HashSet<>(); // 중복 체크용

		System.out.println("<날짜를 선택하세요>");

		try {
			String sql = "SELECT DISTINCT date FROM MovieSchedule ms " + "JOIN Movie m ON ms.movie_id = m.movie_id "
					+ "WHERE m.movie_id = ? AND ms.theater_id = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, movieId);
				pstmt.setString(2, selectTheaterId);
				ResultSet rs = pstmt.executeQuery();

				idx = 1;
				while (rs.next()) {
					String date = rs.getString("date");
					if (!addedDate.contains(date)) {
						System.out.println(idx + ". " + date);
						dateList.add(date);
						addedDate.add(date);
						idx++;
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("날짜 조회 중 오류 발생: " + e.getMessage());
			return false;
		}

		if (dateList.isEmpty()) {
			System.out.println("해당 영화는 선택한 극장에서 상영되지 않습니다.");
			return false;
		}

		System.out.println("---------------------");
		System.out.println("0번을 누르면 취소됩니다.");
		System.out.println("선택>");
		int choice = Integer.parseInt(sc.nextLine());

		if (choice == 0) {
			return false;
		}

		String selectedDate = dateList.get(choice - 1);
		// 선택한 날짜를 Movie 객체에 업데이트
		reservation.setMovie(new Movie(movieId, selectMovie, selectedDate));

		System.out.println("선택된 날짜: " + selectedDate);
		return true;

	}

	@Override
	public String toString() {
		return this.title;
	}

}
