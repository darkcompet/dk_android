/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import android.app.Activity;

import tool.compet.navigation.DkFragmentNavigator;

/**
 * Activity interface, an activity should implement this interface to work where Dk library.
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
	 * Dismiss (finish) itself.
	 *
	 * @return true if finish succeed, otherwise falied.
	 */
	boolean close();
}
