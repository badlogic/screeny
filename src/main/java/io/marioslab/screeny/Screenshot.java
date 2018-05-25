
package io.marioslab.screeny;

import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import io.marioslab.screeny.ui.UI;

public class Screenshot {
	static Robot robot;
	static boolean editNext = false;

	private static void saveScreenshot (BufferedImage image) {
		if (image == null) return;
		String fileName = ".png";
		if (Screeny.getConfig().addTimestampToFilename) fileName = new SimpleDateFormat("yyyymmdd-HHmmss").format(new Date()) + fileName;
		if (Screeny.getConfig().addUIDToFilename) {
			String id = Base64.getEncoder().encodeToString(asByteArray(UUID.randomUUID()));
			id = id.replace("==", "").replace("/", "a").replace("+", "b");
			fileName = id + "-" + fileName;
		}

		if (editNext) {
			UI.showEdit(image);
			editNext = false;
		}

		System.out.println(fileName);
		for (Storage storage : Screeny.getStorages()) {
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
		saveScreenshot(Screeny.getPlatform().takeScreenshot());
	}

	public static void takeAppScreenshot () {
		saveScreenshot(Screeny.getPlatform().takeAppScreenshot());
	}

	public static void takeRegionScreenshot () {
		saveScreenshot(Screeny.getPlatform().takeRegionScreenshot());
	}

	public static void editNext () {
		editNext = true;
	}
}
