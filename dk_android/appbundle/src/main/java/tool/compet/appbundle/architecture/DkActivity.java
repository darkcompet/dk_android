/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import android.app.Activity;

import tool.compet.appbundle.architecture.navigator.DkFragmentNavigator;

/**
 * Activity interface for Dk library. If a activity implements this interface,
 * then it can work where Dk library supports.
 */
public interface DkActivity {
    /**
     * Obtain itself.
     */
    Activity getActivity();

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
     * Be called from other fragments or itself.
     *
     * @return children fragment navigator that the fragment owns
     */
    DkFragmentNavigator getChildNavigator();

    /**
     * Dismiss itself.
     */
    void dismiss();

    /**
     * Indicates the activity is resumsed or come to front.
     *
     * @param isResume true if this activity is in resume state, otherwise it is on front.
     */
    void onActive(boolean isResume);

    /**
     * Indicates the activity is paused or go to behind.
     *
     * @param isPause true if this activity is in pause state, otherwise it is in behind.
     */
    void onInactive(boolean isPause);
}
