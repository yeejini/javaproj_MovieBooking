package movieService.data;

import java.util.List;

public class MovieSchedule {
	private String title;
	private String screenName;
	private String theaterName;
	private String date;
	private String time;

	private Integer[][] seats; // 각 스케줄별 좌석 배열

	public Integer[][] getSeats() {
		return seats;
	}

	public void setSeats(Integer[][] seats) {
		this.seats = seats;
	}

	public MovieSchedule(String title, String theaterName, String screenName, String date, String time) {
		this.title = title;
		this.screenName = screenName;
		this.theaterName = theaterName;
		this.date = date;
		this.time = time;

		// 스케줄 생성 시 좌석 배열 초기화
        this.seats = new Integer[5][5]; 
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

	public static final List<MovieSchedule> movieS = List.of(new MovieSchedule("좀비딸", "롯데시네마", "1", "2025-08-26", "14:00"),
			new MovieSchedule("귀멸의칼날", "CGV", "3", "2025-08-29", "11:00"),
			new MovieSchedule("F1", "메가박스", "5", "2025-09-26", "16:00"));

}
