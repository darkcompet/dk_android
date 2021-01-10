/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import androidx.lifecycle.ViewModel;

public interface DkCompactView {
    <M extends ViewModel> M getOwnViewModel(Class<M> modelType);
}
