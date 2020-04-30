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

import android.app.Activity;

import tool.compet.appbundle.arch.navigator.DkFragmentNavigator;

/**
 * Activity interface for Dk library. If a activity implements this interface,
 * then it can work where Dk library supports.
 */
public interface DkActivity {
   /**
    * Obtain itself.
    */
   Activity getActivity();

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
    * Dismiss itself.
    */
   void dismiss();
}
