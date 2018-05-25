
package io.marioslab.screeny.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.marioslab.screeny.Config.LocalStorageConfig;
import io.marioslab.screeny.Config.ScpStorageConfig;
import io.marioslab.screeny.Config.StorageConfig;

public class Utils {
	// @off
	private final static Gson gson = new GsonBuilder()
		.registerTypeAdapterFactory(
			RuntimeTypeAdapterFactory.of(StorageConfig.class)
				.registerSubtype(LocalStorageConfig.class, "LocalStorageConfig")
				.registerSubtype(ScpStorageConfig.class, "SshStorageConfig"))
		.setPrettyPrinting().create();
	// @on

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

	public static String toJson (Object obj) {
		return gson.toJson(obj);
	}

	public static <T> T fromJson (String json, Class<T> clazz) {
		return gson.fromJson(json, clazz);
	}
}
