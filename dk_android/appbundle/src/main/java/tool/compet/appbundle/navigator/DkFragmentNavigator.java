/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.navigator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Set;

import tool.compet.appbundle.DkFragment;
import tool.compet.core.BuildConfig;
import tool.compet.core.DkLogs;
import tool.compet.core.DkStrings;

/**
 * Differ with stack of Activities, the important feature of this navigator is,
 * we can re-arrange fragments inside stack.
 */
public class DkFragmentNavigator {
	public interface Listener {
		void onStackSizeChanged(int size, int oldSize);
	}

	final int containerId;
	final FragmentManager fm;
	final MyTagManager tags;

	Set<Listener> listeners;

	public DkFragmentNavigator(int containerId, FragmentManager fm) {
		this.containerId = containerId;
		this.fm = fm;
		this.tags = new MyTagManager();
	}

	public void registerListener(Listener listener) {
		if (listeners == null) {
			listeners = new ArraySet<>();
		}
		listeners.add(listener);
	}

	public void unregisterListener(Listener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	public TheFragmentTransactor beginTransaction() {
		return new TheFragmentTransactor(this);
	}

	/**
	 * Notify the event to last child fragment.
	 *
	 * @return TRUE iff child fragment not exist or has closed successfully, otherwise FALSE.
	 */
	public boolean handleOnBackPressed() {
		int lastIndex = tags.size() - 1;
		if (lastIndex < 0) {
			return true;
		}

		Fragment lastChild = fm.findFragmentByTag(tags.get(lastIndex).tag);
		if (lastChild == null) {
			return true;
		}

		// Notify the event to last child fragment
		if (lastChild instanceof DkFragment) {
			return ((DkFragment) lastChild).onBackPressed();
		}

		throw new RuntimeException(DkStrings.format("Fragment %s must implement `DkFragment`", lastChild.getClass().getName()));
	}

	/**
	 * @return Number of fragment inside backstack of the view.
	 */
	public int childCount() {
		return tags.size();
	}

	/**
	 * Be called from our DkActivity and DkFragment.
	 */
	public void restoreInstanceState(@Nullable Bundle in) {
		if (in != null) {
			MyTagManager.MyTagsParcelable tags = in.getParcelable("DkFragmentNavigator.tags");
			this.tags.restoreFrom(tags);

			if (BuildConfig.DEBUG) {
				DkLogs.info(this, "Restored tags: " + this.tags.toString());
			}
		}
	}

	/**
	 * Be called from our DkActivity and DkFragment.
	 */
	public void storeInstanceState(@NonNull Bundle out) {
		out.putParcelable("DkFragmentNavigator.tags", tags.generateState());

		if (BuildConfig.DEBUG) {
			DkLogs.info(this, "Stored tags: " + tags.toString());
		}
	}
}
