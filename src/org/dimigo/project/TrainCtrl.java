/**
 * 
 */
package org.dimigo.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * <pre>
 *  org.dimigo.project
 *     |_ PublicTransitCtrl
 *
 * 1. 개요 : 
 * 2. 작성일 : 2017. 6. 23.
 * </pre>
 *
 * @author : ji548
 * @version : 1.0
 */
public class TrainCtrl implements Initializable {
	@FXML ComboBox<String> CBregion; // 도시 선택
	@FXML ListView<String> LVplatform; // 해당 도시 내에 존재하는 역 출력
	@FXML TextField Departure; // 출발역
	@FXML TextField Destination; // 도착역
	@FXML DatePicker DPdepartDate; // 출발날짜
	String DepartureID, DestinationID; // 출발역, 도착역의 고유 ID (API사용)
	private static int page = 1; // 열차 정보 수 현재 페이지
	private static int totalInfo; // 열차 정보 수 (다음 페이지, 이전 페이지 기능에 활용)
	
	// 열차 정보 출력 표
	@FXML TableView<TrainInformation> TVTrainInfo;
	@FXML TableColumn<TrainInformation, String> trainNameColumn;
	@FXML TableColumn<TrainInformation, String> departColumn;
	@FXML TableColumn<TrainInformation, String> arrivalColumn;
	@FXML TableColumn<TrainInformation, String> chargeColumn;
	@FXML ObservableList<TrainInformation> trainInfo = FXCollections.observableArrayList();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		CBregion.getItems().addAll("서울특별시", "세종특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시", "대전광역시", "울산광역시",
								 "경기도", "강원도", "충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도");
		trainNameColumn.setCellValueFactory(cellData -> cellData.getValue().getTrainName());
		departColumn.setCellValueFactory(cellData -> cellData.getValue().getDepart());
		arrivalColumn.setCellValueFactory(cellData -> cellData.getValue().getArrival());
		chargeColumn.setCellValueFactory(cellData -> cellData.getValue().getCharge());
		TVTrainInfo.setItems(trainInfo);
		
		// 읽기용 변환
		Departure.setEditable(false);
		Destination.setEditable(false);
		DPdepartDate.setEditable(false);
	}
	
	// 해당 도시 내 역 조회
	public void getPlatform() {
		LVplatform.getItems().clear();
		String cityCode = TrainAPI.cityMap.get(CBregion.getValue());
		try {
			TrainAPI.platformSearch(cityCode);
			TrainAPI.platformMap.clear();
			TrainAPI.ParseJson("platform", TrainAPI.PLATFORM_MAP_TAGS);
			LVplatform.setItems(FXCollections.observableArrayList(new ArrayList<String>(TrainAPI.platformMap.keySet())));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 출발지 설정
	public void setDeparture() {
		DepartureID = null;
		Departure.setText(LVplatform.getSelectionModel().getSelectedItem());
		DepartureID = TrainAPI.platformMap.get(Departure.getText());
	}
	
	// 도착지 설정
	public void setDestination() {
		DestinationID = null;
		Destination.setText(LVplatform.getSelectionModel().getSelectedItem());
		DestinationID = TrainAPI.platformMap.get(Destination.getText());
	}
	
	// 열차 정보 조회
	public void getTrainInfo() throws Exception {
		trainInfo.clear();
		totalPage();
		try {
			String date = DPdepartDate.getValue().toString().replaceAll("-", "");
			TrainAPI.trainSearch(String.valueOf(page), DepartureID, DestinationID, date);
			List<String[]> trainInfo = TrainAPI.parseInfo();
			for (int i=0;i<trainInfo.size();i++) {
				addRow((trainInfo.get(i))[0], (trainInfo.get(i))[1], (trainInfo.get(i))[2], (trainInfo.get(i))[3]);
			}
		} catch (NullPointerException e) {
			TrainMain.msg = "lack";
			new TrainMain().errMsg();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// TableView에 조회된 열차 정보 추가
	public void addRow(String trainName, String depart, String arrival, String charge) {
		TVTrainInfo.getItems().add(new TrainInformation(new SimpleStringProperty(trainName), new SimpleStringProperty(depart),
														new SimpleStringProperty(arrival), new SimpleStringProperty(charge)));
	}
	
	public void nextPage() throws Exception {
		if (page < Math.ceil(totalInfo / 15.0)) {
			page++;
			TVTrainInfo.getItems().clear();
			getTrainInfo();
		}
		else return;
	}
	
	public void prevPage() throws Exception {
		if (page > 1) {
			page--;
			TVTrainInfo.getItems().clear();
			getTrainInfo();
		}
		else return;
	}
	
	// API호출을 통해 불러온 정보의 총합 (페이지 수를 계산하기 위함)
	@SuppressWarnings("unchecked")
	public static void totalPage() {
		String json = "", str;
		try (BufferedReader reader = new BufferedReader(new FileReader("files/trainInfo.json"))) {
			while ((str = reader.readLine()) != null) json += str;
			Map<String, Object> map = new ObjectMapper().readValue(json, Map.class);
			Map<String, Object> response = (Map<String, Object>) map.get("response");
			Map<String, Object> body = (Map<String, Object>) response.get("body");
			totalInfo = (int) body.get("totalCount");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
