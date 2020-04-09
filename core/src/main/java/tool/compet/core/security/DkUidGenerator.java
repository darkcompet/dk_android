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

package tool.compet.core.security;

import java.util.Random;

public class DkUidGenerator {
   private static final Random random = new Random();
   private static final char[] idoms = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

   /**
    * Generate unique 32-characters random key. Each character is alphabet or underscore.
    */
   public static String generateRandomKey() {
      return generateRandomKey(32);
   }

   /**
    * Generate unique key which has given length. Each character ise alphabet or underscore.
    *
    * @param expectLength should greater or equals than 19.
    *
    * @return unique random key which has length is at least expectLength.
    */
   public static String generateRandomKey(int expectLength) {
      StringBuilder key = new StringBuilder(expectLength);

      String suffix = String.valueOf(System.nanoTime());

      final int BOUNDS = idoms.length;
      final int N = expectLength - suffix.length() - 1;

      for (int i = 0; i < N; ++i) {
         key.append(idoms[random.nextInt(BOUNDS)]);
      }

      key.append('_').append(suffix);

      return key.toString();
   }
}
