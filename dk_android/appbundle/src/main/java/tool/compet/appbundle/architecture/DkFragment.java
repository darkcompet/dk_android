/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import androidx.fragment.app.Fragment;

/**
 * Fragment interface for Dk library. Implements this to work with Dk library.
 */
public interface DkFragment {
    /**
     * Obtain fragment itself.
     */
    Fragment getFragment();

    /**
     * Specify id of layout resource for this fragment.
     */
    int layoutResourceId();

    /**
     * Specify id of container inside the layout of this fragment. This id can be used in
     * fragment transaction for other screens.
     */
    int fragmentContainerId();

    /**
     * Each fragment should response #onBackPressed() from host activity.
     *
     * @return true if this fragment will handle this event, otherwise false.
     */
    boolean onBackPressed();

    /**
     * Dismiss itself, like #Activity.finish().
     */
    void dismiss();

    /**
     * Specify whether this fragment should be retained instance during configuration changed.
     */
    boolean isRetainInstance();

    /**
     * Be called from other fragments or itself.
     *
     * @return children fragment navigator that the fragment owns
     */
    DkFragmentNavigator getChildNavigator();

    /**
     * Be called from other fragments or itself.
     *
     * @return parent fragment navigator that the fragment is owned
     */
    DkFragmentNavigator getParentNavigator();

    /**
     * Indicates the fragment is resumsed or come to front.
     *
     * @param isResume true if this fragment is in resume state, otherwise it is on front.
     */
    void onActive(boolean isResume);

    /**
     * Indicates the fragment is paused or go to behind.
     *
     * @param isPause true if this fragment is in pause state, otherwise it is in behind.
     */
    void onInactive(boolean isPause);
}
