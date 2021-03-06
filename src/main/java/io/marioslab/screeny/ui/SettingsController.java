
package io.marioslab.screeny.ui;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.jnativehook.keyboard.NativeKeyEvent;

import com.jfoenix.controls.JFXTextField;

import io.marioslab.screeny.Config;
import io.marioslab.screeny.Screeny;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;

public class SettingsController implements Initializable {
	@FXML JFXTextField desktopHotkey;
	@FXML JFXTextField appHotkey;
	@FXML JFXTextField regionHotkey;
	@FXML JFXTextField editNextHotkey;
	@FXML JFXTextField settingsHotkey;

	@Override
	public void initialize (URL location, ResourceBundle resources) {
		load();
	}

	private void load () {
		Config config = Screeny.getConfig();

		desktopHotkey.setText(keysToString(config.screenshotHotkey));
		appHotkey.setText(keysToString(config.appScreenshotHotkey));
		regionHotkey.setText(keysToString(config.regionScreenshotHotkey));
		editNextHotkey.setText(keysToString(config.editNextHotkey));
		settingsHotkey.setText(keysToString(config.settingsHotkey));

		desktopHotkey.addEventFilter(KeyEvent.ANY, (event) -> {
			event.consume();
			if (event.getEventType() == KeyEvent.KEY_PRESSED) {
				if (event.getCode().isModifierKey()) return;

				int[] keys = Screeny.getKeyDetector().getPressedKeys();
				config.screenshotHotkey = keys;
				Screeny.saveAndReloadConfig();
				desktopHotkey.setText(keysToString(keys));
			}
		});

		appHotkey.addEventFilter(KeyEvent.ANY, (event) -> {
			event.consume();
			if (event.getEventType() == KeyEvent.KEY_PRESSED) {
				if (event.getCode().isModifierKey()) return;

				int[] keys = Screeny.getKeyDetector().getPressedKeys();
				config.appScreenshotHotkey = keys;
				Screeny.saveAndReloadConfig();
				appHotkey.setText(keysToString(keys));
			}
		});

		regionHotkey.addEventFilter(KeyEvent.ANY, (event) -> {
			event.consume();
			if (event.getEventType() == KeyEvent.KEY_PRESSED) {
				if (event.getCode().isModifierKey()) return;

				int[] keys = Screeny.getKeyDetector().getPressedKeys();
				config.regionScreenshotHotkey = keys;
				Screeny.saveAndReloadConfig();
				regionHotkey.setText(keysToString(keys));
			}
		});

		editNextHotkey.addEventFilter(KeyEvent.ANY, (event) -> {
			event.consume();
			if (event.getEventType() == KeyEvent.KEY_PRESSED) {
				if (event.getCode().isModifierKey()) return;

				int[] keys = Screeny.getKeyDetector().getPressedKeys();
				config.editNextHotkey = keys;
				Screeny.saveAndReloadConfig();
				regionHotkey.setText(keysToString(keys));
			}
		});

		settingsHotkey.addEventFilter(KeyEvent.ANY, (event) -> {
			event.consume();
			if (event.getEventType() == KeyEvent.KEY_PRESSED) {
				if (event.getCode().isModifierKey()) return;

				int[] keys = Screeny.getKeyDetector().getPressedKeys();
				config.editNextHotkey = keys;
				Screeny.saveAndReloadConfig();
				regionHotkey.setText(keysToString(keys));
			}
		});
	}

	private String keysToString (int[] keys) {
		Arrays.sort(keys);
		StringBuilder builder = new StringBuilder();
		for (int i = keys.length - 1; i >= 0; i--) {
			builder.append(NativeKeyEvent.getKeyText(keys[i]));
			if (i != 0) builder.append(" ");
		}
		return builder.toString();
	}
}
