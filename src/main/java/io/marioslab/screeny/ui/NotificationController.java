
package io.marioslab.screeny.ui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NotificationController implements Initializable {
	private static final int SHOW_TIME = 5000;

	@FXML ImageView image;
	@FXML Label title;
	@FXML Label text;
	@FXML JFXButton close;

	Stage stage;
	String titleData;
	String textData;

	public NotificationController (Stage stage, String titleData, String textData) {
		this.stage = stage;
		this.titleData = titleData;
		this.textData = textData;

		new Timeline(new KeyFrame(Duration.millis(SHOW_TIME), (event) -> stage.close())).play();
	}

	@Override
	public void initialize (URL location, ResourceBundle resources) {
		title.setText(titleData);
		text.setText(textData);
		close.setOnAction( (event) -> stage.close());
	}
}
