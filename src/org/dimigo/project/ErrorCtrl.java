/**
 * 
 */
package org.dimigo.project;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * <pre>
 *  org.dimigo.project
 *     |_ ErrorCtrl
 *
 * 1. 개요 : 
 * 2. 작성일 : 2017. 6. 25.
 * </pre>
 *
 * @author : ji548
 * @version : 1.0
 */
public class ErrorCtrl implements Initializable {
	@FXML Label label;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (TrainMain.msg.equals("none")) label.setText("해당하는 열차 정보가 없습니다.");
		else if (TrainMain.msg.equals("lack")) label.setText("검색 정보를 입력해주세요.");
	}
	
	public void exit() {
		Stage stage = (Stage) label.getScene().getWindow();
		stage.close();
	}
}