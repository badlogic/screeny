
package io.marioslab.screeny.storage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.marioslab.screeny.Config.LocalStorageConfig;
import io.marioslab.screeny.Main;
import io.marioslab.screeny.Storage;

public class LocalStorage implements Storage {
	@Override
	public String save (BufferedImage image, String fileName) {
		for (LocalStorageConfig config : Main.getConfig().getEnabledStorageConfigs(LocalStorageConfig.class)) {
			LocalStorageConfig localStorageConfig = config;
			File outputDir = new File(localStorageConfig.localDirectory);
			if (!outputDir.exists() && !outputDir.mkdirs()) {
				Main.logError("Couldn't create local storage directory " + outputDir.getAbsolutePath(), null);
				continue;
			}
			File outputFile = new File(outputDir, fileName);
			try {
				ImageIO.write(image, "png", outputFile);
				Main.log("Stored screenshot to local file " + outputFile);
			} catch (IOException e) {
				Main.failError("Couldn't write screenshot to file " + outputFile.getAbsolutePath(), e);
			}
		}
		return null;
	}
}
