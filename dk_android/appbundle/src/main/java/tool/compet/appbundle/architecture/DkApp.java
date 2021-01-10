/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import androidx.lifecycle.ViewModelStoreOwner;

/**
 * Application (single or multidex) should extend this to work with Dk libraries.
 */
public interface DkApp extends ViewModelStoreOwner {
}
