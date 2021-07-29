/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.preferenceview;

public interface DkPreferenceListener {
	void onPreferenceChanged(String key);

	void notifyDataSetChanged();
}
