/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import tool.compet.core.DkLogs;

public class MyFragmentTransactor {
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
	 * @param addAnim      animation or animator resId for added action.
	 * @param removeAnim   animation or animator resId for removed action.
	 * @param reattachAnim animation or animator resId for reattached action.
	 * @param detachAnim   animation or animator resId for detached action.
	 */
	public MyFragmentTransactor setAnims(int addAnim, int removeAnim, int reattachAnim, int detachAnim) {
		this.addAnim = addAnim;
		this.removeAnim = removeAnim;
		this.reattachAnim = reattachAnim;
		this.detachAnim = detachAnim;
		return this;
	}

	public MyFragmentTransactor addIfAbsent(Class<? extends DkFragment> fclazz) {
		return backstack.contains(fclazz.getName()) ? this :
			performAdd(instantiate(fclazz), true);
	}

	public MyFragmentTransactor add(Class<? extends DkFragment> fclazz) {
		return performAdd(instantiate(fclazz), true);
	}

	public MyFragmentTransactor addIfAbsent(DkFragment f) {
		return backstack.contains(calcBackStackTag(f)) ? this : performAdd(f.getFragment(), true);
	}

	public MyFragmentTransactor add(DkFragment f) {
		return performAdd(f.getFragment(), true);
	}

	/**
	 * Detach top fragment before add the fragment. Note that, detach action doesn't
	 * change backstack structure.
	 */
	public MyFragmentTransactor detachTopThenAdd(DkFragment f) {
		int lastIndex = backstack.size() - 1;

		if (lastIndex >= 0) {
			Fragment last = findFragmentByIndex(lastIndex);

			if (last != null) {
				performDetach(last);
			}
		}

		return performAdd(f.getFragment(), false);
	}

	/**
	 * Detach all fragments before add the fragment. Note that, detach action doesn't
	 * change backstack structure.
	 */
	public MyFragmentTransactor detachAllThenAdd(DkFragment f) {
		for (int index = backstack.size(); index > 0; --index) {
			Fragment targetFrag = findFragmentByIndex(index);

			if (targetFrag != null) {
				performDetach(targetFrag);
			}
		}

		return performAdd(f.getFragment(), false);
	}

	/**
	 * Remove only top fragment and add given fragment.
	 */
	public MyFragmentTransactor replaceTop(DkFragment f) {
		int lastIndex = backstack.size() - 1;

		if (lastIndex >= 0) {
			performRemoveRange(lastIndex - 1, lastIndex, false);
		}

		return performAdd(f.getFragment(), false);
	}

	public MyFragmentTransactor replaceAll(Class<? extends DkFragment> fClass) {
		int lastIndex = backstack.size() - 1;

		if (lastIndex >= 0) {
			performRemoveRange(0, lastIndex, false);
		}

		return performAdd(instantiate(fClass), false);
	}

	/**
	 * Remove all existing fragments and add given fragment.
	 */
	public MyFragmentTransactor replaceAll(DkFragment f) {
		int lastIndex = backstack.size() - 1;

		if (lastIndex >= 0) {
			performRemoveRange(0, lastIndex, false);
		}

		return performAdd(f.getFragment(), false);
	}

	public MyFragmentTransactor back() {
		int lastIndex = backstack.size() - 1;

		return lastIndex < 0 ? this : performRemoveRange(lastIndex, lastIndex, true);
	}

	public MyFragmentTransactor back(int times) {
		int lastIndex = backstack.size() - 1;

		return lastIndex < 0 ? this : performRemoveRange(lastIndex - times + 1, lastIndex, true);
	}

	public MyFragmentTransactor remove(Class<? extends DkFragment> fClass) {
		return remove(fClass.getName());
	}

	public MyFragmentTransactor remove(DkFragment f) {
		return remove(calcBackStackTag(f));
	}

	public MyFragmentTransactor remove(String tag) {
		int index = backstack.indexOf(tag);

		return index < 0 ? this : performRemoveRange(index, index, true);
	}

	public MyFragmentTransactor removeRange(String fromTag, String toTag) {
		return performRemoveRange(backstack.indexOf(fromTag), backstack.indexOf(toTag), true);
	}

