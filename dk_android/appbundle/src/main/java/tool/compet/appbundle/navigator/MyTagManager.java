/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.navigator;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import tool.compet.core.BuildConfig;
import tool.compet.core.DkLogs;
import tool.compet.core.DkStrings;

// This manages children fragment (list of child framgnet inside the parent).
class MyTagManager {
	interface OnStackChangeListener {
		void onStackSizeChanged(int size, int oldSize);
	}

	// List of tag of child fragment (we can instantiate fragment from a tag)
	private List<MyTag> tags;
	private final OnStackChangeListener listener;

	MyTagManager(OnStackChangeListener listener) {
		this.tags = new ArrayList<>();
		this.listener = listener;
	}

	void restoreStates(MyTagsParcelable in) {
		if (in != null) {
			tags = in.tags;

			if (tags == null) {
				tags = new ArrayList<>();
			}

			if (BuildConfig.DEBUG) {
				DkLogs.info(this, "Restored backstack keys to: " + tags.toString());
			}
		}
	}

	Parcelable saveStates() {
		MyTagsParcelable out = new MyTagsParcelable();
		out.tags = tags;

		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "Saved backstack keys: " + tags.toString());
		}

		return out;
	}

	int size() {
		return tags.size();
	}

	int indexOf(MyTag myTag) {
		return tags.indexOf(myTag);
	}

	// last index
	int indexOf(String tag) {
		for (int index = tags.size() - 1; index >= 0; --index) {
			if (DkStrings.isEquals(tag, tags.get(index).tag)) {
				return index;
			}
		}
		return -1;
	}

	// Must assure valid range when call this
	MyTag get(int index) {
		return tags.get(index);
	}

	MyTag get(String tag) {
		for (MyTag myTag : tags) {
			if (DkStrings.isEquals(tag, myTag.tag)) {
				return myTag;
			}
		}
		return null;
	}

	void clear() {
		int oldSize = tags.size();
		tags.clear();

		notifySizeChanged(oldSize);
	}

	boolean contains(String tag) {
		return indexOf(tag) >= 0;
	}

	void add(MyTag myTag) {
		int oldSize = tags.size();
		tags.add(myTag);

		notifySizeChanged(oldSize);
	}

	void remove(int index) {
		if (index >= 0 && index < tags.size()) {
			int oldSize = tags.size();
			tags.remove(index);

			notifySizeChanged(oldSize);
		}
	}

	void remove(MyTag myTag) {
		final int oldSize = tags.size();

		if (tags.remove(myTag)) {
			notifySizeChanged(oldSize);
		}
	}

	MyTag remove(String tag) {
		int index = indexOf(tag);

		if (index >= 0) {
			int oldSize = tags.size();
			MyTag keyState = tags.remove(index);
			notifySizeChanged(oldSize);

			return keyState;
		}

		return null;
	}

	void moveToTop(String tag) {
		int index = indexOf(tag);

		if (index >= 0) {
			MyTag key = tags.remove(index);
			add(key);
		}
	}

	MyTagManager deepClone() {
		MyTagManager tagManager = new MyTagManager(this.listener);
		tagManager.tags = new ArrayList<>(this.tags);
		return tagManager;
	}

	void copyFrom(MyTagManager other) {
		this.tags = other.tags;
	}

	private void notifySizeChanged(int oldSize) {
		if (listener != null) {
			listener.onStackSizeChanged(oldSize, tags.size());
		}
	}

	static class MyTagsParcelable implements Parcelable {
		List<MyTag> tags;

		MyTagsParcelable() {
		}

		MyTagsParcelable(Parcel in) {
			tags = in.createTypedArrayList(MyTag.CREATOR);
		}

		static final Creator<MyTagsParcelable> CREATOR = new Creator<MyTagsParcelable>() {
			@Override
			public MyTagsParcelable createFromParcel(Parcel in) {
				return new MyTagsParcelable(in);
			}

			@Override
			public MyTagsParcelable[] newArray(int size) {
				return new MyTagsParcelable[size];
			}
		};

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeTypedList(tags);
		}
	}
}
