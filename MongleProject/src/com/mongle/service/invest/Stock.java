package com.mongle.service.invest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mongle.view.MongleVisual;

import netscape.javascript.JSObject;

public class Stock {
	
	static String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0b2tlbiIsImF1ZCI6IjBkNDBmNGEwLWY5MmMtNDMwYS1hNTU1LTI3MWIxMjQ0YWUxYiIsImlzcyI6InVub2d3IiwiZXhwIjoxNzA2NjU2OTgxLCJpYXQiOjE3MDY1NzA1ODEsImp0aSI6IlBTc2VSdm1xdEhnNWt4OWNKZnM3SkZ6cGJQQlFiWHhUNFduOCJ9.Edz3sCO3a5NlSUf6mZoTd50X8A0lG_iymZ5rYDXxd39_dWIb1HTeCM-cG0LkYL4jTh1ezNTWo19S0DT2ez28Ng";
	static String TOKENexpired = "2024-01-31 08:27:12";
	public static int buyPrice = 0;
	public static int buyAmount = 0;
	
	public static void stockService() {
		try {

			Scanner scan = new Scanner(System.in);
			Boolean loop = true;
			MongleVisual.menuHeader("주식");
			

			while (loop) {
				String stockURL =  "https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo?serviceKey=S3mJZVa%2B2sPWWlGQUV7WgSNe4Fd3yYWAts4pwm9aPIOJVQY4NZqBVvp4bQT%2Fm6iH023rAE5yPGI7gi%2FAZKlxng%3D%3D&numOfRows=1&resultType=json";

				System.out.printf("%30s1. 종목명으로 검색\n", " ");
				System.out.printf("%30s2. 코드명으로 검색\n", " ");
				System.out.printf("%30s0. 이전으로\n", " ");
				System.out.println();
				System.out.printf("%30s선택: ", " ");

				String sel = scan.nextLine();
				
				System.out.println();

				if (sel.equals("1")) {
					System.out.printf("%30s종목명: ", " ");
					sel = "&itmsNm=" + URLEncoder.encode(scan.nextLine(), "UTF-8");
				} else if (sel.equals("2")) {
					System.out.printf("%30s코드명: ", " ");
					sel = "&srtnCd=" + URLEncoder.encode(scan.nextLine(), "UTF-8");
				}  else if (sel.equals("0")) {
						System.out.printf("%30s종료 하시겠습니까? (y/n)", " ");
						sel = scan.nextLine();
						if (sel.equals("y")) {
						loop = false;
						break;
					} else if (sel.equals("n")) {
						continue;
					}
				}
				System.out.println();
				
				stockURL += sel;
				
				URL url = new URL(stockURL);
				// JSON 결과
				BufferedReader bf;
				bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
				String result = bf.readLine();
				
				JSONParser parser = new JSONParser();
				JSONObject jsonObject = (JSONObject) parser.parse(result);
				JSONObject response = (JSONObject) jsonObject.get("response");
				JSONObject body = (JSONObject) response.get("body");
				JSONObject items = (JSONObject) body.get("items");
				Long totalCount = (Long)body.get("totalCount");
				JSONArray item = (JSONArray) items.get("item");
				// System.out.println(jsonObject);
				
				if (totalCount == 0) {
					System.out.printf("%30s검색 결과가 없습니다.\n", " ");
					System.out.printf("%30s계속하시려면 엔터를 눌러주세요\n", " ");
					scan.nextLine();
					continue;
				}
				
				result = stockPrice((String)((JSONObject) item.get(0)).get("srtnCd"));
				response = (JSONObject) parser.parse(result);
				
				body = (JSONObject) response.get("output");
				int nowPrice = Integer.parseInt((String) body.get("stck_prpr"));
				double priceRate = Double.parseDouble((String) body.get("prdy_ctrt"));
				int startPrice = Integer.parseInt((String) body.get("stck_oprc"));
				int highPrice = Integer.parseInt((String) body.get("stck_hgpr"));
				int lowPrice = Integer.parseInt((String) body.get("stck_lwpr"));
				String name = (String) ((JSONObject) item.get(0)).get("itmsNm");
				
				String midHeader = "+---------------+---------------+---------------+---------------+---------------+";
				String header = "+-------------------------------------------------------------------------------+";
		        System.out.println(header);
		        System.out.printf("|%37s종목명\t\t\t\t\t|\n", " ");
		        System.out.printf("|%36s%s\t\t\t\t\t|\n", " ", name);
		        System.out.println(midHeader);
		        System.out.printf(
		        		"|%5s현재가\t|%4s전일비\t|%5s시가\t|%5s고가\t|%5s저가\t|\n"
		        		, "", " ", " ", " ", " ");
		        System.out.printf("|%5s%,d\t|%5s%,.2f\t|%5s%,d\t|%5s%,d\t|%5s%,d\t|\n"
		        					, " ", nowPrice
		        					, " ", priceRate
		        					, " ",  startPrice
		        					, " ", highPrice
		        					, " ", lowPrice);
		        System.out.println(midHeader);
				
				
				
				System.out.println();
				System.out.println();
				
				System.out.printf("%30s1. 구매\n", " ");
				System.out.printf("%30s2. 다시 검색하기\n", " ");
				System.out.printf("%30s9. 홈으로\n", " ");
				System.out.printf("%30s0. 이전으로\n", " ");
				System.out.println();
				System.out.printf("%30s선택: ", " ");
				String sel2 = scan.nextLine();
				if (sel2.equals("1")) {
					String amount = "";
					MongleVisual.menuHeader("현재가: " + String.format("%,d", nowPrice));
					while (true) {
						System.out.printf("%30s수량(숫자): ", " ");
						amount = scan.nextLine();
						String regex = "^[0-9]+$";
						Pattern p1 = Pattern.compile(regex);
						Matcher m1 = p1.matcher(amount);
						if (!m1.find()) {
							System.out.printf("%27s정확한 숫자를 입력해 주시기 바랍니다.\n", " ");
						} else {
							break;
						}
					}
					System.out.printf("%30s총 구매 대금: %,d원\n", " ", nowPrice * Integer.parseInt(amount));
					System.out.printf("%30s구매하시겠습니까? (y/n)\n", " ");
					System.out.printf("%30s선택: ", " ");
					sel = scan.nextLine();
					if (sel.equals("y")) {
						System.out.printf("%35s거래가 완료되었습니다.\n", " ");
						System.out.printf("%27s홈 화면으로 돌아가려면 엔터를 눌러주세요.\n", " ");
						buyPrice = nowPrice;
						buyAmount = Integer.parseInt(amount);
						scan.nextLine();
					} else if (sel.equals("n")) {
						System.out.printf("%35s거래가 취소되었습니다.\n", " ");
						System.out.printf("%27s홈 화면으로 돌아가려면 엔터를 눌러주세요.\n", " ");
						scan.nextLine();
					} else {
						System.out.printf("%35s입력이 올바르지 않습니다.\n", " ");
						System.out.printf("%27s홈 화면으로 돌아가려면 엔터를 눌러주세요.\n", " ");
						scan.nextLine();
					}
				} else if (sel2.equals("2")) {
					System.out.printf("%30s엔터를 눌러 계속하기\n", " ");
					scan.nextLine();
				} else if (sel2.equals("9")) {
					loop = false;
					break;
				} else if (sel2.equals("0")) {
					loop = false;
					break;
				}
				
				System.out.println();

			}

		} catch (Exception e) {
			System.out.println("Stock.main");
			e.printStackTrace();
		}

	}// stock
	
