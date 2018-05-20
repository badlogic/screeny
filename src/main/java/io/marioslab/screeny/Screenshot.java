
package io.marioslab.screeny;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Screenshot {
	static Robot robot;

	private static void uploadScreenshot (Rectangle bounds) {
		if (robot == null) try {
			robot = new Robot();
		} catch (AWTException e) {
			Main.failError("Couldn't create screenshot robot", e);
		}

		BufferedImage image = robot.createScreenCapture(bounds);
		File outputFile = new File(Main.getConfig().getLocalConfig().localDirectory, UUID.randomUUID().toString() + ".png");
		try {
			ImageIO.write(image, "png", outputFile);
		} catch (IOException e) {
			Main.failError("Couldn't write screenshot to file " + outputFile.getAbsolutePath(), e);
		}
	}

	public static void takeScreenshot () {
		uploadScreenshot(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
	}

	public static void takeAppScreenshot () {
		Rectangle bounds = getBoundsOfForegroundWindow();
		if (bounds == null) {
			Main.logError("Couldn't take screenshot of app", null);
		} else {
			uploadScreenshot(bounds);
		}
	}

	private static Rectangle getBoundsOfForegroundWindow () {
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			Main.log("Getting bounds of foreground window on macOs");

			try {
				byte[] bytes = Utils.readBytes(Screenshot.class.getResourceAsStream("/bounds.scpt"));
				File scriptFile = new File("/tmp/bounds.scpt");
				Utils.writeToFile(scriptFile, bytes, false);
				scriptFile.setExecutable(true);
				Process process = new ProcessBuilder().command(scriptFile.getAbsolutePath()).start();
				int result = process.waitFor();
				if (result != 0) return null;
				String bounds = Utils.readString(process.getInputStream());
				String[] tokens = bounds.split(",");
				return new Rectangle(Integer.parseInt(tokens[0].trim()), Integer.parseInt(tokens[1].trim()), Integer.parseInt(tokens[2].trim()),
					Integer.parseInt(tokens[3].trim()));
			} catch (Throwable t) {
				Main.logError("Couldn't get bounds of foreground window", t);
				return null;
			}
		}

		return null;
	}

	public static void takeRegionScreenshot () {
		Rectangle bounds = new SelectionFrame().start();
		if (bounds != null) uploadScreenshot(bounds);
	}

	private static class SelectionFrame extends JFrame {
		private volatile boolean disposed = false;
		private volatile boolean cancled = false;
		private int sx = -1, sy = -1, ex = -1, ey = -1;

		public SelectionFrame () {
			super();
			this.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased (KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						SelectionFrame.this.dispose();
						cancled = true;
						disposed = true;
					}
				}
			});
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			setTitle("Screeny");
			setSize(screenSize);
			setUndecorated(true);
			setBackground(new Color(0, 0, 0, 0));
			setAlwaysOnTop(true);
			setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

			setContentPane(new JPanel() {
				{
					MouseAdapter mouseAdapter = new MouseAdapter() {
						@Override
						public void mousePressed (MouseEvent e) {
							sx = sy = ex = ey = -1;
							sx = e.getX();
							sy = e.getY();
						}

						@Override
						public void mouseReleased (MouseEvent e) {
							ex = e.getX();
							ey = e.getY();
							SelectionFrame.this.dispose();
							disposed = true;
						}

						@Override
						public void mouseDragged (MouseEvent e) {
							ex = e.getX();
							ey = e.getY();
							repaint();
						}
					};

					this.addMouseListener(mouseAdapter);
					this.addMouseMotionListener(mouseAdapter);
					this.setOpaque(false);
				}

				@Override
				protected void paintComponent (Graphics g) {
					super.paintComponent(g);
					Graphics2D g2 = (Graphics2D)g.create();

					// Area area = new Area(new Rectangle(0, 0, getWidth(), getHeight()));
					if (!(sx == -1 || sy == -1 || ex == -1 || ey == -1)) {
						int x = Math.min(sx, ex);
						int y = Math.min(sy, ey);
						int w = Math.abs(ex - sx);
						int h = Math.abs(ey - sy);
// area.subtract(new Area(new Rectangle(x - 1, y - 1, w + 2, h + 2)));
						g2.setColor(new Color(0.7f, 0, 0));
						g2.drawRect(x, y, w, h);
					}

// g2.setComposite(AlphaComposite.Src.derive(.25f));
// g2.setPaint(new Color(0, 0, 0));
// g2.fill(area);
// g2.setComposite(AlphaComposite.Src.derive(1f));

					g2.setPaint(Color.RED);
					g2.drawString("Press Escape to cancel", 10, 20);
					g2.dispose();
				}
			});

			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setType(Window.Type.UTILITY);
		}

		Rectangle start () {
			disposed = false;
			sx = sy = ex = ey = -1;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run () {
					SelectionFrame.this.setVisible(true);
					SelectionFrame.this.setAlwaysOnTop(true);
				}
			});
			while (!disposed) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e1) {
				}
			}
			while (this.isActive())
				;
			if (cancled || Math.abs(sx - ex) == 0 || Math.abs(sy - ey) == 0)
				return null;
			else
				return new Rectangle(Math.min(sx, ex), Math.min(sy, ey), Math.abs(sx - ex), Math.abs(sy - ey));
		}
	}
}
