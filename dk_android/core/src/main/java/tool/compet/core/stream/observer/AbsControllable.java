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

package tool.compet.core.stream.observer;

import tool.compet.core.util.DkLogs;

import static tool.compet.core.BuildConfig.DEBUG;

/**
 * Controllable observer which user can use it to cancel, resume, pause... current stream any time.
 * Since user normally control the stream in main thread, but the stream is executed IO thread almostly,
 * so we must
 */
public abstract class AbsControllable {
   // Parent controllable
   protected DkControllable parent;

   // Indicates child requested resume event
   protected volatile boolean isResume;

   // Indicate this and parent have resumed succeed or not
   protected volatile boolean isResumed;

   // Indicates child requested pause event
   protected volatile boolean isPause;

   // Indicates this and parent have paused succeed or not
   protected volatile boolean isPaused;

   // Indicates child requested cancel event
   protected volatile boolean isCancel;

   // Indicate this and parent have cancelled succeed or not
   protected volatile boolean isCanceled;

   /**
    * Subclass should overide if want to handle Resume event.
    */
   public synchronized boolean resume() {
      boolean ok = true;

      isResume = true;

      if (parent != null) {
         ok = parent.resume();
      }

      isResumed = ok;

      return ok;
   }

   /**
    * Subclass should overide if want to handle Pause event.
    */
   public synchronized boolean pause() {
      boolean ok = true;

      isPause = true;

      if (parent != null) {
         ok = parent.pause();
      }

      isPaused = ok;

      return ok;
   }

   /**
    * Subclass should overide if want to handle Cancel event.
    */
   public synchronized boolean cancel(boolean mayInterruptThread) {
      boolean ok = true;

      isCancel = true;

      if (parent != null) {
         ok = parent.cancel(mayInterruptThread);
      }

      if (DEBUG) {
         DkLogs.log(this, "Cancelled with mayInterruptThread %b inside parent with result %b",
            mayInterruptThread, ok);
      }

      isCanceled = ok;

      return ok;
   }

   public boolean isResumed() {
      return isResumed;
   }

   public boolean isPaused() {
      return isPaused;
   }

   public boolean isCanceled() {
      return isCanceled;
   }
}
