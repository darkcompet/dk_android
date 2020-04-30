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

public abstract class AbsMapObserver<T, R> implements DkObserver<T> {
   protected final DkObserver<R> child;

   public AbsMapObserver(DkObserver<R> child) {
      this.child = child;
   }

   @Override
   public void onSubscribe(DkControllable controllable) {
      child.onSubscribe(controllable);
   }

   @Override
   public void onError(Throwable e) {
      child.onError(e);
   }

   @Override
   public void onComplete() {
      child.onComplete();
   }

   @Override
   public void onFinal() {
      child.onFinal();
   }
}
