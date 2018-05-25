
package io.marioslab.screeny;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.marioslab.screeny.storage.LocalStorage;
import io.marioslab.screeny.storage.ScpStorage;
import io.marioslab.screeny.ui.UI;
import io.marioslab.screeny.utils.Log;
import io.marioslab.screeny.utils.PlatformJavaFX;
import io.marioslab.screeny.utils.PlatformMac;
import javafx.application.Application;

public class Screeny {
	public static final File APP_DIR = new File(System.getProperty("user.home") + "/.screeny");

	private static Config config;
	private static Platform platform;
	private static HotkeyDetector detector;
	private static Tray tray;
	private static List<Storage> storages = new ArrayList<Storage>();

	public static void main (String[] args) throws Throwable {
		System.setProperty("apple.awt.UIElement", "true");

		if (!APP_DIR.exists()) {
			if (!APP_DIR.mkdirs()) {
				Log.error("Couldn't create app dir.", null);
				exit(-1);
			}
		}

		platform = newPlatform();
		detector = new HotkeyDetector();
		tray = new Tray();
		storages.add(new LocalStorage());
		storages.add(new ScpStorage());
		loadConfig();
		Application.launch(UI.class);
	}

	public static List<Storage> getStorages () {
		return storages;
	}

	public static Tray getTray () {
		return tray;
	}

	public static Config getConfig () {
		synchronized (Screeny.class) {
			return config;
		}
	}

	public static HotkeyDetector getKeyDetector () {
		return detector;
	}

	public static Platform getPlatform () {
		return platform;
	}

	private static Platform newPlatform () {
		if (System.getProperty("os.name").toLowerCase().contains("mac")) return new PlatformMac();
		return new PlatformJavaFX();
	}

	private static void loadConfig () {
		Thread configReloadThread = new Thread(new Runnable() {
			@Override
			public void run () {
				long lastModificationTime = 0;

				while (true) {
					if (!Config.CONFIG_FILE.exists() || Config.CONFIG_FILE.lastModified() != lastModificationTime) {
						lastModificationTime = Config.CONFIG_FILE.lastModified();

						synchronized (Screeny.class) {
							Screeny.config = Config.load();
						}

						detector.clearHotkeyActions();
						detector.addHotkeyAction(Screeny.config.screenshotHotkey, () -> Screenshot.takeScreenshot());
						detector.addHotkeyAction(Screeny.config.appScreenshotHotkey, () -> Screenshot.takeAppScreenshot());
						detector.addHotkeyAction(Screeny.config.regionScreenshotHotkey, () -> Screenshot.takeRegionScreenshot());

						Log.info("Reloaded configuration.");
					}

					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
				}
			}
		});
		configReloadThread.setName("Config reload thread");
		configReloadThread.setDaemon(true);
		configReloadThread.start();
	}

	public static void exit (int i) {
		System.exit(-1);
	}
}
