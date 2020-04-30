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

import android.os.Parcel;
import android.os.Parcelable;

public class KeyState implements Parcelable {
	String tag;

	public KeyState(String tag) {
		this.tag = tag;
	}

	public KeyState(Parcel in) {
		tag = in.readString();
	}

	public static final Creator<KeyState> CREATOR = new Creator<KeyState>() {
		@Override
		public KeyState createFromParcel(Parcel in) {
			return new KeyState(in);
		}

		@Override
		public KeyState[] newArray(int size) {
			return new KeyState[size];
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
}
