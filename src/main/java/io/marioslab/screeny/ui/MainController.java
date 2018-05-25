
package io.marioslab.screeny.ui;

import java.net.URL;
import java.util.ResourceBundle;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import io.marioslab.screeny.utils.Log;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainController implements Initializable {
	@FXML HBox settings;
	@FXML HBox storage;

	@FXML MaterialDesignIconView navbarBack;
	@FXML Label navbarTitle;
	@FXML Pane content;

	@Override
	public void initialize (URL location, ResourceBundle resources) {
		settings.setOnMouseClicked(event -> {
			showSettings();
		});

		storage.setOnMouseClicked(event -> {
			showStorage();
		});

		showSettings();
	}

	private void showSettings () {
		navbarBack.setVisible(false);
		navbarTitle.setText("Settings");

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(UI.class.getResource("/fxml/settings.fxml"));
		loader.setController(new SettingsController());
		try {
			Pane root = loader.<Pane> load();
			VBox.setVgrow(root, Priority.ALWAYS);
			content.getChildren().clear();
			content.getChildren().add(root);

		} catch (Exception e) {
			Log.error("Couldn't load settings UI.", e);
		}
	}

	private void showStorage () {

	}
}
