
package io.marioslab.screeny.storage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.marioslab.screeny.Config.LocalStorageConfig;
import io.marioslab.screeny.Screeny;
import io.marioslab.screeny.Storage;
import io.marioslab.screeny.utils.Log;

public class LocalStorage implements Storage {
	@Override
	public String save (BufferedImage image, String fileName) {
		for (LocalStorageConfig config : Screeny.getConfig().getEnabledStorageConfigs(LocalStorageConfig.class)) {
			LocalStorageConfig localStorageConfig = config;
			File outputDir = new File(localStorageConfig.localDirectory);
			if (!outputDir.exists() && !outputDir.mkdirs()) {
				Log.error("Couldn't create local storage directory " + outputDir.getAbsolutePath(), null);
				continue;
			}
			File outputFile = new File(outputDir, fileName);
			try {
				ImageIO.write(image, "png", outputFile);
				Log.info("Stored screenshot to local file " + outputFile);
			} catch (IOException e) {
				Log.error("Couldn't write screenshot to file " + outputFile.getAbsolutePath(), e);
			}
		}
		return null;
	}
}
