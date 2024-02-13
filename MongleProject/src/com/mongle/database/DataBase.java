package com.mongle.database;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongle.resource.AttendList;
import com.mongle.resource.BankAccount;
import com.mongle.resource.History;
import com.mongle.resource.Investment;
import com.mongle.resource.ResourcePath;
import com.mongle.yourapp.LogIn;

/**
 * 데이터베이스 클래스
 */
public class DataBase {

	static ArrayList<HashMap> user = new ArrayList<HashMap>();
	static ArrayList<HashMap> privateUser = new ArrayList<HashMap>();

	/**
	 * user getter
	 * 
	 * @return user
	 */
	public static ArrayList<HashMap> getUser() {
		return user;
	}

	/**
	 * user setter
	 * 
	 * @param user
	 */
	public static void setUser(HashMap<String, Object> newUser) {
		DataBase.user.add(newUser);
	}

	/**
	 * privateUser getter
	 * 
	 * @return privateUser
	 */
	public static ArrayList<HashMap> getPrivateUser() {
		return privateUser;
	}

	/**
	 * privateUser setter
	 * 
	 * @param privateUser
	 */
	public static void setPrivateUser(HashMap<String, Object> newUser) {
		DataBase.privateUser.add(newUser);
	}

	/**
	 * 로그인 된 사용자 정보 불러오기
	 * 
	 * @param primaryKey 로그인 유저 아이디
	 */
	public static void loadPrivateUser(String primaryKey) {
		JSONParser parser = new JSONParser();
		try {
			// JSON 파일을 읽어와 JsonArray로 파싱
			FileReader reader = new FileReader(ResourcePath.MEMBER);
			JSONArray jsonArray = (JSONArray) parser.parse(reader);

			privateUser.clear();
			BankAccount.list.clear();
			Investment.list.clear();
			for (Object obj : jsonArray) {
				JSONObject item = (JSONObject) obj;

				if (item.get("id").equals(primaryKey)) {
					HashMap<String, Object> userData = new HashMap<String, Object>();
					for (Object key : item.keySet()) {
						userData.put((String) key, item.get((String) key));

						if (key.equals("account")) {
							JSONArray temp = (JSONArray) item.get("account");
							for (Object ob : temp) {
								JSONObject it = (JSONObject) ob;

								if (it.containsKey("history")) {
									JSONArray arr = (JSONArray) it.get("history");
									ArrayList<History> history = new ArrayList<History>();
									for (Object content : arr) {
										JSONObject his = (JSONObject) content;
										int amount = (int) ((long) his.get("amount"));
										int balance = (int) ((long) his.get("balance"));
										History h = new History((String) his.get("date"), (String) his.get("memo"),
												amount, balance);
										history.add(h);
									}
									int num = (int) ((long) it.get("depositAmount"));
									BankAccount b = new BankAccount((String) it.get("bankDepo"),
											(String) it.get("titleDepo"), (String) it.get("accountNumber"), num,
											history);
									BankAccount.list.add(b);
								} else {
									int num = (int) ((long) it.get("depositAmount"));
									BankAccount b = new BankAccount((String) it.get("bankDepo"),
											(String) it.get("titleDepo"), (String) it.get("accountNumber"), num);
									BankAccount.list.add(b);
								}
							}
						}

						if (key.equals("invest")) {
							JSONArray temp = (JSONArray) item.get("invest");
							for (Object ob : temp) {
								JSONObject it = (JSONObject) ob;

								int amount = (int) ((long) it.get("amount"));
								int price = (int) ((long) it.get("price"));
								Investment i = new Investment((String) it.get("realTitle"), (String) it.get("bankDepo"),
										(String) it.get("titleDepo"), price, amount);

								Investment.list.add(i);
							}
						}
					}
					privateUser.add(userData);
				}
			}
			if (privateUser.isEmpty()) {
				System.out.println(primaryKey + " 값을 가진 데이터가 존재하지 않습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 특정 사용자의 특정(key) 데이터(value) 수정
	 * 
	 * @param arrayList 수정할 리스트
	 * @param key       수정할 분야
	 * @param value     수정할 값
	 */
	public static void changeData(ArrayList<HashMap> arrayList, String key, Object value) {
		// 기존 privateUser ArrayList에서 "id"가 "asd159"인 데이터의 "name" 수정
		for (HashMap<String, Object> user : arrayList) {
			if (user.get("id").equals(LogIn.primaryKey)) {
				user.put(key, value);
				break; // 수정한 후에는 루프 종료
			}
		}
	} // loadPrivateUser

	/**
	 * 특정 사용자의 모든 데이터 갱신
	 */
	public static void changeData() { // user데이터 중 로그인 중인 primaryuser의 데이터를 갱신
//		DataBase.changeData(DataBase.getUser(), "account", BankAccount.list); // sample
//		DataBase.changeData(DataBase.getUser(), "invest", Investment.list); // sample
		for (HashMap map : privateUser) {
			map.put("account", BankAccount.list);
			map.put("invest", Investment.list);
			map.put("attend", AttendList.list);
		}

		for (HashMap map : user) {
			if (map.get("id").equals(LogIn.primaryKey)) {
				for (Object obj : privateUser.get(0).keySet()) {
					map.put(obj, privateUser.get(0).get(obj));
				}
			}
		}

	}

	/**
	 * 사용자 데이터 저장
	 */
	public static void dataSave() {
		try {
			if (!DataBase.privateUser.isEmpty()) {
				changeData();
			}
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			File file = new File(ResourcePath.MEMBER);
			FileWriter writer = new FileWriter(file, false); // 덮쓰

			writer.write(gson.toJson(user));
			writer.flush(); // 버퍼 비우기
			writer.close();
		} catch (Exception e) {
			System.out.println("DataBase.dataSave Error");
			e.printStackTrace();
		}

	}

	/**
	 * 파일에서 사용자 데이터 읽기
	 */
	public static void dataLoad() {

		JSONParser parser = new JSONParser();
		try {
			FileReader reader = new FileReader(ResourcePath.MEMBER);
			JSONArray userList = (JSONArray) parser.parse(reader);

			user.clear(); // 기존 리스트를 비움
			Iterator<Object> iterator = userList.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonObject = (JSONObject) iterator.next();
				HashMap<String, Object> userData = new HashMap<>();
				// 가정: JSON 객체의 모든 키는 문자열이고, 값도 문자열임
				for (Object key : jsonObject.keySet()) {
					userData.put((String) key, jsonObject.get(key));
				}
				user.add(userData); // 읽은 데이터를 리스트에 추가
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
