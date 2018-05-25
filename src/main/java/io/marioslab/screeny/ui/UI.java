
package io.marioslab.screeny.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.marioslab.screeny.utils.Log;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class UI extends Application {
	public static UI instance;
	public static Stage mainStage;
	public static MainController mainController;
	private static final List<Stage> notifications = new ArrayList<Stage>();

	@Override
	public void start (Stage stage) throws Exception {
		UI.instance = this;
		UI.mainStage = stage;

		Platform.setImplicitExit(false);
		// stage.initStyle(StageStyle.UTILITY);
		stage.setAlwaysOnTop(true);
		stage.setTitle("Screeny");

		showMain();
	}

	public static void showMain () {
		Platform.runLater( () -> {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(UI.class.getResource("/fxml/main.fxml"));
			loader.setController(new MainController());
			Pane root;
			try {
				root = loader.<Pane> load();
				Scene scene = new Scene(root);
				mainStage.setScene(scene);
				mainStage.show();
			} catch (IOException e) {
				Log.error("Couldn't load UI.", e);
			}
		});
	}

	public static void showNotification (String title, String text) {
		Platform.runLater( () -> {
			FXMLLoader loader = new FXMLLoader();
			Stage stage = new Stage();
			loader.setLocation(UI.class.getResource("/fxml/notification.fxml"));
			loader.setController(new NotificationController(stage, title, text));
			try {
				Pane root = loader.<Pane> load();
				Scene scene = new Scene(root);
				stage.setScene(scene);
				stage.setAlwaysOnTop(true);
				stage.initStyle(StageStyle.UNDECORATED);
				stage.setOnHiding( (event) -> {
					notifications.remove(stage);
					Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
					double y = bounds.getMinY() + 10;
					for (int i = 0; i < notifications.size(); i++) {
						notifications.get(i).setY(y);
						y += notifications.get(i).getHeight() + 10;
					}
				});

				Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
				double offsetY = bounds.getMinY() + 10;
				if (notifications.size() > 0) {
					Stage last = notifications.get(notifications.size() - 1);
					offsetY = last.getY() + last.getHeight() + 10;
				}
				notifications.add(stage);
				stage.setX(-200000);
				stage.setY(-200);
				stage.show();
				double y = offsetY;
				Platform.runLater( () -> {
					stage.setX(bounds.getMaxX() - stage.getWidth() - 10);
					stage.setY(y);
				});
			} catch (IOException e) {
				Log.error("Couldn't create notification.", e);
			}
		});
	}

	public static void showEdit (BufferedImage image) {
		// TODO implement me
	}
}
