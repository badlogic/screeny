
package io.marioslab.screeny;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyAdapter;
import org.jnativehook.keyboard.NativeKeyEvent;

import io.marioslab.screeny.Config.Hotkey;

public class HotkeyDetector {
	private final Set<Integer> pressedKeys = new HashSet<Integer>();
	private HotkeyAction[] actions = new HotkeyAction[0];

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
				pressedKeys.add(keyEvent.getKeyCode());

				synchronized (this) {
					for (HotkeyAction action : actions) {
						if (action.matches(pressedKeys)) {
							Main.log("Triggered action " + action.action);
							action.callback.hotkey(action.action, action.hotkey);
							break;
						}
					}
				}
			}

			@Override
			public void nativeKeyReleased (NativeKeyEvent keyEvent) {
				pressedKeys.remove(keyEvent.getKeyCode());
			}
		});

		Main.log("Setup hotkey detector.");
	}

	public void setHotkeyActions (HotkeyAction... actions) {
		Arrays.sort(actions);
		synchronized (this) {
			this.actions = actions;
		}
	}

	public static class HotkeyAction implements Comparable<HotkeyAction> {
		public final Action action;
		public final Hotkey hotkey;
		public final HotkeyCallback callback;

		public HotkeyAction (Action action, Hotkey hotkey, HotkeyCallback callback) {
			this.action = action;
			this.hotkey = hotkey;
			this.callback = callback;
		}

		public boolean matches (Set<Integer> pressedKeys) {
			if (hotkey.keys.size() == 0) return false;
			if (hotkey.keys.size() != pressedKeys.size()) return false;
			for (Integer key : hotkey.keys)
				if (!pressedKeys.contains(key)) return false;
			return true;
		}

		@Override
		public int compareTo (HotkeyAction o) {
			return o.hotkey.keys.size() - hotkey.keys.size();
		}
	}

	public static interface HotkeyCallback {
		public void hotkey (Action action, Hotkey hotkey);
	}
}
