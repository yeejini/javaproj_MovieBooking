package movieService.data;

import java.util.List;

public class MovieSchedule {
	private String title;
	private String screenName;
	private String theaterName;
	private String date;
	private String time;
	private String screen;

	public String getScreen() {
		return screen;
	}

	public void setScreen(String screen) {
		this.screen = screen;
	}

	private Integer[][] seats; // 각 스케줄별 좌석 배열

	public Integer[][] getSeats() {
		return seats;
	}

	public void setSeats(Integer[][] seats) {
		this.seats = seats;
	}

	public MovieSchedule(String title, String theaterName, String screenName, String date, String time, String screen) {
		this.title = title;
		this.screenName = screenName;
		this.theaterName = theaterName;
		this.date = date;
		this.time = time;
		this.screen = screen;

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getTheaterName() {
		return theaterName;
	}

	public void setTheaterName(String theaterName) {
		this.theaterName = theaterName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public static final List<MovieSchedule> movieS = List.of(
			// 1. 좀비딸
			new MovieSchedule("좀비딸", "롯데시네마", "1", "2025-08-26", "14:00", "1관"),
			new MovieSchedule("좀비딸", "롯데시네마", "1", "2025-08-26", "16:00", "1관"),
			new MovieSchedule("좀비딸", "CGV", "2", "2025-08-27", "13:00", "2관"),
			new MovieSchedule("좀비딸", "CGV", "2", "2025-08-27", "15:00", "2관"),
			new MovieSchedule("좀비딸", "메가박스", "3", "2025-08-28", "18:00", "3관"),
			new MovieSchedule("좀비딸", "메가박스", "3", "2025-08-28", "20:00", "3관"),

			// 2. 귀멸의칼날
			new MovieSchedule("귀멸의칼날", "롯데시네마", "1", "2025-08-26", "12:00", "1관"),
			new MovieSchedule("귀멸의칼날", "롯데시네마", "1", "2025-08-26", "14:00", "1관"),
			new MovieSchedule("귀멸의칼날", "CGV", "2", "2025-08-27", "16:00", "2관"),
			new MovieSchedule("귀멸의칼날", "CGV", "2", "2025-08-27", "18:00", "2관"),
			new MovieSchedule("귀멸의칼날", "메가박스", "3", "2025-08-28", "13:00", "3관"),
			new MovieSchedule("귀멸의칼날", "메가박스", "3", "2025-08-28", "15:00", "3관"),

			// 3. F1
			new MovieSchedule("F1", "롯데시네마", "1", "2025-08-28", "13:00", "1관"),
			new MovieSchedule("F1", "롯데시네마", "1", "2025-08-28", "16:00", "1관"),
			new MovieSchedule("F1", "CGV", "2", "2025-08-29", "14:00", "2관"),
			new MovieSchedule("F1", "CGV", "2", "2025-08-29", "17:00", "2관"),
			new MovieSchedule("F1", "메가박스", "3", "2025-08-30", "15:00", "3관"),
			new MovieSchedule("F1", "메가박스", "3", "2025-08-30", "18:00", "3관"),

			// 4. 오징어게임
			new MovieSchedule("오징어게임", "롯데시네마", "1", "2025-09-01", "14:00", "1관"),
			new MovieSchedule("오징어게임", "롯데시네마", "1", "2025-09-01", "16:00", "1관"),
			new MovieSchedule("오징어게임", "CGV", "2", "2025-09-02", "13:00", "2관"),
			new MovieSchedule("오징어게임", "CGV", "2", "2025-09-02", "15:00", "2관"),
			new MovieSchedule("오징어게임", "메가박스", "3", "2025-09-03", "18:00", "3관"),
			new MovieSchedule("오징어게임", "메가박스", "3", "2025-09-03", "20:00", "3관"),

			// 5. 인셉션
			new MovieSchedule("인셉션", "롯데시네마", "1", "2025-09-05", "12:00", "1관"),
			new MovieSchedule("인셉션", "롯데시네마", "1", "2025-09-05", "14:00", "1관"),
			new MovieSchedule("인셉션", "CGV", "2", "2025-09-06", "16:00", "2관"),
			new MovieSchedule("인셉션", "CGV", "2", "2025-09-06", "18:00", "2관"),
			new MovieSchedule("인셉션", "메가박스", "3", "2025-09-07", "15:00", "3관"),
			new MovieSchedule("인셉션", "메가박스", "3", "2025-09-07", "17:00", "3관"),

			// 6. 토르:러브앤썬더
			new MovieSchedule("토르:러브앤썬더", "롯데시네마", "1", "2025-09-08", "13:00", "1관"),
			new MovieSchedule("토르:러브앤썬더", "롯데시네마", "1", "2025-09-08", "16:00", "1관"),
			new MovieSchedule("토르:러브앤썬더", "CGV", "2", "2025-09-09", "14:00", "2관"),
			new MovieSchedule("토르:러브앤썬더", "CGV", "2", "2025-09-09", "17:00", "2관"),

			// 7. 스파이더맨
			new MovieSchedule("스파이더맨", "롯데시네마", "1", "2025-09-11", "14:00", "1관"),
			new MovieSchedule("스파이더맨", "롯데시네마", "1", "2025-09-11", "17:00", "1관"),
			new MovieSchedule("스파이더맨", "CGV", "2", "2025-09-12", "15:00", "2관"),
			new MovieSchedule("스파이더맨", "CGV", "2", "2025-09-12", "18:00", "2관"),
			new MovieSchedule("스파이더맨", "메가박스", "3", "2025-09-13", "16:00", "3관"),
			new MovieSchedule("스파이더맨", "메가박스", "3", "2025-09-13", "19:00", "3관"));

}
