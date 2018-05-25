
package io.marioslab.screeny;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;

import io.marioslab.screeny.ui.UI;
import io.marioslab.screeny.utils.Log;

public class Tray {
	public Tray () {
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().createImage(Screeny.class.getResource("/icon.png"));

			TrayIcon trayIcon = new TrayIcon(image, "Screeny");
			trayIcon.setImageAutoSize(true);
			trayIcon.setToolTip("Screeny");
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				Log.error("Couldn't set tray icon", e);
				Screeny.exit(-1);
			}

			PopupMenu menu = new PopupMenu();
			MenuItem openSettings = new MenuItem("Settings");
			MenuItem openLog = new MenuItem("Logs");
			MenuItem close = new MenuItem("Quit");
			menu.add(openSettings);
			menu.add(openLog);
			menu.add(close);

			openSettings.addActionListener( (event) -> {
				UI.showMain();
			});

			openLog.addActionListener( (event) -> {
				try {
					Desktop.getDesktop().edit(Log.LOG_FILE);
				} catch (Throwable e) {
					Log.error("Couldn't open config file", e);
				}
			});

			close.addActionListener( (event) -> {
				System.exit(0);
			});

			trayIcon.setPopupMenu(menu);
			Log.info("Setup tray.");
		}
	}
}
