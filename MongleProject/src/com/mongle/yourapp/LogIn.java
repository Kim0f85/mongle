package com.mongle.yourapp;

import java.util.Scanner;

import com.mongle.database.DataBase;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PipedWriter;
import java.lang.reflect.Member;
import java.security.Identity;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongle.database.DataBase;
import com.mongle.resource.ResourcePath;
import com.mongle.resource.UserData;
import com.mongle.service.mypage.AttendanceCheck;
import com.mongle.service.mypage.Point;
import com.mongle.service.mypage.SafeSend;
import com.mongle.view.MongleVisual;

/**
 * 로그인 클래스
 */
public class LogIn {
	/**
	 * 로그인 토큰
	 */
	public static String primaryKey;

	/**
	 * 로그인 메서드
	 */
	public static void logIn() {
		try {
			MongleVisual.menuMove("로그인 화면");
			userLogin();
		} catch (Exception e) {
			System.out.println("LogIn.logIn");
			e.printStackTrace();
		}
	}

	private static void userLogin() {
		String checklevel = "";
		int count = 0;
		try {

			JSONParser parser = new JSONParser();
			UserData user = new UserData();
			Scanner scan = new Scanner(System.in);
			JSONArray list = (JSONArray) parser.parse(new FileReader(ResourcePath.MEMBER));
			MongleVisual.menuHeader("로그인");
			String checkID = "";
			String checkPW = "";
			String checkSalt = "";
			do {
				System.out.printf("\n%22s아이디: ", " ");
				user.setId(scan.nextLine());

				System.out.printf("\n%22s비밀번호: ", " ");
				user.setPw(scan.nextLine());

				for (Object obj : list) {
					if (((JSONObject) obj).get("id").equals(user.getId())) {
						checklevel = (String) ((JSONObject) obj).get("level");
						checkID = (String) ((JSONObject) obj).get("id");
						checkPW = (String) ((JSONObject) obj).get("pw");
						checkSalt = (String) ((JSONObject) obj).get("salt");
					}
				}
				if (checkID.equals(user.getId()) && (checklevel.equals("1") || checklevel.equals("2"))
						&& checkPW.equals(Encrypt.LogInPw(user.getPw(), checkSalt))) {
					// 로그인 상태
					System.out.printf("\n%22s로그인 성공\r\n", " ");
					primaryKey = user.getId();
					DataBase.loadPrivateUser(LogIn.primaryKey);
					AttendanceCheck.autoAttendance();
					MongleVisual.stopper();
					SafeSend.getSafeMoney();
					SafeSend.getSafeMoney();
					MainMenu.mainMenu(checklevel);
					return;
				} else {
					System.out.printf("\n%22s로그인 실패\r\n", " ");
					count++;
				}

				if (count == 3) {
					System.out.printf("\n%22s다시 시도하시겠습니까? Y/N\r\n", " ");
					MongleVisual.choiceGuidePrint();
					String choice = scan.nextLine();
					if (choice.equals("Y") || choice.equals("y")) {
						count = 0;
					} else if (choice.equals("N") || choice.equals("n")) {
						return;
					} else {
						System.out.printf("\n%22s잘못된 입력\r\n", " ");
						return;
					}
				}
			} while (!checkID.equals(user.getId()) || count == 3 || !checkPW.equals(user.getPw()));

		} catch (Exception e) {
			System.out.println("LogIn.userLogin");
			e.printStackTrace();
		}

		return;
	}
}
