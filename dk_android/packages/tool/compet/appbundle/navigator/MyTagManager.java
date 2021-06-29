/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.navigator;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import tool.compet.core4j.DkStrings;

// This manages children fragment (list of child framgnet inside the parent).
class MyTagManager {
	// List of tag of child fragment (we can instantiate fragment from a tag)
	private List<MyTag> tags = new ArrayList<>();

	void restoreFrom(MyTagsParcelable in) {
		if (in != null) {
			tags = in.tags;
			if (tags == null) {
				tags = new ArrayList<>();
			}
		}
	}

	Parcelable generateState() {
		MyTagsParcelable out = new MyTagsParcelable();
		out.tags = tags;
		return out;
	}

	int size() {
		return tags.size();
	}

	int lastIndexOf(String tag) {
		for (int index = tags.size() - 1; index >= 0; --index) {
			if (DkStrings.isEquals(tag, tags.get(index).tag)) {
				return index;
			}
		}
		return -1;
	}

	// Must pass valid range when call this
	MyTag get(int index) {
		return tags.get(index);
	}

	void clear() {
		tags.clear();
	}

	boolean contains(String tag) {
		return lastIndexOf(tag) >= 0;
	}

	void add(MyTag myTag) {
		tags.add(myTag);
	}

	void remove(MyTag myTag) {
		tags.remove(myTag);
	}

	void remove(String tag) {
		int index = lastIndexOf(tag);
		if (index >= 0) {
			tags.remove(index);
		}
	}

	MyTagManager deepClone() {
		MyTagManager tagManager = new MyTagManager();
		tagManager.tags = new ArrayList<>(this.tags);
		return tagManager;
	}

	void applyChanges(MyTagManager other) {
		this.tags = other.tags;
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
