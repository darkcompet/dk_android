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

class MyFragmentTransactor {
	private final int containerId;
	private final FragmentManager fragmentManager;
	private final FragmentTransaction transaction;
	private final MyBackStack backstack;
	private int addAnim;
	private int removeAnim;
	private int reattachAnim;
	private int detachAnim;

	MyFragmentTransactor(DkFragmentNavigator navigator) {
		this.containerId = navigator.containerId;
		this.fragmentManager = navigator.fm;
		this.transaction = navigator.fm.beginTransaction();
		this.backstack = navigator.backstack;
	}

	public MyFragmentTransactor setAnims(int enterAnim) {
		return setAnims(enterAnim, 0, 0, 0);
	}

	public MyFragmentTransactor setAnims(int enterAnim, int exitAnim) {
		return setAnims(enterAnim, exitAnim, 0, 0);
	}

	/**
	 * @param addAnim animation or animator resId for added action.
	 * @param removeAnim animation or animator resId for removed action.
	 * @param reattachAnim animation or animator resId for reattached action.
	 * @param detachAnim animation or animator resId for detached action.
	 */
	public MyFragmentTransactor setAnims(int addAnim, int removeAnim, int reattachAnim, int detachAnim) {
		this.addAnim = addAnim;
		this.removeAnim = removeAnim;
		this.reattachAnim = reattachAnim;
		this.detachAnim = detachAnim;

		return this;
	}

	/**
	 * Add a fragment if instance of it is not found in backstack.
	 */
	public MyFragmentTransactor addIfAbsent(Class<? extends Fragment> fragClass) {
		return backstack.contains(calcBackStackTag(fragClass)) ? this : performAdd(fragClass, true);
	}

	/**
	 * Bring a fragment (child) to UI top if exist, otherwise add new fragment.
	 */
	public MyFragmentTransactor bringToTopOrAdd(Class<? extends Fragment> fragClass) {
		String tag = calcBackStackTag(fragClass);
		int index = backstack.indexOf(tag);
		if (index < 0) {
			return performAdd(fragClass, true);
		}

		// Find the fragment to re-attach it
		Fragment target = findFragmentByTag(tag);

		return performDetach(target).performReattach(target, index < backstack.size() - 1);
	}

	/**
	 * Init and Add a fragment to last (top UI) of child list.
	 */
	public MyFragmentTransactor add(Class<? extends Fragment> fragClass) {
		return performAdd(fragClass, true);
	}

	/**
	 * Remove top fragment and add a fragment.
	 */
	public MyFragmentTransactor replaceTop(Class<? extends Fragment> fragClass) {
		int lastIndex = backstack.size() - 1;

		if (lastIndex >= 0) {
			performRemoveRange(lastIndex - 1, lastIndex, false);
		}

		return performAdd(fragClass, false);
	}

	/**
	 * Remove all existing fragments and add a fragment.
	 */
	public MyFragmentTransactor replaceAll(Class<? extends Fragment> fragClass) {
		int lastIndex = backstack.size() - 1;

		if (lastIndex >= 0) {
			performRemoveRange(0, lastIndex, false);
		}

		return performAdd(fragClass, false);
	}

	public MyFragmentTransactor back() {
		return back(1);
	}

	/**
	 * Perform back with given `times`. That is, `times` fragments will be removed.
	 */
	public MyFragmentTransactor back(int times) {
		int lastIndex = backstack.size() - 1;
		if (lastIndex < 0) {
			DkLogs.warning(this, "Backstack empty -> could not back");
			return this;
		}
		return performRemoveRange(lastIndex - times + 1, lastIndex, true);
	}

	public MyFragmentTransactor remove(Class<? extends Fragment> fragClass) {
		return remove(calcBackStackTag(fragClass));
	}

	/**
	 * Remove from fragment manager a fragment which has tag equals to given `tag`.
	 */
	public MyFragmentTransactor remove(String tag) {
		int index = backstack.indexOf(tag);
		return performRemoveRange(index, index, true);
	}

	public MyFragmentTransactor removeRange(String fromTag, String toTag) {
		return performRemoveRange(backstack.indexOf(fromTag), backstack.indexOf(toTag), true);
	}

	public MyFragmentTransactor removeAllAfter(Class<? extends Fragment> fragClass) {
		return removeAllAfter(calcBackStackTag(fragClass));
	}

	public MyFragmentTransactor removeAllAfter(Fragment fragment) {
		return removeAllAfter(calcBackStackTag(fragment));
	}

	/**
	 * Remove all fragments which be located after given fragment by tag.
	 */
	public MyFragmentTransactor removeAllAfter(String tag) {
		int index = backstack.indexOf(tag);
		if (index < 0) {
			DkLogs.warning(this, "Could not remove fragments after tag `%s` since not found", tag);
			return this;
		}
		return performRemoveRange(index + 1, backstack.size() - 1, true);
	}

