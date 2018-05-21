
package io.marioslab.screeny.ui;

import java.io.IOException;

import io.marioslab.screeny.Main;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class UI extends Application {
	public static UI instance;
	public static Stage stage;

	@Override
	public void start (Stage stage) throws Exception {
		UI.instance = this;
		UI.stage = stage;

		Platform.setImplicitExit(false);
		stage.setAlwaysOnTop(true);
		stage.setTitle("Screeny");
	}

	public static void show () {
		Platform.runLater( () -> {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(UI.class.getResource("/fxml/main.fxml"));
			Pane root;
			try {
				root = loader.<Pane> load();
				Scene scene = new Scene(root);
				stage.setScene(scene);
				stage.show();
			} catch (IOException e) {
				Main.logError("Couldn't load UI.", e);
			}
		});
	}
}
