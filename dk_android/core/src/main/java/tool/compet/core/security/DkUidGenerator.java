/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
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
	 * @param expectLength should greater than or equals 19.
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
