
package io.marioslab.screeny.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.marioslab.screeny.Screeny;

public class Log {
	public static final File LOG_FILE = new File(Screeny.APP_DIR, "log.txt");

	static {
		try {
			Utils.writeToFile(LOG_FILE, "", false);
		} catch (IOException e) {
		}
	}

	public static enum LogType {
		Info, Warning, Error
	}

	public static void info (String message) {
		log(LogType.Info, message, null);
	}

	public static void error (String message, Throwable t) {
		log(LogType.Error, message, t);
	}

	private static synchronized void log (LogType type, String message, Throwable exception) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
			message = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss ").format(new Date()) + " " + type + ": " + message + "\n";
			writer.write(message);

			if (exception != null) {
				writer.write(exception.getMessage());
				exception.printStackTrace(writer);
			}

			System.out.print(message);
			if (exception != null) {
				System.out.println(exception.getMessage());
				exception.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
