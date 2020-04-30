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

package tool.compet.core.security;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * This class, provides common basic operations for crypto.
 */
public class DkCryptos {
	public static String getMd5Hash(String message) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(message.getBytes());
			return new BigInteger(1, messageDigest.digest()).toString();
		}
		catch (Exception e) {
			e.printStackTrace();
			return message;
		}
	}
}
