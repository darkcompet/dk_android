/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import tool.compet.core.log.DkLogs;

/**
 * Differ with stack of Activities, the important feature of this navigator is,
 * we can re-arrange fragments in stack.
 */
public class DkFragmentNavigator implements MyBackStack.OnStackChangeListener {
    private static final String KEY_BACKSTACK_STATE = "DkFragmentNavigator.KEY_BACKSTACK_STATE";

    public interface Callback {
        void onActive(boolean isResume);

        void onInactive(boolean isPause);
    }

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

    public int childCount() {
        return stack.size();
    }

    /**
     * Notify back-event to last added fragment.
     *
     * @return false indicates the target fragment will handle this event later.
     */
    public boolean onBackPressed() {
        int lastIndex = stack.size() - 1;

        if (lastIndex < 0) {
            // tell owner handle this event
            return false;
        }

        // finish target fragment
        Fragment f = fm.findFragmentByTag(stack.get(lastIndex).tag);

        if (f == null) {
            // tell owner handle this event
            return false;
        }

        if (f instanceof DkFragment) {
            DkFragment view = (DkFragment) f;

            if (!view.onBackPressed()) {
                view.dismiss();
            }
        }
        else {
            DkLogs.complain(this, "Fragment %d must implement #DiFragment to work with %s",
                f.getClass().getName(), getClass().getName());
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
