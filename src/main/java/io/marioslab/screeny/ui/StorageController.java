
package io.marioslab.screeny.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import io.marioslab.screeny.Config;
import io.marioslab.screeny.Config.LocalStorageConfig;
import io.marioslab.screeny.Config.StorageConfig;
import io.marioslab.screeny.Screeny;
import io.marioslab.screeny.utils.Log;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class StorageController implements Initializable {

	@FXML VBox storageList;

	@Override
	public void initialize (URL location, ResourceBundle resources) {
		Config config = Screeny.getConfig();

		for (StorageConfig storageConfig : config.getStorageConfigs()) {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/fxml/storage-item.fxml"));
			loader.setController(new Initializable() {
				@FXML Label name;
				@FXML Label type;
				@FXML MaterialDesignIconView delete;

				@Override
				public void initialize (URL location, ResourceBundle resources) {
					name.setText(storageConfig.name);
					type.setText(storageConfig.getClass().getSimpleName().replaceAll("StorageConfig", ""));
					if (storageConfig instanceof LocalStorageConfig) {
						delete.setVisible(false);
					} else {
						delete.setOnMouseClicked(event -> {
							storageList.getChildren().remove(name.getParent().getParent());
						});
					}
				}
			});
			try {
				Node node = loader.<Node> load();
				storageList.getChildren().add(node);
			} catch (IOException e) {
				Log.error("Couldn't load storage item UI.", e);
			}
		}
	}
}