	public MyFragmentTransactor removeAllAfter(Class<? extends DkFragment> fClass) {
		return removeAllAfter(fClass.getName());
	}

	public MyFragmentTransactor removeAllAfter(DkFragment f) {
		return removeAllAfter(calcBackStackTag(f));
	}

	public MyFragmentTransactor removeAllAfter(String tag) {
		int index = backstack.indexOf(tag);

		return index < 0 ? this : performRemoveRange(index + 1, backstack.size() - 1, true);
	}

	public MyFragmentTransactor removeAll() {
		// humh, other fragment exists in this stack: SupportLifecycleFragmentImpl
		for (Fragment child : fragmentManager.getFragments()) {
			transaction.setCustomAnimations(removeAnim, 0);
			transaction.remove(child);
		}

		backstack.clear();

		return this;
	}

	/**
	 * Bring the child (create new if not exist) to Top.
	 */
	public MyFragmentTransactor bringToTopOrAdd(Class<? extends DkFragment> fClass) {
		String tag = fClass.getName();
		Fragment f = findFragmentByTag(tag);

		if (f == null) {
			f = instantiate(fClass);
		}

		int index = backstack.indexOf(tag);

		return index < 0 ?
			performAdd(f, true) :
			performDetach(f).performReattach(f, index < backstack.size() - 1);
	}

	private MyFragmentTransactor performAdd(Fragment f, boolean notifyTopInactive) {
		if (notifyTopInactive) {
			Fragment last = findFragmentByIndex(backstack.size() - 1);

			if (last != null) {
				notifyFragmentInactive(last);
			}
		}

		MyKeyState key = new MyKeyState(calcBackStackTag(f));

		backstack.add(key);

		transaction.setCustomAnimations(addAnim, 0);
		transaction.add(containerId, f, key.tag);

		return this;
	}

	private String calcBackStackTag(Object fragment) {
		return fragment.getClass().getName();
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
			return this;
		}

		boolean did = false;

		for (int i = toIndex; i >= fromIndex; --i) {
			MyKeyState key = backstack.get(i);
			Fragment f = findFragmentByTag(key.tag);

			if (f != null) {
				did = true;
				backstack.remove(key.tag);
				transaction.setCustomAnimations(removeAnim, 0);
				transaction.remove(f);
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

	private MyFragmentTransactor performDetach(Fragment f) {
		transaction.setCustomAnimations(detachAnim, 0);
		transaction.detach(f);

		return this;
	}

	private MyFragmentTransactor performReattach(Fragment f, boolean notifyTopInactive) {
		if (notifyTopInactive) {
			notifyFragmentInactive(backstack.size() - 1);
		}

		backstack.moveToTop(calcBackStackTag(f));

		transaction.setCustomAnimations(reattachAnim, 0);
		transaction.attach(f);

		return this;
	}

	/**
	 * Commit now current transaction.
	 * Note, we use `transaction.commitNow()` instead of `transaction.commit()`
	 * since following reasons:
	 * - commit all pending transactions
	 * - not register this commit to framework's backstack
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

	private Fragment instantiate(Class<? extends DkFragment> clazz) {
		try {
			// we don't need check security here -> so not need use clazz.newInstance()
			return clazz.getConstructor().newInstance().getFragment();
		}
		catch (Exception e) {
			throw new RuntimeException("Could not instantiate fragment: " + clazz.getName());
		}
	}

	/**
	 * @param index position in backstack.
	 * @return instance of DkIFragment which was found in FM.
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
		Fragment f = findFragmentByIndex(index);

		if (f != null) {
			notifyFragmentActive(f);
		}
	}

	private void notifyFragmentActive(Fragment f) {
		if (f.isAdded() && f.isResumed()) {
			((DkFragment) f).onActive(false);
		}
	}

	private void notifyFragmentInactive(int index) {
		Fragment f = findFragmentByIndex(index);

		if (f != null) {
			notifyFragmentInactive(f);
		}
	}

	private void notifyFragmentInactive(Fragment f) {
		if (f.isAdded() && f.isResumed()) {
			((DkFragment) f).onInactive(false);
		}
	}
}
