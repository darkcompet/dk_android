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

import androidx.fragment.app.Fragment;

import tool.compet.appbundle.arch.navigator.DkFragmentNavigator;

/**
 * Fragment interface for Dk library. Implements this to work with Dk library.
 */
public interface DkFragment {
   /**
    * Obtain fragment itself.
    */
   Fragment getFragment();

   /**
    * Each fragment should response #onBackPressed() from host activity.
    *
    * @return true if this fragment will handle this event, otherwise false.
    */
   boolean onBackPressed();

   /**
    * Dismiss itself, like #Activity.finish().
    */
   void dismiss();

   /**
    * Specify whether this fragment should be retained instance during configuration changed.
    */
   boolean isRetainInstance();

   /**
    * Specify id of layout resource for this fragment.
    */
   int layoutResourceId();

   /**
    * Specify id of container inside the layout of this fragment. This id can be used in
    * fragment transaction for other screens.
    */
   int fragmentContainerId();

   /**
    * Be called from other fragments or itself.
    *
    * @return children fragment navigator that the fragment owns
    */
   DkFragmentNavigator getChildNavigator();

   /**
    * Be called from other fragments or itself.
    *
    * @return parent fragment navigator that the fragment is owned
    */
   DkFragmentNavigator getParentNavigator();

   /**
    * Indicates the fragment is resumsed or come to front.
    *
    * @param isResume true if this fragment is in resume state, otherwise it is on front.
    */
   void onActive(boolean isResume);

   /**
    * Indicates the fragment is paused or go to behind.
    *
    * @param isPause true if this fragment is in pause state, otherwise it is in behind.
    */
   void onInactive(boolean isPause);
}
