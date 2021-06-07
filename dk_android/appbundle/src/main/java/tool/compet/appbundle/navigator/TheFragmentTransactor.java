/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.navigator;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import tool.compet.appbundle.DkFragment;
import tool.compet.core.DkLogs;

public class TheFragmentTransactor {
	private final int containerId;
	private final FragmentManager fragmentManager;
	private final FragmentTransaction transaction;
	private final MyTagManager originTags;
	private final MyTagManager tags;
	private int enterAnim;
	private int exitAnim;
	private int attachAnim;
	private int detachAnim;

	TheFragmentTransactor(DkFragmentNavigator navigator) {
		this.containerId = navigator.containerId;
		this.fragmentManager = navigator.fm;
		this.transaction = navigator.fm.beginTransaction();
		this.originTags = navigator.tags;
		this.tags = navigator.tags.deepClone();
	}

	public TheFragmentTransactor setAnims(int enterAnim) {
		return setAnims(enterAnim, 0, 0, 0);
	}

	public TheFragmentTransactor setAnims(int enterAnim, int exitAnim) {
		return setAnims(enterAnim, exitAnim, 0, 0);
	}

	/**
	 * @param addAnim animation or animator resId for added action.
	 * @param removeAnim animation or animator resId for removed action.
	 * @param reattachAnim animation or animator resId for reattached action.
	 * @param detachAnim animation or animator resId for detached action.
	 */
	public TheFragmentTransactor setAnims(int addAnim, int removeAnim, int reattachAnim, int detachAnim) {
		this.enterAnim = addAnim;
		this.exitAnim = removeAnim;
		this.attachAnim = reattachAnim;
		this.detachAnim = detachAnim;

		return this;
	}

	/**
	 * Add a fragment to last (top UI) of child list.
	 */
	public TheFragmentTransactor add(Fragment fragment) {
		return performAdd(fragment, calcTag(fragment.getClass()));
	}

	public TheFragmentTransactor add(Fragment fragment, String tag) {
		return performAdd(fragment, tag);
	}

	/**
	 * Add a fragment if instance of it is not found in backstack.
	 */
	public TheFragmentTransactor addIfAbsent(Fragment fragment) {
		return addIfAbsent(fragment, calcTag(fragment.getClass()));
	}

	public TheFragmentTransactor addIfAbsent(Fragment fragment, String tag) {
		if (tags.contains(tag)) {
			DkLogs.info(this, "Ignore add since given fragment was existed");
			return this;
		}
		return performAdd(fragment, tag);
	}

	public TheFragmentTransactor bringToTop(Class<? extends Fragment> fragClass) {
		return bringToTop(calcTag(fragClass));
	}

	/**
	 * Bring a fragment (child) to UI top if exist, otherwise add new fragment.
	 */
	public TheFragmentTransactor bringToTop(String tag) {
		int index = tags.indexOf(tag);
		if (index < 0 || index == tags.size() - 1) {
			return this; // not found or already at top -> ignore
		}

		// Find the fragment to re-attach it
		Fragment target = fragmentManager.findFragmentByTag(tag);
		if (target == null) {
			DkLogs.warning(this, "Not found fragment at tag `%s` from its manager", tag);
			return this;
		}

		return performDetach(target).performAttach(target);
	}

	/**
	 * Remove only top fragment and add a fragment.
	 */
	public TheFragmentTransactor replaceTop(Fragment fragment) {
		return replaceTop(fragment, calcTag(fragment.getClass()));
	}

	public TheFragmentTransactor replaceTop(Fragment fragment, String tag) {
		int lastIndex = tags.size() - 1;
		if (lastIndex >= 0) {
			performRemoveRange(lastIndex - 1, lastIndex);
		}
		return performAdd(fragment, tag);
	}

	/**
	 * Remove all existing fragments and add a fragment.
	 */
	public TheFragmentTransactor replace(Fragment fragment) {
		return replace(fragment, calcTag(fragment.getClass()));
	}

	public TheFragmentTransactor replace(Fragment fragment, String tag) {
		tags.clear();
		tags.add(new MyTag(tag));

		transaction.setCustomAnimations(enterAnim, 0);
		transaction.replace(containerId, fragment, tag);

		return this;
	}

	public TheFragmentTransactor back() {
		return back(1);
	}

	/**
	 * Perform back with given `times`. That is, `times` fragments will be removed.
	 */
	public TheFragmentTransactor back(int times) {
		int lastIndex = tags.size() - 1;
		if (lastIndex < 0) {
			DkLogs.notice(this, "Backstack empty -> ignore backing");
			return this;
		}
		return performRemoveRange(lastIndex - times + 1, lastIndex);
	}

	public TheFragmentTransactor remove(Fragment fragment) {
		return remove(calcTag(fragment.getClass()));
	}

	public TheFragmentTransactor remove(Class fragClass) {
		return remove(calcTag(fragClass));
	}

	/**
	 * Remove from fragment manager a fragment which has tag equals to given `tag`.
	 */
	public TheFragmentTransactor remove(String tag) {
		int index = tags.indexOf(tag);
		if (index < 0) {
			DkLogs.notice(this, "Ignore remove fragment since not found tag `%s`", tag);
			return this;
		}
		return performRemoveRange(index, index);
	}

