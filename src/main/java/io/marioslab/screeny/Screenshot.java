
package io.marioslab.screeny;

import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

public class Screenshot {
	static Robot robot;

	private static void saveScreenshot (BufferedImage image) {
		if (image == null) return;

		File outputFile = new File(Main.getConfig().getLocalConfig().localDirectory, UUID.randomUUID().toString() + ".png");
		try {
			ImageIO.write(image, "png", outputFile);
		} catch (IOException e) {
			Main.failError("Couldn't write screenshot to file " + outputFile.getAbsolutePath(), e);
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
