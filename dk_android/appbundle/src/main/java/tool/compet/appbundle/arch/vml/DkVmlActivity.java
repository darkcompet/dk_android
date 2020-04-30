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

package tool.compet.appbundle.arch.vml;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import tool.compet.appbundle.arch.DkSimpleActivity;
import tool.compet.appbundle.eventbus.DkEventBus;

/**
 * View component of DVC design pattern.
 *
 * <p> App side can extend this class to implement DVC design pattern which auto wireScriptersAndViewModels
 * View, Controller and Decorator for us.
 */
public abstract class DkVmlActivity extends DkSimpleActivity implements VmlView {
   private List<DkVmlViewLogic> vls;

   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      // this must be run after #super.onCreate()
      vls = new VmlInjector(this).start();

      for (DkVmlViewLogic vl : vls) {
         vl.onCreate(this, savedInstanceState);
      }
   }

   @CallSuper
   @Override
   protected void onPostCreate(@Nullable Bundle savedInstanceState) {
      super.onPostCreate(savedInstanceState);

      for (DkVmlViewLogic vl : vls) {
         vl.onPostCreate(this, savedInstanceState);
      }
   }

   @Override
   protected void onStart() {
      super.onStart();

      for (DkVmlViewLogic vl : vls) {
         vl.onStart(this);

         DkEventBus.getIns().register(vl);
      }
   }

   @Override
   public void onActive(boolean isResume) {
      super.onActive(isResume);

      for (DkVmlViewLogic vl : vls) {
         vl.onActive(this, isResume);
      }
   }

   @Override
   public void onInactive(boolean isPause) {
      super.onInactive(isPause);

      for (DkVmlViewLogic vl : vls) {
         vl.onInactive(this, isPause);
      }
   }

   @Override
   protected void onStop() {
      super.onStop();

      for (DkVmlViewLogic vl : vls) {
         vl.onRestart(this);

         DkEventBus.getIns().unregister(vl);
      }
   }

   // Note: after onStop() is onCreate() or onDestroy()
   @Override
   protected void onRestart() {
      super.onRestart();

      for (DkVmlViewLogic vl : vls) {
         vl.onRestart(this);
      }
   }

   @Override
   protected void onDestroy() {
      if (vls != null) {
         for (DkVmlViewLogic vl : vls) {
            vl.onDestroy(this);
         }
         vls.clear();
      }
      super.onDestroy();
   }

   @Override
   public void onLowMemory() {
      super.onLowMemory();

      for (DkVmlViewLogic vl : vls) {
         vl.onLowMemory(this);
      }
   }

   @Override
   public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);

      for (DkVmlViewLogic vl : vls) {
         vl.onConfigurationChanged(this, newConfig);
      }
   }

   @Override
   protected void onSaveInstanceState(Bundle outState) {
      for (DkVmlViewLogic vl : vls) {
         vl.onSaveInstanceState(this, outState);
      }
      super.onSaveInstanceState(outState);
   }

   @Override
   protected void onRestoreInstanceState(Bundle savedInstanceState) {
      for (DkVmlViewLogic vl : vls) {
         vl.onRestoreInstanceState(this, savedInstanceState);
      }
      super.onRestoreInstanceState(savedInstanceState);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      for (DkVmlViewLogic vl : vls) {
         vl.onActivityResult(this, requestCode, resultCode, data);
      }
      super.onActivityResult(requestCode, resultCode, data);
   }

   @Override
   public void onRequestPermissionsResult(int rc, @NonNull String[] perms, @NonNull int[] res) {
      for (DkVmlViewLogic vl : vls) {
         vl.onRequestPermissionsResult(this, rc, perms, res);
      }
      super.onRequestPermissionsResult(rc, perms, res);
   }
}
