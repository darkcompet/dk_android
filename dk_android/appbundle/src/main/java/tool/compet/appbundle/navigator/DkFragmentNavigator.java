/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.navigator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import tool.compet.appbundle.DkFragment;
import tool.compet.core.DkStrings;

/**
 * Differ with stack of Activities, the important feature of this navigator is,
 * we can re-arrange fragments inside stack.
 */
public class DkFragmentNavigator implements MyBackStack.OnStackChangeListener {
	public interface Callback {
		void onActive(boolean isResume);

		void onInactive(boolean isPause);
	}

	private static final String KEY_BACKSTACK_STATE = "DkFragmentNavigator.KEY_BACKSTACK_STATE";

	final int containerId;
	final FragmentManager fm;
	final MyBackStack backstack;

	private final Callback callback;

	public DkFragmentNavigator(int containerId, FragmentManager fm, @NonNull Callback cb) {
		this.containerId = containerId;
		this.fm = fm;
		this.callback = cb;
		this.backstack = new MyBackStack(this);
	}

	@Override
	public void onStackSizeChanged(int size, int oldSize) {
		if (size == 0) {
			callback.onActive(false);
		}
		else if (size == 1 && oldSize == 0) {
			callback.onInactive(false);
		}
	}

	public MyFragmentTransactor beginTransaction() {
		return new MyFragmentTransactor(this);
	}

	/**
	 * Notify the event to last child fragment
	 *
	 * @return true if child fragment not exist or has dismissed successfully, otherwise false.
	 */
	public boolean handleOnBackPressed() {
		int lastIndex = backstack.size() - 1;
		if (lastIndex < 0) {
			return true;
		}

		Fragment lastChild = fm.findFragmentByTag(backstack.get(lastIndex).tag);
		if (lastChild == null) {
			return true;
		}

		// Notify the event to last child fragment
		if (lastChild instanceof DkFragment) {
			return ((DkFragment) lastChild).onBackPressed();
		}

		throw new RuntimeException(DkStrings.format("Fragment %s must be subclass of `DkFragment`", lastChild.getClass().getName()));
	}

	/**
	 * @return NUmber of fragment inside backstack of the view.
	 */
	public int childCount() {
		return backstack.size();
	}

	/**
	 * Be called from our DkActivity and DkFragment.
	 */
	public void restoreState(Bundle in) {
		if (in != null) {
			MyBackStackState state = in.getParcelable(KEY_BACKSTACK_STATE);
			backstack.restoreStates(state);
		}
	}

	/**
	 * Be called from our DkActivity and DkFragment.
	 */
	public void saveState(Bundle out) {
		if (out != null) {
			out.putParcelable(KEY_BACKSTACK_STATE, backstack.saveStates());
		}
	}
}
