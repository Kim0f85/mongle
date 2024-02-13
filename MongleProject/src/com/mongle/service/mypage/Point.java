package com.mongle.service.mypage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.JsonParser;
import com.mongle.database.DataBase;
import com.mongle.resource.AttendList;
import com.mongle.view.MongleVisual;

/**
 * 포인트 클래스
 */
public class Point {

	/**
	 * 포인트 조회 및 메뉴 이동
	 * 
	 * @return 메뉴 이동을 위한 변수
	 */
	public static int pointService() {
		Scanner scan = new Scanner(System.in);

		while (true) {
			int r = -1;

			MongleVisual.menuHeader("포 인 트");

			System.out.println();
			System.out.printf("%22s보유 포인트: %,d", " ",
					Integer.parseInt((String) DataBase.getPrivateUser().get(0).get("point")));
			System.out.println();
			System.out.println();
			System.out.printf("%22s1. 포인트 내역\n", " ");
			System.out.printf("%22s9. 홈으로\n", " ");
			System.out.printf("%22s0. 이전으로\n", " ");
			MongleVisual.choiceGuidePrint();

			String sel = scan.nextLine();
			if (sel.equals("1")) {
				MongleVisual.menuMove("포인트 내역 화면");
				r = pointList();
				if (r == 9) {
					return 9;
				}
			} else if (sel.equals("9")) {
				MongleVisual.menuMove("메인 화면");
				return 9;
			} else if (sel.equals("0")) {
				MongleVisual.menuMove("이전 화면");
				return 0;
			}
		}

	}

	/**
	 * 포인트 내역 조회 및 메뉴 이동
	 * 
	 * @return 메뉴 이동을 위한 변수
	 */
	private static int pointList() {
		Scanner scan = new Scanner(System.in);
		ArrayList<String> attenddate = AttendList.list.get(0).getAttenddate();

		for (int i = 0; i < attenddate.size(); i++) {
			if (i % 5 == 0) {
				MongleVisual.menuHeader("포인트 내역");
			}
			System.out.printf("%22s|%s  | 출석 |   +10point\t|\n", " ", attenddate.get(i));
			if (i != 0 && (i + 1) % 5 == 0 || i == (attenddate.size() - 1)) {
				MongleVisual.menuHeader("");
				System.out.println();
				while (true) {
					System.out.printf("%22s1. 다음페이지\n", " ");
					System.out.printf("%22s9. 홈으로\n", " ");
					System.out.printf("%22s0. 이전으로\n", " ");
					MongleVisual.choiceGuidePrint();
					String sel = scan.nextLine();
					if (sel.equals("1")) {
						if (i == (attenddate.size() - 1)) {
							System.out.printf("%22s다음 페이지가 없습니다.\n\n", " ");
						}
						break;
					} else if (sel.equals("9")) {
						MongleVisual.menuMove("메인 화면");
						return 9;
					} else if (sel.equals("0")) {
						MongleVisual.menuMove("이전 화면");
						return 0;
					} else {
						MongleVisual.wrongInput();
					}
				}
			}
		}
		MongleVisual.stopper();

		return 0;
	}

}
