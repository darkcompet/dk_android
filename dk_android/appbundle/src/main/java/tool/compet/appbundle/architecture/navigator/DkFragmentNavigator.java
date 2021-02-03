/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.navigator;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import tool.compet.appbundle.architecture.DkFragment;
import tool.compet.core.log.DkLogs;

/**
 * Differ with stack of Activities, the important feature of this navigator is,
 * we can re-arrange fragments in stack.
 */
public class DkFragmentNavigator implements MyBackStack.OnStackChangeListener {
    public interface Callback {
        void onActive(boolean isResume);

        void onInactive(boolean isPause);
    }

    private static final String KEY_BACKSTACK_STATE = "DkFragmentNavigator.KEY_BACKSTACK_STATE";

    final int containerId;
    final FragmentManager fm;
    final MyBackStack stack;

    private final Callback callback;

    public DkFragmentNavigator(int containerId, FragmentManager fm, Callback cb) {
        this.containerId = containerId;
        this.fm = fm;
        this.callback = cb;
        this.stack = new MyBackStack(this);
    }

    @Override
    public void onStackSizeChanged(int oldSize, int newSize) {
        if (newSize == 0) {
            if (callback != null) {
                callback.onActive(false);
            }
        }
        else if (newSize == 1 && oldSize == 0) {
            if (callback != null) {
                callback.onInactive(false);
            }
        }
    }

    public MyFragmentTransactor beginTransaction() {
        return new MyFragmentTransactor(this);
    }

    /**
     * Dismiss child fragment.
     *
     * @return false to tell the the fragment need handle the back-event. Otherwise this will dismiss child.
     */
    public boolean onBackPressed() {
        int lastIndex = stack.size() - 1;
        if (lastIndex < 0) {
            return false;
        }

        Fragment f = fm.findFragmentByTag(stack.get(lastIndex).tag);
        if (f == null) {
            return false;
        }

        // Finish target fragment
        if (f instanceof DkFragment) {
            DkFragment child = (DkFragment) f;
            if (! child.onBackPressed()) {
                child.dismiss();
            }
        }
        else {
            DkLogs.complain(this, "Fragment %d must be subclass of `DkFragment`", f.getClass().getName());
        }

        return true;
    }

    /**
     * Be called from our DkActivity and DkFragment.
     */
    public void restoreState(Bundle in) {
        if (in != null) {
            MyBackStackState state = in.getParcelable(KEY_BACKSTACK_STATE);
            stack.restoreStates(state);
        }
    }

    /**
     * Be called from our DkActivity and DkFragment.
     */
    public void saveState(Bundle out) {
        if (out != null) {
            out.putParcelable(KEY_BACKSTACK_STATE, stack.saveStates());
        }
    }
}
