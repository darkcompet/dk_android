/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;

import tool.compet.core.log.DkLogs;
import tool.compet.core.util.DkStrings;

import static tool.compet.core.BuildConfig.DEBUG;

class MyBackStack {
    interface OnStackChangeListener {
        void onStackSizeChanged(int oldSize, int newSize);
    }

    private ArrayList<MyKeyState> keys;
    private OnStackChangeListener listener;

    public MyBackStack(OnStackChangeListener listener) {
        keys = new ArrayList<>();
        this.listener = listener;
    }

    public void restoreStates(MyBackStackState in) {
        if (in != null) {
            keys = in.keys;

            if (keys == null) {
                keys = new ArrayList<>();
            }

            if (DEBUG) {
                StringBuilder msg = new StringBuilder("[");
                Iterator<MyKeyState> it = keys.iterator();

                if (it.hasNext()) {
                    msg.append(it.next().tag);

                    while (it.hasNext()) {
                        msg.append(", ").append(it.next().tag);
                    }
                }

                DkLogs.info(this, "restore backstack keys to: " + msg.append("]").toString());
            }
        }
    }

    public Parcelable saveStates() {
        MyBackStackState out = new MyBackStackState();
        out.keys = keys;

        if (DEBUG) {
            StringBuilder msg = new StringBuilder("[");
            Iterator<MyKeyState> it = keys.iterator();

            if (it.hasNext()) {
                msg.append(it.next().tag);

                while (it.hasNext()) {
                    msg.append(", ").append(it.next().tag);
                }
            }

            DkLogs.info(this, "save backstack keys: " + msg.append("]").toString());
        }

        return out;
    }

    public int size() {
        return keys.size();
    }

    public int indexOf(MyKeyState key) {
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
        final int oldSize = keys.size();

        keys.clear();

        notifySizeChange(oldSize);
    }

    public boolean contains(String tag) {
        return indexOf(tag) >= 0;
    }

    public void add(MyKeyState key) {
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

    public void remove(MyKeyState key) {
        final int oldSize = keys.size();

        if (keys.remove(key)) {
            notifySizeChange(oldSize);
        }
    }

    public MyKeyState remove(String tag) {
        int index = indexOf(tag);

        if (index >= 0) {
            final int oldSize = keys.size();

            MyKeyState keyState = keys.remove(index);

            notifySizeChange(oldSize);

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

    private void notifySizeChange(int oldSize) {
        if (listener != null) {
            listener.onStackSizeChanged(oldSize, keys.size());
        }
    }
}
