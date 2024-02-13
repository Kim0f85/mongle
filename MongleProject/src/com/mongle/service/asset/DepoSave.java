package com.mongle.service.asset;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mongle.database.DataBase;
import com.mongle.resource.BankAccount;
import com.mongle.service.invest.InfoProduct;
import com.mongle.view.MongleVisual;
import com.mongle.yourapp.Encrypt;

/**
 * 예적금 클래스
 */
public class DepoSave {
	private static String bankDepo;
	private static String titleDepo;

	/**
	 * 예적금 생성자
	 * 
	 * @param bankDepo  예적금 금융사
	 * @param titleDepo 예적금 상품 이름
	 */
	public DepoSave(String bankDepo, String titleDepo) {
		super();
		this.bankDepo = bankDepo;
		this.titleDepo = titleDepo;

	}

	/**
	 * 예적금 금융사 Getter
	 * 
	 * @return 예적금 금융사
	 */
	public String getBankDepo() {
		return bankDepo;
	}

	/**
	 * 예적금 금융사 Setter
	 * 
	 * @param bankDepo 예적금 금융사
	 */
	public void setBankDepo(String bankDepo) {
		this.bankDepo = bankDepo;
	}

	/**
	 * 예적금 상품 이름 Getter
	 * 
	 * @return 예적금 상품 이름
	 */
	public String getTitleDepo() {
		return titleDepo;
	}

	/**
	 * 예적금 상품 이름 Setter
	 * 
	 * @param titleDepo 예적금 상품 이름
	 */
	public void setTitleDepo(String titleDepo) {
		this.titleDepo = titleDepo;
	}

	/**
	 * 예적금 toString() String.format("DepoSave [bankDepo=%s, titleDepo=%s]",
	 * bankDepo, titleDepo);
	 */
	@Override
	public String toString() {
		return String.format("DepoSave [bankDepo=%s, titleDepo=%s]", bankDepo, titleDepo);
	}

	/**
	 * 예적금 상품 검색 및 가입
	 * 
	 * @return 메뉴 이동을 위한 변수
	 */
	public static int depoSaveService() {
		Scanner scan = new Scanner(System.in);
		List<InfoProduct> table = new ArrayList<>(); // 예적금 정보 담을 리스트
		boolean loop = true;

		while (loop) {
			MongleVisual.menuHeader("계좌 개설");
			table = searchDepoSave(table); // 예적금 검색

			System.out.println();
			System.out.printf("%22s예적금 가입(번호로 선택)\n", " ");
			System.out.printf("%22s8. 다시 검색하기\n", " ");
			System.out.printf("%22s9. 홈으로\n", " ");
			System.out.printf("%22s0. 이전으로\n", " ");

			while (loop) {
				MongleVisual.choiceGuidePrint();
				String sel = scan.nextLine();
				try {
					if (Integer.parseInt(sel) >= 1 && Integer.parseInt(sel) <= (table.size() > 7 ? 7 : table.size())) {
						DepoSave acc = new DepoSave(table.get(Integer.parseInt(sel) - 1).getBank(),
								table.get(Integer.parseInt(sel) - 1).getTitle());
						MongleVisual.menuMove("가입 화면");
						MongleVisual.menuHeader("예적금 가입");
						System.out.printf("%22s%s / %s\n", " ", acc.bankDepo, acc.titleDepo);
						signUp(acc.bankDepo, acc.titleDepo);
						DataBase.dataSave();
						loop = false;
					} else if (sel.equals("8")) {
						table.clear();
						break;
					} else if (sel.equals("9")) {
						MongleVisual.menuMove("홈 화면");
						return 9;
					} else if (sel.equals("0")) {
						MongleVisual.menuMove("이전 화면");
						return 0;
					} else {
						MongleVisual.wrongInput();
					}
				} catch (NumberFormatException e) {
					MongleVisual.wrongInput();
				}
			} // while
		} // while
		return 0;
	}

	/**
	 * 예적금 상품 검색
	 * 
	 * @param table 예적금 상품 정보를 담을 리스트
	 * @return 예적금 상품 정보를 담은 리스트
	 */
	public static List<InfoProduct> searchDepoSave(List<InfoProduct> table) { // 예적금 검색 한번에 모으기
		Scanner scan = new Scanner(System.in);
		String apiDepo = "https://finlife.fss.or.kr/finlifeapi/depositProductsSearch.json?auth=e06ef138c067a4ff1a42504d0fefda36&topFinGrpNo=020000&pageNo=1";
		String apiSave = "https://finlife.fss.or.kr/finlifeapi/savingProductsSearch.json?auth=efebe52a92c17a5bcee4c231f829a349&topFinGrpNo=020000&pageNo=1";

		System.out.printf("%22s검색(은행 이름) : ", " ");
		String name = scan.nextLine();

		String header = "+----+-----------------+----------------------------------+---------+-----------+----------+";
		System.out.printf("%s\n", header);
		System.out.printf("|번호|      금융사     |           상품명           \t  |   기간  | 기본금리 | 최고금리 |\n");
		System.out.printf("%s\n", header);
		table = searchAPI(table, name, apiDepo);
		table = searchAPI(table, name, apiSave);
		print(table);
		System.out.printf("%s\n", header);

		return table;
	}

