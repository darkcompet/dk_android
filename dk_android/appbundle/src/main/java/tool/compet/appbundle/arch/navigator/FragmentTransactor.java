/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.appbundle.arch.navigator;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import tool.compet.appbundle.arch.DkFragment;

public class FragmentTransactor {
	private final int containerId;
	private final FragmentManager fm;
	private final FragmentTransaction ft;
	private final BackStack stack;
	private int addAnim;
	private int removeAnim;
	private int reattachAnim;
	private int detachAnim;

	FragmentTransactor(DkFragmentNavigator navigator) {
		this.containerId = navigator.containerId;
		this.fm = navigator.fm;
		this.ft = navigator.fm.beginTransaction();
		this.stack = navigator.stack;
	}

	public FragmentTransactor setAnims(int add, int remove) {
		addAnim = add;
		removeAnim = remove;

		return this;
	}

	/**
	 * @param add animation or animator resId for added action.
	 * @param remove animation or animator resId for removed action.
	 * @param reattach animation or animator resId for reattached action.
	 * @param detach animation or animator resId for detached action.
	 */
	public FragmentTransactor setAnims(int add, int remove, int reattach, int detach) {
		addAnim = add;
		removeAnim = remove;
		reattachAnim = reattach;
		detachAnim = detach;

		return this;
	}

	public FragmentTransactor addIfAbsent(Class<? extends DkFragment> fclazz) {
		return stack.contains(fclazz.getName()) ? this :
			performAdd(instantiate(fclazz), true);
	}

	public FragmentTransactor add(Class<? extends DkFragment> fclazz) {
		return performAdd(instantiate(fclazz), true);
	}

	public FragmentTransactor addIfAbsent(DkFragment f) {
		return stack.contains(f.getClass().getName()) ? this :
			performAdd(f.getFragment(), true);
	}

	public FragmentTransactor add(DkFragment f) {
		return performAdd(f.getFragment(), true);
	}

	/**
	 * Detach top fragment before add the fragment. Note that, detach action doesn't
	 * change backstack structure.
	 */
	public FragmentTransactor detachTopThenAdd(DkFragment f) {
		int lastIndex = stack.size() - 1;

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
	public FragmentTransactor detachAllThenAdd(DkFragment f) {
		for (int i = stack.size(); i > 0; --i) {
			Fragment fi = findFragmentByIndex(i);

			if (fi != null) {
				performDetach(fi);
			}
		}

		return performAdd(f.getFragment(), false);
	}

	/**
	 * Remove only top fragment and add given fragment.
	 */
	public FragmentTransactor replaceTop(DkFragment f) {
		int lastIndex = stack.size() - 1;

		if (lastIndex >= 0) {
			performRemoveRange(lastIndex - 1, lastIndex, false);
		}

		return performAdd(f.getFragment(), false);
	}

	public FragmentTransactor replaceAll(Class<? extends DkFragment> fClass) {
		int lastIndex = stack.size() - 1;

		if (lastIndex >= 0) {
			performRemoveRange(0, lastIndex, false);
		}

		return performAdd(instantiate(fClass), false);
	}

	/**
	 * Remove all existing fragments and add given fragment.
	 */
	public FragmentTransactor replaceAll(DkFragment f) {
		int lastIndex = stack.size() - 1;

		if (lastIndex >= 0) {
			performRemoveRange(0, lastIndex, false);
		}

		return performAdd(f.getFragment(), false);
	}

	public FragmentTransactor back() {
		int lastIndex = stack.size() - 1;

		return lastIndex < 0 ? this :
			performRemoveRange(lastIndex, lastIndex, true);
	}

	public FragmentTransactor back(int times) {
		int lastIndex = stack.size() - 1;

		return lastIndex < 0 ? this :
			performRemoveRange(lastIndex - times + 1, lastIndex, true);
	}

	public FragmentTransactor remove(Class<? extends DkFragment> fClass) {
		return remove(fClass.getName());
	}

	public FragmentTransactor remove(DkFragment f) {
		return remove(f.getClass().getName());
	}

	public FragmentTransactor remove(String tag) {
		int index = stack.indexOf(tag);

		return index < 0 ? this :
			performRemoveRange(index, index, true);
	}

	public FragmentTransactor removeRange(String fromTag, String toTag) {
		return performRemoveRange(stack.indexOf(fromTag), stack.indexOf(toTag), true);
	}

	public FragmentTransactor removeAllAfter(Class<? extends DkFragment> fClass) {
		return removeAllAfter(fClass.getName());
	}

	public FragmentTransactor removeAllAfter(DkFragment f) {
		return removeAllAfter(f.getClass().getName());
	}

	public FragmentTransactor removeAllAfter(String tag) {
		int index = stack.indexOf(tag);

		return index < 0 ? this :
			performRemoveRange(index + 1, stack.size() - 1, true);
	}

	/**
	 * Bring the child (create new if not exist) to Top.
	 */
	public FragmentTransactor bringToTopOrAdd(Class<? extends DkFragment> fClass) {
		String tag = fClass.getName();
		Fragment f =  findFragmentByTag(tag);

		if (f == null) {
			f = instantiate(fClass);
		}

		int index = stack.indexOf(tag);

		return index < 0 ? performAdd(f, true) :
			performDetach(f).performReattach(f, index < stack.size() - 1);
	}

	private FragmentTransactor performAdd(Fragment f, boolean notifyTopInactive) {
		if (notifyTopInactive) {
			Fragment last = findFragmentByIndex(stack.size() - 1);

			if (last != null) {
				notifyFragmentInactive(last);
			}
		}

		KeyState key = new KeyState(f.getClass().getName());

		stack.add(key);

		ft.setCustomAnimations(addAnim, 0);
		ft.add(containerId, f, key.tag);

		return this;
	}

	private FragmentTransactor performRemoveRange(int fromIndex, int toIndex, boolean notifyTopActive) {
		final int lastIndex = stack.size() - 1;

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
			KeyState key = stack.get(i);
			Fragment f = findFragmentByTag(key.tag);

			if (f != null) {
				did = true;
				stack.remove(key.tag);
				ft.setCustomAnimations(removeAnim, 0);
				ft.remove(f);
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

	private FragmentTransactor performDetach(Fragment f) {
		ft.setCustomAnimations(detachAnim, 0);
		ft.detach(f);

		return this;
	}

	private FragmentTransactor performReattach(Fragment f, boolean notifyTopInactive) {
		if (notifyTopInactive) {
			notifyFragmentInactive(stack.size() - 1);
		}

		stack.moveToTop(f.getClass().getName());

		ft.setCustomAnimations(reattachAnim, 0);
		ft.attach(f);

		return this;
	}

	public void commit() {
		ft.commitNow();
	}

	private Fragment instantiate(Class<? extends DkFragment> clazz) {
		try {
			// we don't need security check here so don't use clazz.newInstance()
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
		KeyState key = stack.get(index);

		return key == null ? null : findFragmentByTag(key.tag);
	}

	@Nullable
	private Fragment findFragmentByTag(String tag) {
		return fm.findFragmentByTag(tag);
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
