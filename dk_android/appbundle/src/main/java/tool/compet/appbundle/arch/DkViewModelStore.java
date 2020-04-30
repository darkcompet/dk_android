/*
 * Copyright (c) 2019 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.appbundle.arch;

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
