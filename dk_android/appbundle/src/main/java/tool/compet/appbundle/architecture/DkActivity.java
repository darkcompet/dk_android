/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import android.app.Activity;

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
}
