
package io.marioslab.screeny;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

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

//			trayIcon.addMouseListener(new MouseAdapter() {
//				@Override
//				public void mouseClicked (MouseEvent e) {
//					JFrame frame = new JFrame();
//					TextField textField = new TextField("Hello world");
//					frame.add(textField);
//					int sta = frame.getExtendedState() & ~JFrame.ICONIFIED & JFrame.NORMAL;
//					frame.setExtendedState(sta);
//					frame.setAlwaysOnTop(true);
//					frame.setAutoRequestFocus(true);
//					frame.setFocusable(true);
//					frame.pack();
//					frame.addFocusListener(new FocusListener() {
//						@Override
//						public void focusGained (FocusEvent e) {
//						}
//
//						@Override
//						public void focusLost (FocusEvent e) {
//							frame.setVisible(false);
//						}
//					});
//					frame.setVisible(!frame.isVisible());
//					frame.toFront();
//					frame.requestFocus();
//					frame.repaint();
//				}
//			});

			Main.log("Setup tray.");
		}
	}
}
