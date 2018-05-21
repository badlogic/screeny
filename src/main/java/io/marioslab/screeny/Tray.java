
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

public class Tray {
	public Tray () {
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().createImage(Main.class.getResource("/icon.png"));

			TrayIcon trayIcon = new TrayIcon(image, "Screeny");
			trayIcon.setImageAutoSize(true);
			trayIcon.setToolTip("Screeny");
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				Main.failError("Couldn't set tray icon", e);
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
					Desktop.getDesktop().edit(Main.LOG_FILE);
				} catch (Throwable e) {
					Main.failError("Couldn't open config file", e);
				}
			});

			close.addActionListener( (event) -> {
				System.exit(0);
			});

			trayIcon.setPopupMenu(menu);
			Main.log("Setup tray.");
		}
	}
}
