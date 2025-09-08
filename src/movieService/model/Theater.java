package movieService.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import movieService.controller.Context;
import movieService.controller.LoginSession;
import movieService.controller.Reservation;

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
	public String selectTheater(Scanner sc, Context<Reservation> reservContext, Connection conn) {
		/* sql 수정부분 start */
		List<String> theaterOption = new ArrayList<>();
		try {
			// sql실행
			String sql = "select distinct t_name from Theater";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			// t_name 값들 theaterOption에 저장
			while (rs.next()) {
				theaterOption.add(rs.getString("t_name"));
			}
			rs.close();
			stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("극장선택에 오류가 발생하였습니다");
		}
		/* sql 수정부분 end */

		System.out.println("<극장을 선택하세요>");
		// 극장 목록 출력 후 선택
		for (int i = 0; i < theaterOption.size(); i++) {
			System.out.println((i + 1) + ". " + theaterOption.get(i));
		}

		System.out.println("---------------------");
		System.out.println("0번을 누르면 예매 취소됩니다.");
		System.out.println(); // 마지막에 줄바꿈

		System.out.println("선택>");
		int inputTheater = Integer.parseInt(sc.nextLine());

		if (inputTheater == 0) {
			return null;
		} else {
			// 입력받은 값 저장
			String selectTheater = theaterOption.get(inputTheater - 1);
			Theater sTheater = new Theater(selectTheater);

			// 로그인 세션에 임시저장된 id값 가져옴
			String keyId = LoginSession.getCurrentId();

//			// 해당 id가 key로 저장되어 있는 reservContext 객체 가져와서 극장명이 담긴 극장 객체 저장
//			reservContext.getData().get(keyId).setTheater(sTheater);
//
//			// 방금 선택한 Theater 객체 출력
//			Theater retrievedTheater = reservContext.getData().get(keyId).getTheater();
//			// System.out.println("저장된 극장: " + retrievedTheater.toString());
//
//			return retrievedTheater.toString();

			if (!reservContext.getData().containsKey(keyId)) {
				reservContext.getData().put(keyId, new Reservation(keyId, null));
			}
			reservContext.getData().get(keyId).setTheater(sTheater);

			return selectTheater;

		}

	}

	public static boolean selectTheaterTime(Scanner sc, Context<Reservation> reservContext, String movieName,
			Connection conn) {
		// 극장선택 후 시간선택으로 넘어가는 로직

		// title에 맞는 극장 get.TheaterName을 받아옴
		/* sql 수정부분 start */
		List<String> theaterOption = new ArrayList<>();

		try {
			String sql = """
					select distinct t.t_name
					from movieschedule ms
					join theater t on ms.theater_id = t.theater_id
					join movie m on ms.movie_id= m.movie_id
					where m.title = ?;
						""";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, movieName);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				theaterOption.add(rs.getString("t_name"));
			}
			rs.close();
			stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("극장 선택에 오류가 발생했습니다.");
		}
		if (theaterOption.isEmpty()) {
			System.out.println("선택한 영화가 상영되는 극장이 없습니다.");
		}

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
		if (inputTheater == 0) {
			return false;
		} else {
			selectTheater = theaterOption.get(inputTheater - 1);

			// 로그인 세션에 임시저장된 id값 가져옴
			String keyId = LoginSession.getCurrentId();

			// 입력받은 값 저장
			Theater sTheater = new Theater(selectTheater);

			if (!reservContext.getData().containsKey(keyId)) {
				reservContext.getData().put(keyId, new Reservation(keyId, null));
			}

			reservContext.getData().get(keyId).setTheater(sTheater);

			// 저장된 값 출력
			// 방금 선택한 Theater 객체 출력
			// Theater retrievedTheater = reservContext.getData().get(keyId).getTheater();
			// System.out.println("저장된 극장: " + retrievedTheater.toString());

			return true;
		}

	}

	@Override
	public String toString() {
		return this.theaterName;
	}

}