	public static String stockPrice(String cd) {

		// 국내 주식 시세 조회
        String url = "https://openapi.koreainvestment.com:9443/uapi/domestic-stock/v1/quotations/inquire-price";
        String tr_id = "FHKST01010100";
		String data = "?fid_cond_mrkt_div_code=J" + //FID 조건 시장 분류 코드
	                "&fid_input_iscd=" + cd; //FID 입력 종목코드
		
		getToken();
	    
        String result = "";
        try {
        	result = httpGetConnection(url,data,tr_id);
		} catch (Exception e) {
			System.out.println("Stock.stockPrice");
			e.printStackTrace();
		}
        return result;
    }
    public static String httpGetConnection(String UrlData, String ParamData,String TrId) throws IOException {
        String totalUrl = "";
        totalUrl = UrlData.trim().toString();

        URL url = null;
        HttpURLConnection conn = null;

        String responseData = "";
        BufferedReader br = null;

        StringBuffer sb = new StringBuffer();
        String returnData = "";

      try{
        url = new URL(totalUrl+ParamData);
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("authorization", "Bearer " + TOKEN);
        conn.setRequestProperty("appKey", "PSseRvmqtHg5kx9cJfs7JFzpbPBQbXxT4Wn8");
        conn.setRequestProperty("appSecret", "ozZWypajrAAc6Qg9XBV8Vi8qyof2EFf//16gsk3nc7XtMOjOrbtmb+v7qKBvYwJJJ/ni4TXLK2Dp0seaE4zIgak+EVrWl+02xHcAiOwRUD9q+bhOkGsBrb4ZmEKuTxrwqog8sBK19oo7ktQ9naCW0XtjNrB0g52ZhbAuIBfwFroN5szX2SY=");
        conn.setRequestProperty("tr_id", TrId);
        conn.setDoOutput(true);

        conn.connect();

        br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
      } catch (IOException e){
          br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
      } finally {
          try {
        	  String result = "";
              while ((responseData = br.readLine()) != null) {
                  result += responseData;
              }
              if (br != null){
                  br.close();
              }
              return result;
          } catch (IOException e){
              throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
          }
      }
    }
    
    public static void getToken() {
        try {
            String APP_KEY = "PSseRvmqtHg5kx9cJfs7JFzpbPBQbXxT4Wn8";
            String APP_SECRET = "ozZWypajrAAc6Qg9XBV8Vi8qyof2EFf//16gsk3nc7XtMOjOrbtmb+v7qKBvYwJJJ/ni4TXLK2Dp0seaE4zIgak+EVrWl+02xHcAiOwRUD9q+bhOkGsBrb4ZmEKuTxrwqog8sBK19oo7ktQ9naCW0XtjNrB0g52ZhbAuIBfwFroN5szX2SY=";
            String URL_BASE = "https://openapivts.koreainvestment.com:29443";
            String PATH = "oauth2/tokenP";
            String URL = URL_BASE + "/" + PATH;
            

			LocalDate date = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String today = date.format(formatter);
			String[] day = TOKENexpired.split(" ");
			if (Integer.parseInt(day[0].replace("-", "")) > Integer.parseInt(today.replace("-", ""))) {
				return;
			}

            // Request body
            String requestBody = String.format(
                "{\"grant_type\":\"client_credentials\",\"appkey\":\"%s\",\"appsecret\":\"%s\"}",
                APP_KEY, APP_SECRET
            );

            // Set up the HTTP connection
            URL url = new URL(URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // Write request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            	String response = br.readLine();
            	JSONParser parser = new JSONParser();
				JSONObject jsonObject = (JSONObject) parser.parse(response);
            	
                TOKEN = (String) jsonObject.get("access_token");
                TOKENexpired = (String) jsonObject.get("access_token_token_expired");
            }

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}