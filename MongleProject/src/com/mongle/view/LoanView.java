package com.mongle.view;

import java.util.Scanner;

import com.mongle.service.AssetService;

public class LoanView {
	public void loanMenu() {
		boolean loop = true;
		while(loop) {
			MongleVisual.menuHeader("대출 관리");
			System.out.println();
			System.out.printf("%22s 1.대출정보\n", " ");
			System.out.printf("%22s 2.대출연장\n", " ");
			System.out.printf("%22s 3.중도/전체 상환\n", " ");
			System.out.printf("%22s 0.이전으로\n", " ");
			System.out.printf("%22s번호를 입력하세요:", " ");

			Scanner sc = new Scanner(System.in);
			String sel = sc.nextLine();
			
			if(sel.equals("1")) {
				// 대출정보
			}else if(sel.equals("2")) {
				// 대출연장
			}else if(sel.equals("3")) {
				// 중도/전체 상환
			}else if(sel.equals("0")) {
				//
			}else {
				MongleVisual.wrongInput();
			}
		}
	}
	 
}