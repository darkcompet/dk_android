/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import android.os.Parcel;
import android.os.Parcelable;

public class MyKeyState implements Parcelable {
    String tag;

    public MyKeyState(String tag) {
        this.tag = tag;
    }

    public MyKeyState(Parcel in) {
        tag = in.readString();
    }

    public static final Creator<MyKeyState> CREATOR = new Creator<MyKeyState>() {
        @Override
        public MyKeyState createFromParcel(Parcel in) {
            return new MyKeyState(in);
        }

        @Override
        public MyKeyState[] newArray(int size) {
            return new MyKeyState[size];
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
