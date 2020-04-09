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

package tool.compet.core.math;

/**
 * This class, provides common basic operations for math.
 */
public final class DkMaths {
	public static boolean parseBoolean(String s) {
		return "1".equals(s) || "true".equalsIgnoreCase(s);
	}

	public static short parseShort(String s) {
		return (short) parseInt(s);
	}

	public static int parseInt(String s) {
		if (s == null) {
			return 0;
		}
		int i = 0, res = 0, N = s.length();

		while (i < N && s.charAt(i) == '-') {
			++i;
		}
		boolean minus = ((i & 1) == 1);
		char ch;

		while (i < N && '0' <= (ch = s.charAt(i++)) && ch <= '9') {
			res = (res << 3) + (res << 1) + (ch - '0');
		}
		return minus ? -res : res;
	}

	public static long parseLong(String s) {
		if (s == null) {
			return 0L;
		}
		int i = 0, N = s.length();
		long res = 0L;

		while (i < N && s.charAt(i) == '-') {
			++i;
		}
		boolean minus = ((i & 1) == 1);
		char ch;

		while (i < N && '0' <= (ch = s.charAt(i++)) && ch <= '9') {
			res = (res << 3) + (res << 1) + (ch - '0');
		}
		return minus ? -res : res;
	}

	public static float parseFloat(String s) {
		try {
			return Float.parseFloat(s);
		}
		catch (Exception ignore) {
			return 0f;
		}
	}

	public static double parseDouble(String s) {
		try {
			return Double.parseDouble(s);
		}
		catch (Exception ignore) {
			return 0.0;
		}
	}

	public static int min(int... a) {
		int min = a[0];

		for (int x : a) {
			if (min > x) {
				min = x;
			}
		}
		return min;
	}

	public static long min(long... a) {
		long min = a[0];

		for (long x : a) {
			if (min > x) {
				min = x;
			}
		}
		return min;
	}

	public static float min(float... a) {
		float min = a[0];

		for (float x : a) {
			if (min > x) {
				min = x;
			}
		}
		return min;
	}

	public static double min(double... a) {
		double min = a[0];

		for (double x : a) {
			if (min > x) {
				min = x;
			}
		}
		return min;
	}

	public static int max(int... a) {
		int max = a[0];

		for (int x : a) {
			if (max < x) {
				max = x;
			}
		}
		return max;
	}

	public static long max(long... a) {
		long max = a[0];

		for (long x : a) {
			if (max < x) {
				max = x;
			}
		}
		return max;
	}

	public static float max(float... a) {
		float max = a[0];

		for (float x : a) {
			if (max < x) {
				max = x;
			}
		}
		return max;
	}

	public static double max(double... a) {
		double max = a[0];

		for (double x : a) {
			if (max < x) {
				max = x;
			}
		}
		return max;
	}

	public static double sin(double degrees) {
		return Math.sin(Math.PI * degrees / 180.0);
	}

	public static double cos(double degrees) {
		return Math.cos(Math.PI * degrees / 180.0);
	}

	public static double tan(double degrees) {
		return Math.tan(Math.PI * degrees / 180.0);
	}

	public static int fastPow(int x, int n) {
		if (n == 0) {
			return x == 0 ? 0 : 1;
		}
		if (n < 0) {
			return 0;
		}

		int res = 1;

		// Express n as bit sequence: 101101, then res = x * x^4 * x^8 * x^32
		while (n > 0) {
			// multiple res with current x for bit one
			if ((n & 1) == 1) {
				res *= x;
			}
			// check bit from right to left
			if ((n >>= 1) > 0) {
				x *= x;
			}
		}

		return res;
	}

	public static long fastPow(long x, int n) {
		if (n == 0) {
			return 1;
		}
		if (n < 0) {
			return 0;
		}

		long res = 1;

		// Express n as bit sequence: 101101, then res = x * x^4 * x^8 * x^32
		while (n > 0) {
			// multiple res with current x for bit one
			if ((n & 1) == 1) {
				res *= x;
			}
			// check bit from right to left
			if ((n >>= 1) > 0) {
				x *= x;
			}
		}

		return res;
	}

	public static float fastPow(float x, int n) {
		if (n == 0) {
			return 1;
		}
		if (n < 0) {
			return 1f / fastPow(x, -n);
		}

		float res = 1f;

		// Express n as bit sequence: 101101, then res = x * x^4 * x^8 * x^32
		while (n > 0) {
			// multiple res with current x for bit one
			if ((n & 1) == 1) {
				res *= x;
			}
			// check bit from right to left
			if ((n >>= 1) > 0) {
				x *= x;
			}
		}

		return res;
	}

	public static double fastPow(double x, int n) {
		if (n == 0) {
			return 1;
		}
		if (n < 0) {
			return 1.0 / fastPow(x, -n);
		}

		double res = 1;

		// Express n as bit sequence: 101101, then res = x * x^4 * x^8 * x^32
		while (n > 0) {
			// multiple res with current x for bit one
			if ((n & 1) == 1) {
				res *= x;
			}
			// check bit from right to left
			if ((n >>= 1) > 0) {
				x *= x;
			}
		}

		return res;
	}

	/**
	 * @return degrees in range [-180, 180].
	 */
	public static double normalizeAngle(double degrees) {
		if (degrees > 360 || degrees < -360) {
			degrees %= 360;
		}
		if (degrees > 180) {
			degrees -= 360;
		}
		if (degrees < -180) {
			degrees += 360;
		}
		return degrees;
	}

	/**
	 * @return nomarlized value in range [0, 1].
	 */
	public static double normalize(double value, double from, double to) {
		double min = from < to ? from : to;
		double max = from < to ? to : from;

		return (value - min) / (max - min);
	}

	/**
	 * @return given value if it is in range [min, max]. Otherwise return min or max.
	 */
	public static double clamp(double value, double min, double max) {
		return value < min ? min : value > max ? max : value;
	}
}
