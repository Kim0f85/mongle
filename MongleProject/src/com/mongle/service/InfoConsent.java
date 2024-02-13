package com.mongle.service;

import java.awt.Choice;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

import javax.swing.RepaintManager;

import com.mongle.resource.ResourcePath;
import com.mongle.view.MongleVisual;
import com.mongle.yourapp.StartPage;

/**
 * 개인정보 수집 동의 클래스
 */
public class InfoConsent {
	/**
	 * 개인정보 수집 동의서 선택 메뉴
	 * 
	 * @param input 선택 값
	 * @return 동의서 경로
	 */
	public static int useConsent(String input) {
		System.out.printf("\n%22s%s\r\n", " ", input);

		String path = "";
		if (input.equals("개인정보 수집이용 동의서")) {
			path = ResourcePath.IA;
		} else {
			path = ResourcePath.UA;
		}
		choicePrint();
		int choice = choiceReturn(path);

		if (choice == 2 || choice == 0) {
			StartPage.startPage();
		}
		return choice;
	}

	/**
	 * 동의 여부 확인
	 * 
	 * @param path 동의서 경로
	 * @return 동의 여부
	 */
	public static int choiceReturn(String path) {
		int result = 3;
		do {
			System.out.printf("\n%22s선택: ", " ");
			Scanner scan = new Scanner(System.in);
			String choice = scan.nextLine();

			if (choice.equals("y") || choice.equals("Y")) {
				result = 1;
			} else if (choice.equals("n") || choice.equals("N")) {
				System.out.printf("\n%22s동의 하지 않으시면 회원가입이 불가능합니다. \r\n", "");
				System.out.printf("\n%22s동의서에 동의하시나요?  y/n", " ");
				String sel = scan.nextLine();
				if (sel.equals("y") || sel.equals("Y")) {
					result = 1;
				} else if (sel.equals("n") || sel.equals("N")) {
					result = 0;
				} else {
					System.out.printf("%22s입력이 올바르지 않습니다.\n\n", " ");
					result = 3;
				}
			} else if (choice.equals("m") || choice.equals("M")) {
				try {
					printTxt(path);
					choicePrint();
				} catch (Exception e) {
					System.out.println("InfoConsent.choiceReturn");
				}
				result = 3;
			} else {
				System.out.printf("%22s입력이 올바르지 않습니다.\n\n", " ");
				result = 3;
			}
		} while (result == 3);

		return result;
	}

	/**
	 * 동의 문구 출력
	 */
	public static void choicePrint() {
		System.out.printf("\n%22s동의 하시면 y\r", " ");
		System.out.printf("\n%22s미동의 하시면 n\r", " ");
		System.out.printf("\n%22s세부사항 확인 m \r", " ");
	}

	/**
	 * 동의서 출력
	 * 
	 * @param path 동의서 경로
	 */
	public static void printTxt(String path) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			String line = in.readLine();

			while (line != null) {
				System.out.printf("\n%22s%s\r", " ", line);
				line = in.readLine();
			}
		} catch (Exception e) {
			System.out.println("InfoConsent.printTxt");
			e.printStackTrace();
		}
	}

}
