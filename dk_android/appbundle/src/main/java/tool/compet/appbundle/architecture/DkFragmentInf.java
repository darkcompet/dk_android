/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import androidx.fragment.app.Fragment;

/**
 * Fragment interface, a fragment should Implement this to work with Dk library.
 */
public interface DkFragmentInf {
    /**
     * Obtain fragment itself.
     */
    Fragment getFragment();

    /**
     * ID of layout resource for this fragment, for eg,. `R.layout.main_activity`
     */
    int layoutResourceId();

    /**
     * ID of container inside the layout of this fragment. This id can be used in
     * fragment transaction for other screens, for eg,. `R.id.frag_container`
     */
    int fragmentContainerId();

    /**
     * Each fragment should response #onBackPressed() from host activity.
     *
     * @return true if this fragment will handle this event, otherwise false.
     */
    boolean onBackPressed();

    /**
     * Dismiss (finish) itself.
     *
     * @return true if finish succeed, otherwise falied.
     */
    boolean close();

    /**
     * Specify whether this fragment should be retained instance during configuration changed.
     */
    boolean isRetainInstance();

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
