/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.preference;

public interface MyPreferenceListener {
	void onPreferenceChanged(String key);

	void notifyDataSetChanged();
}
