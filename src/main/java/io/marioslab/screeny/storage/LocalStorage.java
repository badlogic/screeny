
package io.marioslab.screeny.storage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.marioslab.screeny.Main;
import io.marioslab.screeny.Storage;

public class LocalStorage implements Storage {

	@Override
	public String save (BufferedImage image, String fileName) {
		File outputFile = new File(Main.getConfig().getLocalConfig().localDirectory, fileName);
		try {
			ImageIO.write(image, "png", outputFile);
			Main.log("Stored screenshot to local file " + outputFile);
			return outputFile.getAbsolutePath();
		} catch (IOException e) {
			Main.failError("Couldn't write screenshot to file " + outputFile.getAbsolutePath(), e);
			return null;
		}
	}
}
