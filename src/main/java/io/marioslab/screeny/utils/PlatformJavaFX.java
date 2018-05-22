
package io.marioslab.screeny.utils;

import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;

import io.marioslab.screeny.Main;
import io.marioslab.screeny.Platform;
import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PlatformJavaFX implements Platform {
	private Robot robot;

	private BufferedImage roboCapture (java.awt.Rectangle bounds) {
		if (robot == null) try {
			robot = new Robot();
		} catch (AWTException e) {
			Main.failError("Couldn't create screenshot robot", e);
		}
		return robot.createScreenCapture(bounds);
	}

	@Override
	public BufferedImage takeScreenshot () {		
		return roboCapture(new java.awt.Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
	}

	@Override
	public BufferedImage takeAppScreenshot () {
		return roboCapture(new java.awt.Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
	}

	@Override
	public BufferedImage takeRegionScreenshot () {
		java.awt.Rectangle bounds = new Selection().start();
		double scale = Toolkit.getDefaultToolkit().getScreenSize().width / Screen.getPrimary().getBounds().getWidth();
		bounds.x *= scale;
		bounds.y *= scale;
		bounds.width *= scale;
		bounds.height *= scale;
		if (bounds != null) return roboCapture(bounds);
		return null;
	}

	public static class Selection {
		CountDownLatch latch = new CountDownLatch(1);
		Stage stage;
		double startX = -1, startY = -1, endX = -1, endY = -1;

		public java.awt.Rectangle start () {
			javafx.application.Platform.runLater( () -> {
				Group root = new Group();
				Rectangle rect = new Rectangle();
				rect.setStroke(Color.RED);
				rect.setFill(new Color(1, 0, 0, 0.2));
				root.getChildren().add(rect);

				AnimationTimer loop = new AnimationTimer() {
					@Override
					public void handle (long now) {
						if (startX != -1) {
							rect.setX(Math.min(startX, endX));
							rect.setY(Math.min(startY, endY));
							rect.setWidth(Math.abs(startX - endX));
							rect.setHeight(Math.abs(startY - endY));
						}
					}
				};

				Scene scene = new Scene(root, new Color(0, 0, 0, 0.2));

				stage = new Stage();
				stage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
					if (KeyCode.ESCAPE == event.getCode()) {
						stage.close();
						loop.stop();
						latch.countDown();
						startX = -1;
					}
				});

				stage.addEventHandler(MouseEvent.MOUSE_DRAGGED, (event) -> {
					if (startX == -1) {
						startX = event.getScreenX();
						startY = event.getScreenY();
					}
					endX = event.getScreenX();
					endY = event.getScreenY();
				});

				stage.addEventHandler(MouseEvent.MOUSE_RELEASED, (event) -> {
					endX = event.getScreenX();
					endY = event.getScreenY();
					stage.close();
					loop.stop();
					latch.countDown();
				});

				scene.getRoot().setStyle("-fx-background-color: transparent");

				Rectangle2D bounds = Screen.getPrimary().getBounds();
				stage.setX(0);
				stage.setY(0);
				stage.setWidth(bounds.getWidth());
				stage.setHeight(bounds.getHeight());
				stage.setAlwaysOnTop(true);
				stage.initStyle(StageStyle.TRANSPARENT);
				stage.setScene(scene);

				loop.start();
				stage.show();
			});

			try {
				latch.await();
			} catch (InterruptedException e) {
			}

			if (startX == -1)
				return null;
			else
				return new java.awt.Rectangle((int)Math.min(startX, endX), (int)Math.min(startY, endY), (int)Math.abs(startX - endX),
					(int)Math.abs(startY - endY));
		}
	}
}
