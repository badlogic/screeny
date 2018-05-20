
package io.marioslab.screeny;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
	public static byte[] readBytes (InputStream in) throws IOException {
		try (InputStream input = in) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024 * 100];
			int read = 0;
			while ((read = input.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			return out.toByteArray();
		}
	}

	public static String readString (InputStream in) throws IOException {
		return new String(readBytes(in), "UTF-8");
	}

	public static void writeToFile (File file, byte[] bytes, boolean append) throws IOException {
		try (FileOutputStream out = new FileOutputStream(file, append)) {
			out.write(bytes);
		}
	}

	public static void writeToFile (File file, String text, boolean append) throws IOException {
		try (FileOutputStream out = new FileOutputStream(file, append)) {
			out.write(text.getBytes("UTF-8"));
		}
	}
}
