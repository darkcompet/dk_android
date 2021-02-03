/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.navigator;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import tool.compet.appbundle.architecture.DkFragment;
import tool.compet.core.log.DkLogs;

public class MyFragmentTransactor {
    private final int containerId;
    private final FragmentManager fm;
    private final FragmentTransaction ft;
    private final MyBackStack stack;
    private int addAnim;
    private int removeAnim;
    private int reattachAnim;
    private int detachAnim;

    MyFragmentTransactor(DkFragmentNavigator navigator) {
        this.containerId = navigator.containerId;
        this.fm = navigator.fm;
        this.ft = navigator.fm.beginTransaction();
        this.stack = navigator.stack;
    }

    public MyFragmentTransactor setAnims(int add, int remove) {
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
    public MyFragmentTransactor setAnims(int add, int remove, int reattach, int detach) {
        addAnim = add;
        removeAnim = remove;
        reattachAnim = reattach;
        detachAnim = detach;

        return this;
    }

    public MyFragmentTransactor addIfAbsent(Class<? extends DkFragment> fclazz) {
        return stack.contains(fclazz.getName()) ? this :
            performAdd(instantiate(fclazz), true);
    }

    public MyFragmentTransactor add(Class<? extends DkFragment> fclazz) {
        return performAdd(instantiate(fclazz), true);
    }

    public MyFragmentTransactor addIfAbsent(DkFragment f) {
        return stack.contains(f.getClass().getName()) ? this :
            performAdd(f.getFragment(), true);
    }

    public MyFragmentTransactor add(DkFragment f) {
        return performAdd(f.getFragment(), true);
    }

    /**
     * Detach top fragment before add the fragment. Note that, detach action doesn't
     * change backstack structure.
     */
    public MyFragmentTransactor detachTopThenAdd(DkFragment f) {
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
    public MyFragmentTransactor detachAllThenAdd(DkFragment f) {
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
    public MyFragmentTransactor replaceTop(DkFragment f) {
        int lastIndex = stack.size() - 1;

        if (lastIndex >= 0) {
            performRemoveRange(lastIndex - 1, lastIndex, false);
        }

        return performAdd(f.getFragment(), false);
    }

    public MyFragmentTransactor replaceAll(Class<? extends DkFragment> fClass) {
        int lastIndex = stack.size() - 1;

        if (lastIndex >= 0) {
            performRemoveRange(0, lastIndex, false);
        }

        return performAdd(instantiate(fClass), false);
    }

    /**
     * Remove all existing fragments and add given fragment.
     */
    public MyFragmentTransactor replaceAll(DkFragment f) {
        int lastIndex = stack.size() - 1;

        if (lastIndex >= 0) {
            performRemoveRange(0, lastIndex, false);
        }

        return performAdd(f.getFragment(), false);
    }

    public MyFragmentTransactor back() {
        int lastIndex = stack.size() - 1;

        return lastIndex < 0 ? this : performRemoveRange(lastIndex, lastIndex, true);
    }

    public MyFragmentTransactor back(int times) {
        int lastIndex = stack.size() - 1;

        return lastIndex < 0 ? this : performRemoveRange(lastIndex - times + 1, lastIndex, true);
    }

    public MyFragmentTransactor remove(Class<? extends DkFragment> fClass) {
        return remove(fClass.getName());
    }

    public MyFragmentTransactor remove(DkFragment f) {
        return remove(f.getClass().getName());
    }

    public MyFragmentTransactor remove(String tag) {
        int index = stack.indexOf(tag);

        return index < 0 ? this : performRemoveRange(index, index, true);
    }

    public MyFragmentTransactor removeRange(String fromTag, String toTag) {
        return performRemoveRange(stack.indexOf(fromTag), stack.indexOf(toTag), true);
    }

    public MyFragmentTransactor removeAllAfter(Class<? extends DkFragment> fClass) {
        return removeAllAfter(fClass.getName());
    }

    public MyFragmentTransactor removeAllAfter(DkFragment f) {
        return removeAllAfter(f.getClass().getName());
    }

    public MyFragmentTransactor removeAllAfter(String tag) {
        int index = stack.indexOf(tag);

        return index < 0 ? this : performRemoveRange(index + 1, stack.size() - 1, true);
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

        int index = stack.indexOf(tag);

        return index < 0 ?
            performAdd(f, true) :
            performDetach(f).performReattach(f, index < stack.size() - 1);
    }

    private MyFragmentTransactor performAdd(Fragment f, boolean notifyTopInactive) {
        if (notifyTopInactive) {
            Fragment last = findFragmentByIndex(stack.size() - 1);

            if (last != null) {
                notifyFragmentInactive(last);
            }
        }

        MyKeyState key = new MyKeyState(f.getClass().getName());

        stack.add(key);

        ft.setCustomAnimations(addAnim, 0);
        ft.add(containerId, f, key.tag);

        return this;
    }

    private MyFragmentTransactor performRemoveRange(int fromIndex, int toIndex, boolean notifyTopActive) {
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
            MyKeyState key = stack.get(i);
            Fragment f = findFragmentByTag(key.tag);

            if (f != null) {
                DkLogs.debug(this, "remove fragment: " + f);
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
                    DkLogs.debug(this, "re-attach fragment: " + head);
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
        ft.setCustomAnimations(detachAnim, 0);
        ft.detach(f);

        return this;
    }

    private MyFragmentTransactor performReattach(Fragment f, boolean notifyTopInactive) {
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
        MyKeyState key = stack.get(index);

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
