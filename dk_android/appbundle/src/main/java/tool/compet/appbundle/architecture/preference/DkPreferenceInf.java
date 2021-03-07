/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture.preference;

import tool.compet.core.storage.DkStorageInf;

public interface DkPreferenceInf {
    /**
     * Which manages preference list.
     */
    ThePreferenceManager getPreferenceManager();

    /**
     * Subclass must provide which storage to store preference
     */
    DkStorageInf storage();

    /**
     * Subclass must manually define content (item list) of preference
     */
    void onCreatePreferences(ThePreferenceManager preferenceManager);

    /**
     * Called when some preference was stored (changed)
     */
    void onPreferenceChanged(String key);
}