	public MyFragmentTransactor removeAll() {
		// Note: other fragment exists in this stack, for eg,. `SupportLifecycleFragmentImpl`
		for (Fragment child : fragmentManager.getFragments()) {
			transaction.setCustomAnimations(removeAnim, 0);
			transaction.remove(child);
		}
		backstack.clear();

		return this;
	}

	/**
	 * Commit (now) current transaction immediately.
	 * It is strongly recommend call this before host saving UI state.
	 *
	 * We use `transaction.commitNow()` instead of `transaction.commit()` since following reasons:
	 * - Commit all pending transactions
	 * - Do NOT register this commit to framework's backstack
	 *
	 * @return true if no exception occured, otherwise false.
	 */
	public boolean commit() {
		try {
			transaction.commitNow();
			return true;
		}
		catch (Exception e) {
			DkLogs.error(this, e);
			return false;
		}
	}

	/**
	 * Commit (now) current transaction immediately.
	 * Call this only if it is okay for UI state change unexpectedly on the user.
	 *
	 * We use `transaction.commitNow()` instead of `transaction.commit()` since following reasons:
	 * - Commit all pending transactions
	 * - Do NOT register this commit to framework's backstack
	 *
	 * @return true if no exception occured, otherwise false.
	 */
	public boolean commitAllowingStateLoss() {
		try {
			transaction.commitNowAllowingStateLoss();
			return true;
		}
		catch (Exception e) {
			DkLogs.error(this, e);
			return false;
		}
	}

	// region Private

	private MyFragmentTransactor performAdd(Class<? extends Fragment> fragClass, boolean notifyTopInactive) {
		Fragment fragment = instantiate(fragClass);

		if (notifyTopInactive) {
			Fragment lastFragment = findFragmentByIndex(backstack.size() - 1);

			if (lastFragment != null) {
				notifyFragmentInactive(lastFragment);
			}
		}

		MyKeyState key = new MyKeyState(calcBackStackTag(fragment));

		backstack.add(key);

		transaction.setCustomAnimations(addAnim, 0);
		transaction.add(containerId, fragment, key.tag);

		return this;
	}

	private String calcBackStackTag(Object obj) {
		return (obj instanceof Class) ? ((Class) obj).getName() : obj.getClass().getName();
	}

	private MyFragmentTransactor performRemoveRange(int fromIndex, int toIndex, boolean notifyTopActive) {
		final int lastIndex = backstack.size() - 1;

		// Fix range
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (toIndex > lastIndex) {
			toIndex = lastIndex;
		}

		// Skip if invalid range
		if (fromIndex > toIndex) {
			DkLogs.warning(this, "Skip remove range of fragments since invalid range `%d -> %d`", fromIndex, toIndex);
			return this;
		}

		boolean did = false;

		for (int index = toIndex; index >= fromIndex; --index) {
			MyKeyState key = backstack.get(index);
			Fragment fragment = findFragmentByTag(key.tag);

			if (fragment != null) {
				did = true;
				backstack.remove(key.tag);
				transaction.setCustomAnimations(removeAnim, 0);
				transaction.remove(fragment);
			}
		}

		// In case of toIndex equals lastIndex, attach current top fragment and notify it become active
		if (did && toIndex == lastIndex && fromIndex > 0) {
			Fragment head = findFragmentByIndex(fromIndex - 1);

			if (head != null) {
				if (head.isDetached()) {
					performReattach(head, false);
				}
				else if (notifyTopActive) {
					notifyFragmentActive(head);
				}
			}
		}

		return this;
	}

	// This makes fragment view be destroyed but the its state is still managed by fragment manager.
	private MyFragmentTransactor performDetach(Fragment fragment) {
		transaction.setCustomAnimations(detachAnim, 0);
		transaction.detach(fragment);

		return this;
	}

	// Re-attach the fragment which was detached from UI before.
	private MyFragmentTransactor performReattach(Fragment fragment, boolean notifyTopInactive) {
		if (notifyTopInactive) {
			notifyFragmentInactive(backstack.size() - 1);
		}

		backstack.moveToTop(calcBackStackTag(fragment));

		transaction.setCustomAnimations(reattachAnim, 0);
		transaction.attach(fragment);

		return this;
	}

	private Fragment instantiate(Class<? extends Fragment> clazz) {
		try {
			// We don't need check security here -> so not need use `clazz.newInstance()`
			return clazz.getConstructor().newInstance();
		}
		catch (Exception e) {
			throw new RuntimeException("Could not instantiate fragment: " + clazz.getName());
		}
	}

	/**
	 * @param index position in backstack.
	 * @return instance of fragment which was found in fragment manager.
	 */
	@Nullable
	private Fragment findFragmentByIndex(int index) {
		MyKeyState key = backstack.get(index);

		return key == null ? null : findFragmentByTag(key.tag);
	}

	@Nullable
	private Fragment findFragmentByTag(String tag) {
		return fragmentManager.findFragmentByTag(tag);
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
