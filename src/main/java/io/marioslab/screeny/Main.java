
package io.marioslab.screeny;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import io.marioslab.screeny.storage.LocalStorage;
import io.marioslab.screeny.storage.SshStorage;
import io.marioslab.screeny.ui.UI;
import io.marioslab.screeny.utils.PlatformAWT;
import io.marioslab.screeny.utils.PlatformMac;
import io.marioslab.screeny.utils.Utils;
import javafx.application.Application;

public class Main {
	public static final File LOG_FILE = new File(System.getProperty("user.home") + "/.screeny.log");

	private static Config config;
	private static Platform platform;
	private static HotkeyDetector detector;
	private static Tray tray;
	private static List<Storage> storages = new ArrayList<Storage>();

	public static void main (String[] args) throws Throwable {
		System.setProperty("apple.awt.UIElement", "true");

		Utils.writeToFile(LOG_FILE, "", false);
		platform = newPlatform();
		detector = new HotkeyDetector();
		tray = new Tray();
		storages.add(new LocalStorage());
		storages.add(new SshStorage());
		loadConfig();
		Application.launch(UI.class);

		while (true)
			Thread.sleep(1000);
	}

	public static List<Storage> getStorages () {
		return storages;
	}

	public static Tray getTray () {
		return tray;
	}

	public static Config getConfig () {
		synchronized (Main.class) {
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
		return new PlatformAWT();
	}

	private static void loadConfig () {
		Thread configReloadThread = new Thread(new Runnable() {
			@Override
			public void run () {
				long lastModificationTime = 0;

				while (true) {
					if (!Config.CONFIG_FILE.exists() || Config.CONFIG_FILE.lastModified() != lastModificationTime) {
						lastModificationTime = Config.CONFIG_FILE.lastModified();

						synchronized (Main.class) {
							Main.config = Config.load();
						}

						detector.clearHotkeyActions();
						detector.addHotkeyAction(Main.config.screenshotHotkey, () -> Screenshot.takeScreenshot());
						detector.addHotkeyAction(Main.config.appScreenshotHotkey, () -> Screenshot.takeAppScreenshot());
						detector.addHotkeyAction(Main.config.regionScreenshotHotkey, () -> Screenshot.takeRegionScreenshot());

						log("Reloaded configuration.");
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

	public static synchronized void log (String message) {
		try {
			Utils.writeToFile(LOG_FILE, message + "\n", true);
			System.out.println(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static synchronized void logError (String message, Throwable exception) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
			writer.write(message + "\n");
			if (exception != null) {
				writer.write(exception.getMessage() + "\n");
				exception.printStackTrace(writer);
			}

			System.out.println(message);
			if (exception != null) {
				System.out.println(exception.getMessage());
				exception.printStackTrace();
			}

			UI.showNotification("Error", message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static synchronized void failError (String message) {
		failError(message, null);
	}

	public static synchronized void failError (String message, Throwable exception) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
			writer.write(message + "\n");
			if (exception != null) {
				writer.write(exception.getMessage() + "\n");
				exception.printStackTrace(writer);
			}

			System.out.println(message);
			if (exception != null) {
				System.out.println(exception.getMessage());
				exception.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(-1);
	}
}
