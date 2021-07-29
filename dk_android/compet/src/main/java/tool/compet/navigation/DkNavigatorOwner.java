/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.navigation;

/**
 * A view (activity, fragment...) which supports (provides) navigator
 * should implement this class.
 */
public interface DkNavigatorOwner {
	DkFragmentNavigator getChildNavigator();
	DkFragmentNavigator getParentNavigator();
}
