
package io.marioslab.screeny.storage;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import io.marioslab.screeny.Config.ScpStorageConfig;
import io.marioslab.screeny.Screeny;
import io.marioslab.screeny.Storage;
import io.marioslab.screeny.ui.UI;
import io.marioslab.screeny.utils.Log;

public class ScpStorage implements Storage {
	@Override
	public String save (BufferedImage image, String fileName) {
		for (ScpStorageConfig config : Screeny.getConfig().getEnabledStorageConfigs(ScpStorageConfig.class)) {
			if (config.user == null || config.user.isEmpty()) {
				Log.error("No Scp user specified in configuration", null);
				return null;
			}
			if (config.host == null || config.host.isEmpty()) {
				Log.error("No Scp host specified in configuration", null);
				return null;
			}
			if (config.port == 0) {
				Log.error("No Scp port specified in configuration", null);
				return null;
			}
			if (config.remoteDirectory == null || config.remoteDirectory.isEmpty()) {
				Log.error("No Scp remote directory specified in configuration", null);
				return null;
			}
			if (config.url == null || config.url.isEmpty()) {
				Log.error("No Scp URL specified in configuration", null);
				return null;
			}

			Session session = null;
			Channel channel = null;
			try {
				JSch jsch = new JSch();

				if (config.useKeyFile) {
					if (new File(config.keyFile).exists()) {
						if (config.keyFilePassphrase != null && !config.keyFilePassphrase.isEmpty()) {
							jsch.addIdentity(config.keyFile, config.keyFilePassphrase);
						} else {
							jsch.addIdentity(config.keyFile);
						}
					}
				}

				session = jsch.getSession(config.user, config.host, config.port);
				session.setConfig("StrictHostKeyChecking", "no");
				session.setPassword(config.password);
				session.connect();

				String command = "scp -t " + config.remoteDirectory;
				channel = session.openChannel("exec");
				((ChannelExec)channel).setCommand(command);

				try (OutputStream out = new BufferedOutputStream(channel.getOutputStream());
					InputStream in = new BufferedInputStream(channel.getInputStream())) {
					channel.connect();

					waitAck(in);

					File file = new File(System.getProperty("java.io.tmpdir"), "ssh-temp.png");
					ImageIO.write(image, "png", file);
					long fileSize = file.length();
					command = "C0644 " + fileSize + " ";
					command += fileName;
					command += "\n";

					out.write(command.getBytes("UTF-8"));
					out.flush();

					waitAck(in);

					try (FileInputStream fis = new FileInputStream(file)) {
						byte[] buffer = new byte[1024 * 100];

						while (true) {
							int len = fis.read(buffer);
							if (len <= 0) {
								break;
							}
							out.write(buffer, 0, len);
						}
						writeAck(out);
						out.flush();
						waitAck(in);
					}
				}
				channel.disconnect();
				session.disconnect();
				String url = (!config.url.endsWith("/") ? config.url + "/" : config.url) + fileName;

				StringSelection selection = new StringSelection(url);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, selection);

				Log.info("Stored screenshot to remote file " + url);
				UI.showNotification("Upload complete", "Stored image to " + url);
				return url;
			} catch (Throwable e) {
				if (channel != null) channel.disconnect();
				if (session != null) session.disconnect();
				Log.error("Couldn't transfer image via ssh.", e);
			}
		}
		return null;

	}

	private static void writeAck (OutputStream out) throws IOException {
		out.write(new byte[] {0});
	}

	private static void waitAck (InputStream in) throws IOException {
		int b = in.read();

		if (b == -1) {
			throw new IOException("No response from server");
		} else if (b != 0) {
			StringBuilder builder = new StringBuilder();
			int c = in.read();
			while (c > 0 && c != '\n') {
				builder.append(c);
				c = in.read();
			}

			if (b == 1) {
				throw new IOException("Scp error: " + builder.toString());
			} else if (b == 2) {
				throw new IOException("Scp error: " + builder.toString());
			} else {
				throw new IOException("Scp unknown response code " + b + ", " + builder.toString());
			}
		}
	}
}
