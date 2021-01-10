/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelStoreOwner;

/**
 * Support ViewModel instances which can survived in configuration change.
 */
public interface DkViewModelStore {
    <M extends ViewModel> M getOwnViewModel(Class<M> modelType);

    <M extends ViewModel> M getOwnViewModel(String key, Class<M> modelType);

    <M extends ViewModel> M getHostViewModel(Class<M> modelType);

    <M extends ViewModel> M getHostViewModel(String key, Class<M> modelType);

    <M extends ViewModel> M getAppViewModel(Class<M> modelType);

    <M extends ViewModel> M getAppViewModel(String key, Class<M> modelType);

    <M> M getOwnTopic(Class<M> modelClass, boolean register);

    <M> M getOwnTopic(String topicId, Class<M> modelClass, boolean register);

    <M> M getHostTopic(Class<M> modelClass, boolean register);

    <M> M getHostTopic(String topicId, Class<M> modelClass, boolean register);

    <M> M getAppTopic(Class<M> modelClass, boolean register);

    <M> M getAppTopic(String topicId, Class<M> modelClass, boolean register);

    <M> M getTopic(ViewModelStoreOwner owner, String topicName, Class<M> modelClass, boolean register);
}
