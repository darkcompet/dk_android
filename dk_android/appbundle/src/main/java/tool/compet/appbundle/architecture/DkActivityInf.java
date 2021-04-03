/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import android.app.Activity;

import tool.compet.appbundle.architecture.navigator.DkFragmentNavigator;

/**
 * Activity interface, an activity should implement this interface to work where Dk library.
 */
public interface DkActivityInf {
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
