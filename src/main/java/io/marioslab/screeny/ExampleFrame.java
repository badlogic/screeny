
package io.marioslab.screeny;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ExampleFrame extends JFrame {

	private ExamplePanel selectionPane;

	public ExampleFrame () {
		this.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped (KeyEvent e) {

			}

			@Override
			public void keyPressed (KeyEvent e) {

			}

			@Override
			public void keyReleased (KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					ExampleFrame.this.dispatchEvent(new WindowEvent(ExampleFrame.this, WindowEvent.WINDOW_CLOSING));
				}
			}
		});

		this.setExtendedState(JFrame.MAXIMIZED_BOTH);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSize);

		this.setUndecorated(true);

		this.setBackground(new Color(255, 255, 255, 0));

		populate();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setType(Window.Type.UTILITY);
		this.setVisible(true);
	}

	private void populate () {
		this.selectionPane = new ExamplePanel();
		this.setContentPane(selectionPane);
	}

	public static void main (String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run () {
				new ExampleFrame();
			}
		});
	}

	public static class ExamplePanel extends JPanel {

		private static Color bg = new Color(0, 0, 0);

		private int sx = -1, sy = -1, ex = -1, ey = -1;

		public ExamplePanel () {

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
				}

				@Override
				public void mouseDragged (MouseEvent e) {
					ex = e.getX();
					ey = e.getY();
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

			Area area = new Area(new Rectangle(0, 0, getWidth(), getHeight()));

			if (!(sx == -1 || sy == -1 || ex == -1 || ey == -1)) {

				int asx = Math.min(sx, ex);
				int asy = Math.min(sy, ey);

				int w = Math.abs(ex - sx);
				int h = Math.abs(ey - sy);

				area.subtract(new Area(new Rectangle(asx - 1, asy - 1, w + 2, h + 2)));
			}
			g2.setComposite(AlphaComposite.Src.derive(.25f));
			g2.setPaint(bg);
			g2.fill(area);

			g2.setComposite(AlphaComposite.Src.derive(1f));
			g2.setPaint(Color.WHITE);
			g2.drawString("Press Escape to exit", 10, 20);
			g2.dispose();
			repaint();
		}
	}

}
