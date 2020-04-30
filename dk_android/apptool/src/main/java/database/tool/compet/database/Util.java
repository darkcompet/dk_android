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

package tool.compet.database;

import androidx.annotation.NonNull;

public class Util {
   static String preventInjection(@NonNull String value) {
      for (int tmp = value.length() - 1; tmp >= 0; --tmp) {
         if (value.charAt(tmp) == '\'') {
            StringBuilder builder = new StringBuilder(value);
            builder.replace(tmp, tmp + 1, "''");

            // apply injection prevention to all ' character
            for (int index = tmp - 1; index >= 0; --index) {
               if (builder.charAt(index) == '\'') {
                  builder.replace(index, index + 1, "''");
               }
            }

            value = builder.toString();

            break;
         }
      }

      return value;
   }
}
