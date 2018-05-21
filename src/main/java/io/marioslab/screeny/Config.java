
package io.marioslab.screeny;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jnativehook.keyboard.NativeKeyEvent;

public class Config {
	public static final File CONFIG_FILE = new File(System.getProperty("user.home") + "/.screeny.config");

	private final Map<Action, Hotkey> hotkeys = new HashMap<Action, Hotkey>();
	private final LocalConfig localConfig;
	private final SshConfig sshConfig;

	public Config () {
		if (!CONFIG_FILE.exists()) {
			reset();
		}

		Properties props = new Properties();
		try (InputStream in = new FileInputStream(CONFIG_FILE)) {
			props.load(in);
		} catch (IOException e) {
			Main.failError("Couldn't read config file from " + CONFIG_FILE.getAbsolutePath(), e);
		}

		hotkeys.put(Action.Screenshot, parseHotKey(props, "screenshot"));
		hotkeys.put(Action.AppScreenshot, parseHotKey(props, "app_screenshot"));
		hotkeys.put(Action.RegionScreenshot, parseHotKey(props, "region_screenshot"));

		localConfig = new LocalConfig(props);
		sshConfig = new SshConfig(props);

		Main.log("Read configuration file.");
	}

	public static void reset () {
		try {
			byte[] defaultConfig = Utils.readBytes(Config.class.getResourceAsStream("/config.prop"));
			Utils.writeToFile(CONFIG_FILE, defaultConfig, false);
		} catch (Exception e) {
			Main.failError("Couldn't write default config file to " + CONFIG_FILE.getAbsolutePath(), e);
		}
	}

	public Map<Action, Hotkey> getHotkeys () {
		return hotkeys;
	}

	public LocalConfig getLocalConfig () {
		return localConfig;
	}

	public SshConfig getSshConfig () {
		return sshConfig;
	}

	private Hotkey parseHotKey (Properties props, String key) {
		String keysConfig = props.getProperty(key, null);
		if (keysConfig == null) {
			Main.log("Warning: no hotkey specified for action " + key);
			return new Hotkey(new HashSet<Integer>());
		}

		String[] tokens = keysConfig.split(" ");
		if (tokens.length == 0) {
			Main.log("Warning: no hotkey specified for action " + key);
			return null;
		}

		Set<Integer> keys = new HashSet<Integer>();
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i].toLowerCase();

			if (token.equals("ctrl")) {
				keys.add(NativeKeyEvent.VC_CONTROL);
				continue;
			}

			if (token.equals("alt")) {
				keys.add(NativeKeyEvent.VC_ALT);
				continue;
			}

			if (token.equals("shift")) {
				keys.add(NativeKeyEvent.VC_SHIFT);
				continue;
			}

			if (token.equals("meta")) {
				keys.add(NativeKeyEvent.VC_META);
				continue;
			}

			if (token.length() != 1) {
				Main.log("Warning: unknown hotkey " + token + "for action " + key);
				continue;
			}

			if (Character.isAlphabetic(token.charAt(0))) {
				keys.add(NativeKeyEvent.VC_A + (token.charAt(0) - 'a'));
				continue;
			}

			if (Character.isDigit(token.charAt(0))) {
				if (token.charAt(0) == '0') {
					keys.add(NativeKeyEvent.VC_0);
				} else {
					keys.add(NativeKeyEvent.VC_1 + (token.charAt(0) - '1'));
				}
				continue;
			}

			Main.log("Warning: unknown hotkey " + token + "for action " + key);
		}

		return new Hotkey(keys);
	}

	public static class Hotkey {
		public final Set<Integer> keys;

		public Hotkey (Set<Integer> keys) {
			this.keys = keys;
		}
	}

	public static class LocalConfig {
		public boolean enabled;
		public File localDirectory;

		public LocalConfig (Properties props) {
			enabled = Boolean.parseBoolean(props.getProperty("local_enabled", "true"));
			if (props.getProperty("local_directory") != null && !props.getProperty("local_directory").isEmpty())
				localDirectory = new File(props.getProperty("local_directory"));
			else
				localDirectory = new File(System.getProperty("user.home") + "/Desktop");
			if (!localDirectory.exists() && !localDirectory.mkdirs()) {
				localDirectory = new File(System.getProperty("user.home") + "/Desktop");
				Main.logError("Couldn't create local directory to store images in, defaulting to " + localDirectory.getAbsolutePath(), null);
			}
		}
	}

	public static class SshConfig {
		public boolean enabled;
		public final String user;
		public final String host;
		public final int port;
		public final String remoteDirectory;
		public final String url;
		public String keyFile;
		public final String keyFilePassphrase;
		public final String password;

		public SshConfig (Properties props) {
			this.enabled = Boolean.parseBoolean(props.getProperty("ssh_enabled", "false"));
			this.user = props.getProperty("ssh_user", null);
			this.host = props.getProperty("ssh_host", null);
			this.port = Integer.parseInt(props.getProperty("ssh_port", "0"));
			this.remoteDirectory = props.getProperty("ssh_remote_directory", null);
			this.url = props.getProperty("ssh_url", null);
			this.keyFile = props.getProperty("ssh_key_file");
			if (this.keyFile == null || this.keyFile.isEmpty()) keyFile = System.getProperty("user.home") + "/.ssh/id_rsa";
			this.keyFilePassphrase = props.getProperty("ssh_key_file_passphrase", null);
			this.password = props.getProperty("ssh_password", null);
		}
	}
}
