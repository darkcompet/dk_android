/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.appbundle.arch.navigator;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;

import tool.compet.core.util.DkLogs;
import tool.compet.core.util.DkStrings;

import static tool.compet.core.BuildConfig.DEBUG;

class BackStack {
	interface OnStackChangeListener {
		void onStackSizeChanged(int oldSize, int newSize);
	}

	private ArrayList<KeyState> keys;
	private OnStackChangeListener listener;

	public BackStack(OnStackChangeListener listener) {
		keys = new ArrayList<>();
		this.listener = listener;
	}

	public void restoreStates(BackStackState in) {
		if (in != null) {
			keys = in.keys;

			if (keys == null) {
				keys = new ArrayList<>();
			}

			if (DEBUG) {
				StringBuilder msg = new StringBuilder("[");
				Iterator<KeyState> it = keys.iterator();

				if (it.hasNext()) {
					msg.append(it.next().tag);

					while (it.hasNext()) {
						msg.append(", ").append(it.next().tag);
					}
				}

				DkLogs.log(this, "restore backstack keys to: " + msg.append("]").toString());
			}
		}
	}

	public Parcelable saveStates() {
		BackStackState out = new BackStackState();
		out.keys = keys;

		if (DEBUG) {
			StringBuilder msg = new StringBuilder("[");
			Iterator<KeyState> it = keys.iterator();

			if (it.hasNext()) {
				msg.append(it.next().tag);

				while (it.hasNext()) {
					msg.append(", ").append(it.next().tag);
				}
			}

			DkLogs.log(this, "save backstack keys: " + msg.append("]").toString());
		}

		return out;
	}

	public int size() {
		return keys.size();
	}

	public int indexOf(KeyState key) {
		return keys.indexOf(key);
	}

	public int indexOf(String tag) {
		for (int i = keys.size() - 1; i >= 0; --i) {
			if (DkStrings.isEquals(tag, keys.get(i).tag)) {
				return i;
			}
		}

		return -1;
	}

	public KeyState get(int index) {
		if (index < 0 || index >= keys.size()) {
			return null;
		}
		return keys.get(index);
	}

	public KeyState get(String tag) {
		for (KeyState key : keys) {
			if (DkStrings.isEquals(tag, key.tag)) {
				return key;
			}
		}

		return null;
	}

	public void clear() {
		final int oldSize = keys.size();

		keys.clear();

		notifySizeChange(oldSize);
	}

	public boolean contains(String tag) {
		return indexOf(tag) >= 0;
	}

	public void add(KeyState key) {
		final int oldSize = keys.size();

		keys.add(key);

		notifySizeChange(oldSize);
	}

	public void remove(int index) {
		if (index >= 0 && index < keys.size()) {
			final int oldSize = keys.size();

			keys.remove(index);

			notifySizeChange(oldSize);
		}
	}

	public void remove(KeyState key) {
		final int oldSize = keys.size();

		if (keys.remove(key)) {
			notifySizeChange(oldSize);
		}
	}

	public KeyState remove(String tag) {
		int index = indexOf(tag);

		if (index >= 0) {
			final int oldSize = keys.size();

			KeyState keyState = keys.remove(index);

			notifySizeChange(oldSize);

			return keyState;
		}

		return null;
	}

	public void moveToTop(String tag) {
		int index = indexOf(tag);

		if (index >= 0) {
			KeyState key = keys.remove(index);
			add(key);
		}
	}

	private void notifySizeChange(int oldSize) {
		if (listener != null) {
			listener.onStackSizeChanged(oldSize, keys.size());
		}
	}
}
