
package io.marioslab.screeny.platform;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import io.marioslab.screeny.Main;
import io.marioslab.screeny.Platform;
import io.marioslab.screeny.Utils;

public class PlatformMac implements Platform {
	private static final File OUTPUT_FILE = new File(System.getProperty("java.io.tmpdir"), "capture.png");

	@Override
	public BufferedImage takeScreenshot () {
		try {
			Process process = new ProcessBuilder().command("/usr/sbin/screencapture", "-x", "-m", OUTPUT_FILE.getAbsolutePath()).start();
			int result = process.waitFor();
			if (result != 0) {
				Main.logError("Couldn't capture region screenshot.", null);
			}
			return ImageIO.read(OUTPUT_FILE);
		} catch (Throwable e) {
			Main.logError("Couldn't capture region screenshot.", e);
			return null;
		}
	}

	@Override
	public BufferedImage takeAppScreenshot () {
		try {
			Process process = new ProcessBuilder()
				.command("/usr/sbin/screencapture", "-i", "-o", "-w", "-W", "-x", OUTPUT_FILE.getAbsolutePath()).start();
			int result = process.waitFor();
			if (result != 0) {
				Main.logError("Couldn't capture region screenshot: " + Utils.readString(process.getInputStream()), null);
			}
			return ImageIO.read(OUTPUT_FILE);
		} catch (Throwable e) {
			Main.logError("Couldn't capture region screenshot.", e);
			return null;
		}
	}

	@Override
	public BufferedImage takeRegionScreenshot () {
		try {
			Process process = new ProcessBuilder().command("/usr/sbin/screencapture", "-i", "-o", "-s", "-x", OUTPUT_FILE.getAbsolutePath())
				.start();
			int result = process.waitFor();
			if (result != 0) {
				Main.logError("Couldn't capture region screenshot.", null);
			}
			return ImageIO.read(OUTPUT_FILE);
		} catch (Throwable e) {
			Main.logError("Couldn't capture region screenshot.", e);
			return null;
		}
	}
}
