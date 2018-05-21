
package io.marioslab.screeny;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
			MenuItem openConfig = new MenuItem("Open config");
			MenuItem resetConfig = new MenuItem("Reset config");
			MenuItem openLog = new MenuItem("Open logs");

			MenuItem close = new MenuItem("Quit");
			menu.add(openConfig);
			menu.add(resetConfig);
			menu.add(openLog);
			menu.add(close);

			resetConfig.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed (ActionEvent event) {
					Config.reset();
				}
			});

			openLog.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed (ActionEvent event) {
					try {
						Desktop.getDesktop().edit(Main.LOG_FILE);
					} catch (Throwable e) {
						Main.failError("Couldn't open config file", e);
					}
				}
			});

			close.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed (ActionEvent e) {
					System.exit(0);
				}
			});

			trayIcon.setPopupMenu(menu);
			Main.log("Setup tray icon.");
		}
	}
}
