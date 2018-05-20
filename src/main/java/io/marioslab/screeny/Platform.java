
package io.marioslab.screeny;

import java.awt.image.BufferedImage;

public interface Platform {
	public BufferedImage takeScreenshot ();

	public BufferedImage takeAppScreenshot ();

	public BufferedImage takeRegionScreenshot ();
}
