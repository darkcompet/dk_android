/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.navigator;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

class MyBackStackState implements Parcelable {
    ArrayList<MyKeyState> keys;

    public MyBackStackState() {
    }

    public MyBackStackState(Parcel in) {
        keys = in.createTypedArrayList(MyKeyState.CREATOR);
    }

    public static final Creator<MyBackStackState> CREATOR = new Creator<MyBackStackState>() {
        @Override
        public MyBackStackState createFromParcel(Parcel in) {
            return new MyBackStackState(in);
        }

        @Override
        public MyBackStackState[] newArray(int size) {
            return new MyBackStackState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(keys);
    }
}
