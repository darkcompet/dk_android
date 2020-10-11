/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
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

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tool.compet.appbundle.arch.navigator.DkFragmentNavigator;
import tool.compet.appbundle.arch.scopedTopic.TopicProvider;
import tool.compet.appbundle.binder.DkBinder;
import tool.compet.appbundle.floatingbar.DkSnackbar;
import tool.compet.appbundle.floatingbar.DkToastbar;
import tool.compet.appbundle.BuildConfig;
import tool.compet.core.util.DkLogs;
import tool.compet.core.util.Dks;

/**
 * All fragments should subclass this to work with support of Dk library as possible.
 *
 * <ul>
 *    <li> In default, this use DkBinder to bind your views at #onCreateView(), you should provide
 *    layout resource by implement method {@link DkSimpleFragment#layoutResourceId()}.
 *    <li> This is simple, pure and does not serious implement own logic so you can freely customize as you want.
 *    <li> This supports Navigator which help you manage fragment transaction easier than use
 *    BackStack of Android in some cases.
 * </ul>
 */
public abstract class DkSimpleFragment extends Fragment implements DkFragment,
   DkViewModelStore, DkFragmentNavigator.Callback {

   // Read only fields
   public FragmentActivity host;
   public Context context;
   public ViewGroup layout;

   private DkFragmentNavigator navigator;

   /**
    * Must provide id of fragent container via {@link DkSimpleFragment#fragmentContainerId()}.
    */
   @Override
   public DkFragmentNavigator getChildNavigator() {
      if (navigator == null) {
         int containerId = fragmentContainerId();

         if (containerId <= 0) {
            DkLogs.complain(this, "Invalid fragmentContainerId: " + containerId);
         }

         this.navigator = new DkFragmentNavigator(containerId, getChildFragmentManager(), this);
      }

      return navigator;
   }

   @Override
   public DkFragmentNavigator getParentNavigator() {
      Fragment parent = getParentFragment();
      DkFragmentNavigator owner = null;

      if (parent == null) {
         if (host instanceof DkSimpleActivity) {
            owner = ((DkSimpleActivity) host).getChildNavigator();
         }
      }
      else if (parent instanceof DkFragment) {
         owner = ((DkFragment) parent).getChildNavigator();
      }

      if (owner == null) {
         DkLogs.complain(this, "Must have a parent navigator own the fragment: %s",
            getClass().getName());
      }

      return owner;
   }

   @Override
   public void onAttach(@NonNull Context context) {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, "onAttach (context)");
      }

      this.context = context;

      super.onAttach(context);
   }

   @Override
   @SuppressWarnings("deprecation")
   public void onAttach(@NonNull Activity activity) {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, "onAttach (activity)");
      }

      this.host = (FragmentActivity) activity;

      super.onAttach(activity);
   }

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, "onCreate");
      }
      super.setRetainInstance(isRetainInstance());
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, "onCreateView");
      }

      int layoutId = layoutResourceId();

      if (layoutId <= 0) {
         DkLogs.complain(this, "Invalid layoutId: %d", layoutId);
      }

      layout = (ViewGroup) inflater.inflate(layoutResourceId(), container, false);
      DkBinder.bindViews(this, layout);

      return layout;
   }

   @Override
   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, "onViewCreated");
      }
      super.onViewCreated(view, savedInstanceState);
   }

   @Override
   public void onActivityCreated(@Nullable Bundle savedInstanceState) {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, "onActivityCreated");
      }
      if (navigator != null) {
         navigator.restoreState(savedInstanceState);
      }
      super.onActivityCreated(savedInstanceState);
   }

   @Override
   public void onStart() {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, "onStart");
      }
      super.onStart();
   }

   @Override
   public void onResume() {
      onActive(true);
      super.onResume();
   }

   @Override
   public void onActive(boolean isResume) {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, isResume ? "onResume" : "onFront");
      }
   }

   @Override
   public void onPause() {
      onInactive(true);
      super.onPause();
   }

   @Override
   public void onInactive(boolean isPause) {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, isPause ? "onPause" : "onBehind");
      }
   }

   @Override
   public void onStop() {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, "onStop");
      }
      super.onStop();
   }

   @Override
   public void onDestroyView() {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, "onDestroyView");
      }
      super.onDestroyView();
   }

   @Override
   public void onDestroy() {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, "onDestroy");
      }
      super.onDestroy();
   }

   @Override
   public void onDetach() {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, "onDetach");
      }

      this.host = null;
      this.context = null;

      super.onDetach();
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, "onActivityResult");
      }
      super.onActivityResult(requestCode, resultCode, data);
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, "onActivityResult");
      }
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
   }

   @Override
   public void onLowMemory() {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, "onLowMemory");
      }
      super.onLowMemory();
   }

   @Override
   public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, "onViewStateRestored");
      }
      super.onViewStateRestored(savedInstanceState);
   }

   @Override
   public void onSaveInstanceState(@NonNull Bundle outState) {
      if (BuildConfig.DEBUG) {
         DkLogs.info(this, "onSaveInstanceState");
      }
      if (navigator != null) {
         navigator.saveState(outState);
      }
      super.onSaveInstanceState(outState);
   }

   @Override
   public Fragment getFragment() {
      return this;
   }

   /**
    * This will try to send back-event to children first. If has no child here,
    * then #dismiss() will be called in parent navigator.
    */
   @Override
   public boolean onBackPressed() {
      return (navigator != null && navigator.childCount() != 0) && navigator.onBackPressed();
   }

   /**
    * This will actual dismiss the view even though children exists.
    */
   @Override
   public void dismiss() {
      getParentNavigator()
         .beginTransaction()
         .remove(this)
         .commit();
   }

   /**
    * Get or Create new ViewModel instance which be owned by this Fragment.
    */
   @Override
   public <M extends ViewModel> M getOwnViewModel(Class<M> modelType) {
      return new ViewModelProvider(this).get(modelType);
   }

   /**
    * Get or Create new ViewModel instance which be owned by this Fragment.
    */
   @Override
   public <M extends ViewModel> M getOwnViewModel(String key, Class<M> modelType) {
      return new ViewModelProvider(this).get(key, modelType);
   }

   /**
    * Get or Create new ViewModel instance which be owned by Activity which this contains this Fragment.
    */
   @Override
   public <M extends ViewModel> M getHostViewModel(Class<M> modelType) {
      return new ViewModelProvider(host).get(modelType);
   }

   /**
    * Get or Create new ViewModel instance which be owned by Activity which this contains this Fragment.
    */
   @Override
   public <M extends ViewModel> M getHostViewModel(String key, Class<M> modelType) {
      return new ViewModelProvider(host).get(key, modelType);
   }

   @Override
   public <M extends ViewModel> M getAppViewModel(Class<M> modelType) {
      return getAppViewModel(modelType.getName(), modelType);
   }

   @Override
   public <M extends ViewModel> M getAppViewModel(String key, Class<M> modelType) {
      Application app = host.getApplication();

      if (app instanceof DkApp) {
         return new ViewModelProvider((DkApp) app).get(key, modelType);
      }

      throw new RuntimeException("App must have type of #DkApp");
   }

   /**
    * Join and Get shared ViewModel instance which be owned by this Fragment.
    *
    * @param register true if you want this View register the topic, otherwise just preview.
    */
   @Override
   public <M> M getOwnTopic(Class<M> modelType, boolean register) {
      return getTopic(this, modelType.getName(), modelType, register);
   }

   /**
    * Join and Get shared ViewModel instance which be owned by this Fragment.
    *
    * @param register true if you want this View register the topic, otherwise just preview.
    */
   @Override
   public <M> M getOwnTopic(String topicId, Class<M> modelType, boolean register) {
      return getTopic(this, topicId, modelType, register);
   }

   /**
    * Join and Get shared ViewModel instance which be owned by associated Activity.
    *
    * @param register true if you want this View register the topic, otherwise just preview.
    */
   @Override
   public <M> M getHostTopic(Class<M> modelType, boolean register) {
      return getTopic(host, modelType.getName(), modelType, register);
   }

   /**
    * Join and Get shared ViewModel instance which be owned by associated Activity.
    *
    * @param register true if you want this View register the topic, otherwise just preview.
    */
   @Override
   public <M> M getHostTopic(String topicId, Class<M> modelType, boolean register) {
      return getTopic(host, topicId, modelType, register);
   }

   @Override
   public <M> M getAppTopic(Class<M> modelType, boolean register) {
      return getAppTopic(modelType.getName(), modelType, register);
   }

   @Override
   public <M> M getAppTopic(String topicId, Class<M> modelType, boolean register) {
      Application app = host.getApplication();

      if (app instanceof DkApp) {
         return getTopic(((DkApp) app), topicId, modelType, register);
      }

      throw new RuntimeException("The app must implement #DkApp");
   }

   /**
    * Join and Get shared ViewModel instance which be owned by a owner (Application, Activity or Fragment...).
    * The instance will be removed when no client observes the topic.
    * Note that, you must call this method when host is in active state.
    *
    * @param register true if you want this View register the topic, otherwise just preview.
    */
   @Override
   public <M> M getTopic(ViewModelStoreOwner owner, String topicName, Class<M> modelType, boolean register) {
      return new TopicProvider(owner, this).getTopic(topicName, modelType, register);
   }

   public void hideSoftKeyboard() {
      if (context != null) {
         Dks.hideSoftKeyboard(context, layout);
      }
   }

   public void snack(int msgRes, int type) {
      DkSnackbar.newIns(layout)
         .asType(type)
         .setMessage(msgRes)
         .show();
   }

   public void snack(String message, int type) {
      DkSnackbar.newIns(layout)
         .asType(type)
         .setMessage(message)
         .show();
   }

   public void toast(int msgRes) {
      DkToastbar.newIns(layout)
         .setMessage(msgRes)
         .show();
   }

   public void toast(String message) {
      DkToastbar.newIns(layout)
         .setMessage(message)
         .show();
   }
}
