/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.navigator;

import android.os.Parcelable;

import java.util.ArrayList;

import tool.compet.core.BuildConfig;
import tool.compet.core.DkLogs;
import tool.compet.core.DkStrings;

// This manages children fragment (list of child framgnet inside the parent).
class MyBackStack {
	interface OnStackChangeListener {
		void onStackSizeChanged(int size, int oldSize);
	}

	// List of tag of child fragment (we can instantiate fragment from a tag)
	private ArrayList<MyKeyState> keys;
	private final OnStackChangeListener listener;

	public MyBackStack(OnStackChangeListener listener) {
		this.keys = new ArrayList<>();
		this.listener = listener;
	}

	public void restoreStates(MyBackStackState in) {
		if (in != null) {
			keys = in.keys;

			if (keys == null) {
				keys = new ArrayList<>();
			}

			if (BuildConfig.DEBUG) {
				DkLogs.info(this, "Restored backstack keys to: " + keys.toString());
			}
		}
	}

	public Parcelable saveStates() {
		MyBackStackState out = new MyBackStackState();
		out.keys = keys;

		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "Saved backstack keys: " + keys.toString());
		}

		return out;
	}

	public int size() {
		return keys.size();
	}

	public int indexOf(MyKeyState key) {
		return keys.indexOf(key);
	}

	// last index
	public int indexOf(String tag) {
		for (int index = keys.size() - 1; index >= 0; --index) {
			if (DkStrings.isEquals(tag, keys.get(index).tag)) {
				return index;
			}
		}
		return -1;
	}

	public MyKeyState get(int index) {
		if (index < 0 || index >= keys.size()) {
			return null;
		}
		return keys.get(index);
	}

	public MyKeyState get(String tag) {
		for (MyKeyState key : keys) {
			if (DkStrings.isEquals(tag, key.tag)) {
				return key;
			}
		}
		return null;
	}

	public void clear() {
		int oldSize = keys.size();
		keys.clear();

		notifySizeChanged(oldSize);
	}

	public boolean contains(String tag) {
		return indexOf(tag) >= 0;
	}

	public void add(MyKeyState key) {
		int oldSize = keys.size();
		keys.add(key);

		notifySizeChanged(oldSize);
	}

	public void remove(int index) {
		if (index >= 0 && index < keys.size()) {
			int oldSize = keys.size();
			keys.remove(index);

			notifySizeChanged(oldSize);
		}
	}

	public void remove(MyKeyState key) {
		final int oldSize = keys.size();

		if (keys.remove(key)) {
			notifySizeChanged(oldSize);
		}
	}

	public MyKeyState remove(String tag) {
		int index = indexOf(tag);

		if (index >= 0) {
			int oldSize = keys.size();
			MyKeyState keyState = keys.remove(index);
			notifySizeChanged(oldSize);

			return keyState;
		}

		return null;
	}

	public void moveToTop(String tag) {
		int index = indexOf(tag);

		if (index >= 0) {
			MyKeyState key = keys.remove(index);
			add(key);
		}
	}

	private void notifySizeChanged(int oldSize) {
		if (listener != null) {
			listener.onStackSizeChanged(oldSize, keys.size());
		}
	}
}
