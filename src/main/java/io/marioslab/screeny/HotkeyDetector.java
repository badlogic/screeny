
package io.marioslab.screeny;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyAdapter;
import org.jnativehook.keyboard.NativeKeyEvent;

public class HotkeyDetector {
	private final Set<Integer> pressedKeys = new HashSet<Integer>();
	private List<HotkeyAction> actions = new ArrayList<HotkeyAction>();

	public HotkeyDetector () {
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		logger.setUseParentHandlers(false);

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			Main.failError("Couldn't register keyboard hooks", e);
		}

		GlobalScreen.addNativeKeyListener(new NativeKeyAdapter() {
			@Override
			public void nativeKeyPressed (NativeKeyEvent keyEvent) {
				synchronized (pressedKeys) {
					pressedKeys.add(keyEvent.getKeyCode());
					System.out.println(pressedKeys);
				}

				synchronized (this) {
					for (HotkeyAction action : actions) {
						if (action.matches(pressedKeys)) {
							action.callback.hotkey();
							break;
						}
					}
				}
			}

			@Override
			public void nativeKeyReleased (NativeKeyEvent keyEvent) {
				synchronized (pressedKeys) {
					pressedKeys.remove(keyEvent.getKeyCode());
					System.out.println(pressedKeys);
				}
			}
		});

		Main.log("Setup hotkey detector.");
	}

	public int[] getPressedKeys () {
		synchronized (pressedKeys) {
			int[] keys = new int[pressedKeys.size()];
			int i = 0;
			for (Integer key : pressedKeys) {
				keys[i++] = key;
			}
			return keys;
		}
	}

	public void clearHotkeyActions () {
		synchronized (this) {
			actions.clear();
		}
	}

	public void addHotkeyAction (int[] hotkey, HotkeyCallback callback) {
		synchronized (this) {
			actions.add(new HotkeyAction(hotkey, callback));
			Collections.sort(actions);
		}
	}

	private static class HotkeyAction implements Comparable<HotkeyAction> {
		public final Set<Integer> hotkey = new HashSet<Integer>();
		public final HotkeyCallback callback;

		public HotkeyAction (int[] hotkey, HotkeyCallback callback) {
			for (int key : hotkey)
				this.hotkey.add(key);
			this.callback = callback;
		}

		public boolean matches (Set<Integer> pressedKeys) {
			if (hotkey.size() == 0) return false;
			if (hotkey.size() != pressedKeys.size()) return false;
			for (Integer key : hotkey)
				if (!pressedKeys.contains(key)) return false;
			return true;
		}

		@Override
		public int compareTo (HotkeyAction o) {
			return o.hotkey.size() - hotkey.size();
		}
	}

	public static interface HotkeyCallback {
		public void hotkey ();
	}
}