	public TheFragmentTransactor removeRange(String fromTag, String toTag) {
		return performRemoveRange(tags.indexOf(fromTag), tags.indexOf(toTag));
	}

	public TheFragmentTransactor removeAllAfter(Class<? extends Fragment> fragClass) {
		return removeAllAfter(calcTag(fragClass));
	}

	/**
	 * Remove all fragments which be located after given fragment by tag.
	 */
	public TheFragmentTransactor removeAllAfter(String tag) {
		int index = tags.indexOf(tag);
		if (index < 0) {
			DkLogs.notice(this, "Tag `%s` not found -> skip remove range", tag);
			return this;
		}
		return performRemoveRange(index + 1, tags.size() - 1);
	}

	public TheFragmentTransactor removeAll() {
		return performRemoveRange(0, tags.size() - 1);
	}

	/**
	 * Commit (now) current transaction immediately at main thread (not UI thread).
	 * It is strongly recommend call this before host saving UI state.
	 *
	 * We use `transaction.commitNow()` instead of `transaction.commit()` since following reasons:
	 * - Commit all pending transactions
	 * - Do NOT register this commit to framework's backstack
	 *
	 * @return true if some action was commited and no exception occured. Otherwise false.
	 */
	public boolean commit() {
		try {
			transaction.commitNow(); // call `BackStackRecord.commitNow()`
			applyChangesAndNotifyTargetFragment();
			return true;
		}
		catch (Exception e) {
			DkLogs.error(this, e);
		}
		return false;
	}

	/**
	 * Commit (now) current transaction immediately at main thread (not UI thread).
	 * Call this only if it is okay for UI state change unexpectedly on the user.
	 *
	 * We use `transaction.commitNow()` instead of `transaction.commit()` since following reasons:
	 * - Commit all pending transactions
	 * - Do NOT register this commit to framework's backstack
	 *
	 * @return true if some action was commited and no exception occured. Otherwise false.
	 */
	public boolean commitAllowingStateLoss() {
		try {
			transaction.commitNowAllowingStateLoss();
			applyChangesAndNotifyTargetFragment();
			return true;
		}
		catch (Exception e) {
			DkLogs.error(this, e);
			return false;
		}
	}

	// region Private

	private void applyChangesAndNotifyTargetFragment() {
		//todo notify active and inactive status to target fragment

		originTags.copyFrom(tags);
	}

	private TheFragmentTransactor performAdd(Fragment fragment, String tag) {
		tags.add(new MyTag(tag));

		transaction.setCustomAnimations(enterAnim, 0);
		transaction.add(containerId, fragment, tag);

		return this;
	}

	private TheFragmentTransactor performRemoveRange(int startIndex, int endIndex) {
		final int lastIndex = tags.size() - 1;

		// Fix start, end range
		if (startIndex < 0) {
			startIndex = 0;
		}
		if (endIndex > lastIndex) {
			endIndex = lastIndex;
		}

		// Skip if invalid range
		if (startIndex > endIndex) {
			DkLogs.notice(this, "Invalid range `%d -> %d` -> skip remove range", startIndex, endIndex);
			return this;
		}

		for (int index = endIndex; index >= startIndex; --index) {
			MyTag myTag = tags.get(index);
			Fragment fragment = fragmentManager.findFragmentByTag(myTag.tag);

			if (fragment != null) {
				tags.remove(myTag);

				transaction.setCustomAnimations(exitAnim, 0);
				transaction.remove(fragment);
			}
		}

		return this;
	}

	// This makes fragment view be destroyed but the its state is still managed by fragment manager.
	private TheFragmentTransactor performDetach(Fragment fragment) {
		tags.remove(calcTag(fragment.getClass()));

		transaction.setCustomAnimations(detachAnim, 0);
		transaction.detach(fragment);

		return this;
	}

	// Re-attach the fragment which was detached from UI before.
	private TheFragmentTransactor performAttach(Fragment fragment) {
		tags.add(new MyTag(calcTag(fragment.getClass())));

		transaction.setCustomAnimations(attachAnim, 0);
		transaction.attach(fragment);

		return this;
	}

	private String calcTag(Class fragClass) {
		return fragClass.getName();
	}

	/**
	 * @param index position in backstack.
	 * @return instance of fragment which was found in fragment manager.
	 */
	@Nullable
	private Fragment findFragmentByIndex(int index) {
		MyTag myTag = tags.get(index);
		return myTag == null ? null : fragmentManager.findFragmentByTag(myTag.tag);
	}

	private void notifyFragmentActive(int index) {
		Fragment fragment = findFragmentByIndex(index);

		if (fragment instanceof DkFragment) {
			notifyFragmentActive(fragment);
		}
	}

	private void notifyFragmentActive(Fragment fragment) {
		if (fragment.isAdded() && fragment.isResumed()) {
			((DkFragment) fragment).onActive(false);
		}
	}

	private void notifyFragmentInactive(int index) {
		Fragment fragment = findFragmentByIndex(index);

		if (fragment != null) {
			notifyFragmentInactive(fragment);
		}
	}

	private void notifyFragmentInactive(Fragment fragment) {
		if (fragment.isAdded() && fragment.isResumed()) {
			((DkFragment) fragment).onInactive(false);
		}
	}

	// endregion Private
}