	/**
	 * JSON API 분석 및 내용 추출
	 * 
	 * @param table 예적금 상품 정보를 담을 리스트
	 * @param name  검색할 금융사 이름
	 * @param path  분석할 API path
	 * @return 예적금 상품 정보를 담은 리스트
	 */
	private static List<InfoProduct> searchAPI(List<InfoProduct> table, String name, String path) { // api 검색
		try {
			URL url = new URL(path);

			// JSON 결과
			BufferedReader bf;
			bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
			String result = bf.readLine();
			bf.close();

			JSONParser parser = new JSONParser();
			JSONObject root = (JSONObject) parser.parse(result);
			JSONObject res = (JSONObject) root.get("result");

			JSONArray list = (JSONArray) res.get("baseList");
			JSONArray option = (JSONArray) res.get("optionList");

			for (Object product : list) {
				if (((String) ((JSONObject) product).get("kor_co_nm")).contains(name)) {
					String bank = (String) ((JSONObject) product).get("kor_co_nm");
					String title = (String) ((JSONObject) product).get("fin_prdt_nm");
					String code = (String) ((JSONObject) product).get("fin_prdt_cd");
					String period = "";
					double rate = 0;
					double maxRate = 0;

					for (Object opt : option) {
						if (((JSONObject) opt).get("fin_prdt_cd").equals(code)) {
							period = (String) ((JSONObject) opt).get("save_trm");
							rate = Double.parseDouble(((JSONObject) opt).get("intr_rate").toString());
							maxRate = Double.parseDouble(((JSONObject) opt).get("intr_rate2").toString());
							break;
						}
					}
					InfoProduct d = new InfoProduct(bank, title, period, rate, maxRate);
					table.add(d);
				}
			}
		} catch (Exception e) {
			System.out.println("emain");
			e.printStackTrace();
		}
		return table;
	}

	/**
	 * 표 형식으로 출력
	 * 
	 * @param data 출력할 리스트
	 */
	public static void print(List<InfoProduct> data) { // 표에 반복해서 출력하는 메서드
		for (int i = 0; i < (data.size() > 7 ? 7 : data.size()); i++) {
			System.out.printf("|%-3d|%-14s|%-20s\t  |%5s개월|%9s%%|%10s%%|\n", i + 1, data.get(i).getBank(),
					data.get(i).getTitle(), data.get(i).getPeriod(), data.get(i).getRate(), data.get(i).getMaxRate());
		}
	}

	/**
	 * 계좌 개설
	 * 
	 * @param bankDepo  예적금 금융사
	 * @param titleDepo 예적금 상품 이름
	 */
	public static void signUp(String bankDepo, String titleDepo) {
		Scanner sc = new Scanner(System.in);
		boolean tf = true;
		while (tf) {
			System.out.printf("%21s 선택한 상품이 맞으신가요?(y/n)", " ");
			String answer = sc.nextLine();

			if (answer.equals("y") || answer.equals("Y")) {
				Reconfirm(); /// 비밀번호 검사
				tf = false;
			} else if (answer.equals("n") || answer.equals("N")) {
				MongleVisual.menuMove("이전 화면");
				tf = false;
			} else {
				MongleVisual.wrongInput();
			}
		}
	}// DepositSignUp

	/**
	 * 계좌 생성을 위한 데이터 호출
	 * 
	 * @param bankDepo  예적금 금융사
	 * @param titleDepo 예적금 상품 이름
	 */
	public static void openDepo(String bankDepo, String titleDepo) {
		GiveAccount.load();
		String AccountNumber = "";

		for (BankAccount acc : GiveAccount.glist) {
			if (bankDepo.contains(acc.getBankDepo())) {
				AccountNumber = acc.getAccountNumber();
				GiveAccount.glist.remove(acc);
				break;
			}
		}
		BankAccount.list.add(new BankAccount(bankDepo, titleDepo, AccountNumber, 0)); // json에 추가
		GiveAccount.save();

	}// OpenDeposit

	/**
	 * 비밀번호 재검사
	 */
	public static void Reconfirm() {
		Scanner sc = new Scanner(System.in);

		HashMap<String, Object> userData = new HashMap<String, Object>();
		for (int i = 0; i < DataBase.getPrivateUser().size(); i++) {

			for (Object key : DataBase.getPrivateUser().get(i).keySet()) {
				userData.put((String) key, DataBase.getPrivateUser().get(i).get((String) key));
			}
		}

		String checkPW = "";
		int count = 0;
		for (int i = 6; i > count; i--) {
			System.out.printf("%22s비밀번호를 입력해 주세요:", " ");
			checkPW = sc.nextLine();
			if (userData.get("pw").equals(Encrypt.LogInPw(checkPW, (String) userData.get("salt")))) {
				// 가입성공
				System.out.printf("%22s가입이 완료 되었습니다.\n", " ");
				openDepo(bankDepo, titleDepo);
				MongleVisual.menuMove("이전 화면");
				return;
			} else {
				// 비밀번호 불일치
				System.out.printf("%22s불일치\n", " ");
				System.out.printf("%22s총 %d회 더 입력하실 수 있습니다.\n", " ", i - 1);
			}
		}
		System.out.printf("%22s비밀번호가 총 5회 틀려 이전 화면으로 돌아갑니다.\n", " ");
		MongleVisual.menuMove("이전 화면");

	}// Reconfirm

}// class
