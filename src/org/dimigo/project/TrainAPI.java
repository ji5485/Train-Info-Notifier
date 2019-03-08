/**
 * 
 */
package org.dimigo.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.XML;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <pre>
 *  org.dimigo.project
 *     |_ TrainController
 *
 * 1. 개요 : 
 * 2. 작성일 : 2017. 6. 23.
 * </pre>
 *
 * @author : ji548
 * @version : 1.0
 */
public class TrainAPI {
	
	private static final String APP_KEY = "Li84MrgTHG3NQTtOqttclBn%2F5YDfHKpXh1zic2RtNicYSjX1eElZw44iMHywoCrXo6xUKoKcSSvZvkDuTudD3A%3D%3D";
	
	public static HashMap<String, String> cityMap = new HashMap<String, String>(); // 도시 코드
	public static HashMap<String, String> trainMap = new HashMap<String, String>(); // 열차 종류 코드
	public static HashMap<String, String> platformMap = new HashMap<String, String>(); // 도시 코드를 통해 해당 지역에 있는 역 조회
	
	// JSON 파싱용 key
	public static final String[] CITY_MAP_TAGS = {"citycode", "cityname"};
	public static final String[] TRAIN_MAP_TAGS = {"vehiclekndid", "vehiclekndnm"};
	public static final String[] PLATFORM_MAP_TAGS = {"nodeid", "nodename"};
	
	// 출발역, 도착역, 출발날짜를 통해 해당일에 운행하는 열차 정보 조회
	public static void trainSearch(String page, String dep, String arr, String date) throws Exception {
		StringBuilder urlBuilder = new StringBuilder("http://openapi.tago.go.kr/openapi/service/TrainInfoService/getStrtpntAlocFndTrainInfo"); /*URL*/
		urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + APP_KEY); /*Service Key*/
		urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("15", "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(page, "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("startPage","UTF-8") + "=" + URLEncoder.encode(page, "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("depPlaceId","UTF-8") + "=" + URLEncoder.encode(dep, "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("arrPlaceId","UTF-8") + "=" + URLEncoder.encode(arr, "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("depPlandTime","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8"));
		URL url = new URL(urlBuilder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");
		System.out.println("Response code: " + conn.getResponseCode());
		BufferedReader rd;
		if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
		rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
		rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
		sb.append(line);
		}
		rd.close();
		parseXmlToJson(sb.toString(), "trainInfo");
		conn.disconnect();
	}
	
	// 도시 코드를 통해 해당 지역에 존재하는 역 조회
	public static void platformSearch(String cityCode) throws Exception {
		StringBuilder urlBuilder = new StringBuilder("http://openapi.tago.go.kr/openapi/service/TrainInfoService/getCtyAcctoTrainSttnList"); /*URL*/
		urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + APP_KEY); /*Service Key*/
		urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("50", "UTF-8"));
		urlBuilder.append("&" + URLEncoder.encode("cityCode","UTF-8") + "=" + URLEncoder.encode(cityCode, "UTF-8")); /*시/도ID*/
		URL url = new URL(urlBuilder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");
		System.out.println("Response code: " + conn.getResponseCode());
		BufferedReader rd;
		if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		conn.disconnect();
		parseXmlToJson(sb.toString(), "platform");
	}
	
	// Json 파싱
	@SuppressWarnings("unchecked")
	public static Map<String, String> ParseJson(String title, String[] tags) {
		String json = "", str;
		Map<String, String> result = new HashMap<String, String>();
		try (BufferedReader reader = new BufferedReader(new FileReader("files/" + title + ".json"))) {
			while ((str = reader.readLine()) != null) json += str;
			Map<String, Object> map = new ObjectMapper().readValue(json, Map.class);
			Map<String, Object> response = (Map<String, Object>) map.get("response");
			Map<String, Object> body = (Map<String, Object>) response.get("body");
			Map<String, Object> items = (Map<String, Object>) body.get("items");
			List<Object> item = (List<Object>) items.get("item");
			for (int i=0;i<item.size();i++) {
				Map<String, String> temp = (Map<String, String>) item.get(i);
				result.put(temp.get(tags[1]), temp.get(tags[0]));
				if (title.equals("cityMap")) cityMap.put(temp.get(tags[1]), temp.get(tags[0]));
				else if (title.equals("trainMap")) trainMap.put(temp.get(tags[1]), temp.get(tags[0]));
				else if (title.equals("platform")) platformMap.put(temp.get(tags[1]), temp.get(tags[0]));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// trainInfo.json 파싱 (기존 파싱 경로와 달라 새 메소드를 만듦, 열차 정보 String 배열을 List로 묶어 리턴)
	@SuppressWarnings({ "unchecked"})
	public static List<String[]> parseInfo() throws Exception {
		String json = "", str;
		List<String[]> list = new ArrayList<String[]>();
		try (BufferedReader reader = new BufferedReader(new FileReader("files/trainInfo.json"))) {
			while ((str = reader.readLine()) != null) json += str;
			Map<String, Object> map = new ObjectMapper().readValue(json, Map.class);
			Map<String, Object> response = (Map<String, Object>) map.get("response");
			Map<String, Object> body = (Map<String, Object>) response.get("body");
			Map<String, Object> items = (Map<String, Object>) body.get("items");
			List<Object> item = (List<Object>) items.get("item");
			for (int i=0;i<item.size();i++) {
				Map<String, String> temp = (Map<String, String>) item.get(i);
				String trainName = temp.get("traingradename");
				String depart = String.valueOf(temp.get("depplandtime")).substring(8, 10) + ":" + String.valueOf(temp.get("depplandtime")).substring(10, 12) + " " + temp.get("depplacename");
				String arrival = String.valueOf(temp.get("arrplandtime")).substring(8, 10) + ":" + String.valueOf(temp.get("arrplandtime")).substring(10, 12) + " " + temp.get("arrplacename");
				String charge = String.valueOf(temp.get("adultcharge")) + "원";
				String[] strArr = new String[] {trainName, depart, arrival, charge};
				list.add(strArr);
			}
		} catch(ClassCastException e) {
			TrainMain.msg = "none";
			new TrainMain().errMsg();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	// XML -> JSON 을 files 폴더에 저장
	public static void writeFile(String str, String title) {
		try (Writer writer = new BufferedWriter(new FileWriter("files/" + title + ".json"))) {
			writer.write(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// XML -> JSON
	public static void parseXmlToJson(String xml, String title) {
		String json = XML.toJSONObject(xml).toString();
		writeFile(json, title);
	}
	
}
