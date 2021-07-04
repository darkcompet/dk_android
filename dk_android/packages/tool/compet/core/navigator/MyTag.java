/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.navigator;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

class MyTag implements Parcelable {
	String tag;

	public MyTag(String tag) {
		this.tag = tag;
	}

	public MyTag(Parcel in) {
		tag = in.readString();
	}

	public static final Creator<MyTag> CREATOR = new Creator<MyTag>() {
		@Override
		public MyTag createFromParcel(Parcel in) {
			return new MyTag(in);
		}

		@Override
		public MyTag[] newArray(int size) {
			return new MyTag[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(tag);
	}

	@NonNull
	@Override
	public String toString() {
		return "MyTag{" + "tag='" + tag + '\'' + '}';
	}
}
