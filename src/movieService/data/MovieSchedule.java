package movieService.data;

import java.util.List;

public class MovieSchedule {
	private String title;
	private String screenName;
	private String theaterName;
	private String date;
	private String time;

	public MovieSchedule(String title, String theaterName, String screenName, String date, String time) {
		this.title = title;
		this.screenName = screenName;
		this.theaterName = theaterName;
		this.date = date;
		this.time = time;
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

	public static final List<MovieSchedule> movieS = List.of(new MovieSchedule("좀비딸", "롯데시네마", "1", "8/26", "14:00"),
			new MovieSchedule("귀멸의칼날", "CGV", "3", "8/28", "11:00"),
			new MovieSchedule("F1", "메가박스", "5", "9/26", "16:00"));

}
