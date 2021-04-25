/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.preference;

public interface DkPreference {
	/**
	 * Which manages preference list.
	 */
	ThePreferenceManager getPreferenceManager();

	/**
	 * Subclass must provide which storage to store preference
	 */
	DkPreferenceStorage storage();

	/**
	 * Subclass must manually define content (item list) of preference
	 */
	void onCreatePreferences(ThePreferenceManager preferenceManager);

	/**
	 * Called when some preference was stored (changed)
	 */
	void onPreferenceChanged(String key);
}
