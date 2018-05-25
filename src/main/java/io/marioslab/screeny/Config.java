
package io.marioslab.screeny;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jnativehook.keyboard.NativeKeyEvent;

import io.marioslab.screeny.utils.Log;
import io.marioslab.screeny.utils.Utils;

public class Config {
	/** Default location of the config file **/
	public static final File CONFIG_FILE = new File(Screeny.APP_DIR, "config.json");

	/** Whether to add a timestamp at the end of the file **/
	public boolean addTimestampToFilename = true;

	/** Whether to add a unique ID to the filename **/
	public boolean addUIDToFilename = true;

	/** The hotkeys for various actions, encoded as one string per key. A key may be one of META, CTRL, ALT, SHIFT, a-z, 0-8,
	 * F1-F12 */
	public int[] screenshotHotkey = new int[] {NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_SHIFT, NativeKeyEvent.VC_3};
	public int[] appScreenshotHotkey = new int[] {NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_SHIFT, NativeKeyEvent.VC_4};
	public int[] regionScreenshotHotkey = new int[] {NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_SHIFT, NativeKeyEvent.VC_5};
	public int[] editNextHotkey = new int[] {NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_SHIFT, NativeKeyEvent.VC_6};
	public int[] settingsHotkey = new int[] {NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_SHIFT, NativeKeyEvent.VC_7};

	/** Storage configurations */
	public List<StorageConfig> storageConfigs = Arrays.asList(new LocalStorageConfig("Local storage"), new ScpStorageConfig("marioslab.io"));

	Config () {
	}

	/** Creates are reads the config file from {@link Config#CONFIG_FILE}. */
	public static Config load () {
		try {
			if (!CONFIG_FILE.exists()) {
				Config config = new Config();
				config.save();
				return config;
			} else {
				return Utils.fromJson(Utils.readString(new FileInputStream(CONFIG_FILE)), Config.class);
			}
		} catch (Exception e) {
			Log.error("Couldn't load configuration.", e);
			return new Config();
		}
	}

	/** Saves this configuration to {@link Config#CONFIG_FILE} **/
	public void save () {
		try {
			Utils.writeToFile(CONFIG_FILE, Utils.toJson(this), false);
		} catch (Exception e) {
			Log.error("Couldn't save configuration.", e);
		}
	}

	/** @return the storage configurations. **/
	public List<StorageConfig> getStorageConfigs () {
		return storageConfigs;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public <T> List<T> getEnabledStorageConfigs (Class<T> clazz) {
		List<T> result = new ArrayList();
		for (StorageConfig config : storageConfigs) {
			if (clazz.isInstance(config)) result.add((T)config);
		}
		return result;
	}

	/** All storage configurations have a name and a flag indicating whether the configuration is enabled and used for each
	 * upload. */
	public static abstract class StorageConfig {
		public String name;

		StorageConfig () {
		}

		StorageConfig (String name) {
			this.name = name;
		}
	}

	/** Local storage configuration, stores the local directory to store files to. */
	public static class LocalStorageConfig extends StorageConfig {
		public String localDirectory = System.getProperty("user.home") + "/Desktop";

		LocalStorageConfig () {
			super();
		}

		LocalStorageConfig (String name) {
			super(name);
		}
	}

	/** Scp storage configuration, including user@host:port triple, remote directory, URL at which the uploads will be available,
	 * and optionally a pasword, SSH key file and key file passphrase. */
	public static class ScpStorageConfig extends StorageConfig {
		public String user;
		public String host;
		public int port;
		public String remoteDirectory;
		public String url;
		public String password;
		public boolean useKeyFile = false;
		public String keyFile = System.getProperty("user.home") + "/.ssh/id_rsa";
		public String keyFilePassphrase;

		ScpStorageConfig () {
			super();
		}

		ScpStorageConfig (String name) {
			super(name);
		}
	}
}
