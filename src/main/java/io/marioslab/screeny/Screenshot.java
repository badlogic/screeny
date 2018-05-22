
package io.marioslab.screeny;

import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.UUID;

public class Screenshot {
	static Robot robot;

	private static void saveScreenshot (BufferedImage image) {
		if (image == null) return;
		String fileName = UUID.randomUUID().toString() + ".png";
		for (Storage storage : Main.getStorages()) {
			storage.save(image, fileName);
		}
	}

	public static void takeScreenshot () {
		saveScreenshot(Main.getPlatform().takeScreenshot());
	}

	public static void takeAppScreenshot () {
		saveScreenshot(Main.getPlatform().takeAppScreenshot());
	}

	public static void takeRegionScreenshot () {
		saveScreenshot(Main.getPlatform().takeRegionScreenshot());
	}
}
