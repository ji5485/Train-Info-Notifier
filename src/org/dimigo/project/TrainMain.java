/**
 * 
 */
package org.dimigo.project;

import java.util.HashMap;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * <pre>
 *  org.dimigo.project
 *     |_ TrainMain
 *
 * 1. 개요 : 
 * 2. 작성일 : 2017. 6. 23.
 * </pre>
 *
 * @author : ji548
 * @version : 1.0
 */
public class TrainMain extends Application {
	
	public static String msg; // 에러 코드

	public static void main(String[] args) throws Exception {
		setting();
		launch();
	}

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("TrainInfo.fxml"));
		
		stage.setScene(new Scene(root));
		stage.setTitle("열차 정보 알리미");
		
		stage.centerOnScreen();
		stage.show();
	}
	
	// 에러 메세지 호출
	public void errMsg() throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("Error.fxml"));
		Stage stage = new Stage();
			    
	    stage.setScene(new Scene(root));
	    stage.setTitle("ERROR");
	    stage.show();	    
	}
	
	// 도시 코드, 열차 종류 코드 조회
	private static void setting() {
		TrainAPI.cityMap = (HashMap<String, String>) TrainAPI.ParseJson("cityMap", TrainAPI.CITY_MAP_TAGS);
		TrainAPI.trainMap = (HashMap<String, String>) TrainAPI.ParseJson("trainMap", TrainAPI.TRAIN_MAP_TAGS);
	}

}
