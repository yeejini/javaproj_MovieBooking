package movieService;

import java.util.Scanner;

import movieService.controller.Context;
import movieService.model.User;

public class MovieBooking {

	public static void main(String[] args) {
//      String loginMsg = """
//            영화관에 오신 것을 환영합니다 !
//            <먼저 회원가입과 로그인을 완료해주세요>
//            -------------------------------
//              1. 회원가입 | 2. 로그인 | 3. 종료
//            -------------------------------
//            선택>
//            """;
//      String menuMsg = """
//            --------------------------------------
//              1. 티켓조회 | 2.영화별예매 | 3.극장별예매 
//            --------------------------------------
//            선택>
//            """;
		Scanner sc = new Scanner(System.in);

		Context<User> account = new Context<>();

		String loginMenu = "";
//      while(true) { 
//         System.out.println(loginMsg);
//         loginMenu = sc.nextLine();
//         if(loginMenu.equals("3")) {
//            System.out.println("안녕히 가세요.");
//            break;
//         }
//         switch (loginMenu) {
//         case 
//         }
//      }

		User.signUp(sc, account);
		User.login(sc, account);

	}

}
