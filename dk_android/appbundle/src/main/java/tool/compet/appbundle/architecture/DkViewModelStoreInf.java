/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelStoreOwner;

/**
 * Support ViewModel instances which can survived in configuration change.
 */
public interface DkViewModelStoreInf {
    <M extends ViewModel> M getOwnViewModel(Class<M> modelType);

    <M extends ViewModel> M getOwnViewModel(String key, Class<M> modelType);

    <M extends ViewModel> M getHostViewModel(Class<M> modelType);

    <M extends ViewModel> M getHostViewModel(String key, Class<M> modelType);

    <M extends ViewModel> M getAppViewModel(Class<M> modelType);

    <M extends ViewModel> M getAppViewModel(String key, Class<M> modelType);

    <M> M ownTopic(Class<M> modelClass);
    
    <M> M ownTopic(Class<M> modelClass, boolean listen);

    <M> M ownTopic(String topicId, Class<M> modelClass);

    <M> M ownTopic(String topicId, Class<M> modelClass, boolean listen);

    <M> M hostTopic(Class<M> modelClass);
    
    <M> M hostTopic(Class<M> modelClass, boolean listen);

    <M> M hostTopic(String topicId, Class<M> modelType);

    <M> M hostTopic(String topicId, Class<M> modelClass, boolean listen);

    <M> M appTopic(Class<M> modelClass);
    
    <M> M appTopic(Class<M> modelClass, boolean listen);

    <M> M appTopic(String topicId, Class<M> modelClass);

    <M> M appTopic(String topicId, Class<M> modelClass, boolean listen);

    <M> M topic(ViewModelStoreOwner owner, String topicName, Class<M> modelClass, boolean listen);
}
