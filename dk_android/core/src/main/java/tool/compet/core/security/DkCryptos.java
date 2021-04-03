/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.security;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * This class, provides common basic operations for crypto.
 */
public class DkCryptos {
	public static String calcMd5Hash(String message) {
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
