
package io.marioslab.screeny;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import io.marioslab.screeny.Config.Hotkey;
import io.marioslab.screeny.HotkeyDetector.HotkeyAction;
import io.marioslab.screeny.HotkeyDetector.HotkeyCallback;

public class Main {
	public static final File LOG_FILE = new File(System.getProperty("user.home") + "/.screeny.log");

	private static Config config;
	private static HotkeyDetector detector;
	private static Tray tray;

	public static void main (String[] args) throws InterruptedException, IOException {
		Utils.writeToFile(LOG_FILE, "", false);
		detector = new HotkeyDetector();
		tray = new Tray();
		loadConfig();

		while (true)
			Thread.sleep(1000);
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

	private static void loadConfig () {
		Thread configReloadThread = new Thread(new Runnable() {
			@Override
			public void run () {
				long lastModificationTime = 0;

				while (true) {
					if (!Config.CONFIG_FILE.exists() || Config.CONFIG_FILE.lastModified() != lastModificationTime) {
						lastModificationTime = Config.CONFIG_FILE.lastModified();
						log("Reloading configuration");

						synchronized (Main.class) {
							Main.config = new Config();
						}

						HotkeyAction screenshot = new HotkeyAction(Action.Screenshot, config.getHotkeys().get(Action.Screenshot),
							new HotkeyCallback() {
								@Override
								public void hotkey (Action action, Hotkey hotkey) {
									Screenshot.takeScreenshot();
								}
							});

						HotkeyAction appScreenshot = new HotkeyAction(Action.AppScreenshot, config.getHotkeys().get(Action.AppScreenshot),
							new HotkeyCallback() {
								@Override
								public void hotkey (Action action, Hotkey hotkey) {
									Screenshot.takeAppScreenshot();
								}
							});

						HotkeyAction regionScreenshot = new HotkeyAction(Action.RegionScreenshot,
							config.getHotkeys().get(Action.RegionScreenshot), new HotkeyCallback() {
								@Override
								public void hotkey (Action action, Hotkey hotkey) {
									Screenshot.takeRegionScreenshot();
								}
							});

						detector.setHotkeyActions(screenshot, appScreenshot, regionScreenshot);
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
