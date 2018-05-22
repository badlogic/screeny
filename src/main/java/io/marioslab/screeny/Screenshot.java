
package io.marioslab.screeny;

import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class Screenshot {
	static Robot robot;

	private static void saveScreenshot (BufferedImage image) {
		if (image == null) return;
		String fileName = ".png";
		if (Main.getConfig().addTimestampToFilename)
			fileName = new SimpleDateFormat("yyyymmdd-HHmmss").format(new Date()) + fileName;
		if (Main.getConfig().addUIDToFilename) {
			String id = Base64.getEncoder().encodeToString(asByteArray(UUID.randomUUID()));
			id = id.replace("==", "").replace("/", "a").replace("+", "b");
			fileName = id + "-" + fileName;
		}

		System.out.println(fileName);
		for (Storage storage : Main.getStorages()) {
			storage.save(image, fileName);
		}
	}

	private static byte[] asByteArray (UUID uuid) {
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();
		byte[] buffer = new byte[16];

		for (int i = 0; i < 8; i++) {
			buffer[i] = (byte)(msb >>> 8 * (7 - i));
		}
		for (int i = 8; i < 16; i++) {
			buffer[i] = (byte)(lsb >>> 8 * (7 - i));
		}
		return buffer;
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
