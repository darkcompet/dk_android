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

package tool.compet.core.util;

import android.util.Log;

import java.util.ArrayDeque;

import tool.compet.core.constant.DkConst;

import static tool.compet.core.BuildConfig.DEBUG;

/**
 * Utility class, manages app logs, performance benchmark...
 */
public class DkLogs {
	// For benchmark
	private static long benchmarkStartTime;
	private static ArrayDeque<String> benchmarkTaskNames;

	private static String makePrefix(Object where) {
		String prefix = "~ ";

		if (where != null) {
			String loc = where instanceof Class ? ((Class) where).getName() : where.getClass().getName();
			loc = loc.substring(loc.lastIndexOf('.') + 1);
			prefix = loc + prefix;
		}

		return prefix;
	}

	private static void log(boolean isValidOnRelease, char type, Object where, String format, Object... args) {
		if (!DEBUG && !isValidOnRelease) {
			complain(DkLogs.class, "Can not use log%c() in product version." +
				" You maybe need wrap it inside statement of if-DEBUG.", type);
		}
		if (args != null && args.length > 0) {
			format = DkStrings.format(format, args);
		}

		String msg = makePrefix(where) + format;

		switch (type) {
			case 'd': {
				Log.d("xxx", msg);
				break;
			}
			case 'i': {
				Log.i("xxx", msg);
				break;
			}
			case 'w': {
				Log.w("xxx", msg);
				break;
			}
			case 'e': {
				Log.e("xxx", msg);
				break;
			}
		}
	}

	/**
	 * Throw RuntimeException.
	 */
	public static void complain(String format, Object... args) {
		throw new RuntimeException(DkStrings.format(format, args));
	}

	/**
	 * Throw RuntimeException.
	 */
	public static void complain(Object where, String format, Object... args) {
		throw new RuntimeException(makePrefix(where) + DkStrings.format(format, args));
	}

	/**
	 * Debug log. Can't be invoked in production.
	 */
	public static void debug(Object where, String format, Object... args) {
		log(false, 'd', where, "__________ " + format, args);
	}

	/**
	 * Normal log. Can't call in production.
	 */
	public static void log(Object where, String format, Object... args) {
		log(false, 'i', where, format, args);
	}

	/**
	 * Warning log. Can be invoked in production.
	 */
	public static void logw(Object where, String format, Object... args) {
		log(true, 'w', where, format, args);
	}

	/**
	 * Exception log. Can be invoked in production.
	 */
	public static void logex(Object where, Throwable e) {
		logex(where, e, null);
		e.printStackTrace();
	}

	/**
	 * Exception log. Can be invoked in production.
	 */
	public static void logex(Object where, Throwable e, String format, Object... args) {
		String ls = DkConst.LS;
		StringBuilder sb = new StringBuilder();

		if (format != null) {
			if (args != null) {
				format = DkStrings.format(format, args);
			}
			sb.append("Message: ").append(format).append(ls);
		}

		sb.append(e.toString()).append(ls);

		for (StackTraceElement traceElement : e.getStackTrace()) {
			sb.append("\tat ").append(traceElement).append(ls);
		}

		log(true, 'e', where, sb.toString());
	}

	/**
	 * Start benchmark. Can't be invoked in production.
	 */
	public static void tick(Object where, String task) {
		if (benchmarkTaskNames == null) {
			benchmarkTaskNames = new ArrayDeque<>();
		}

		benchmarkTaskNames.push(task);
		debug(where, "[%s] was started", task);
		benchmarkStartTime = System.currentTimeMillis();
	}

	/**
	 * End benchmark. Can't be invoked in production.
	 */
	public static void tock(Object where) {
		long elapsed = System.currentTimeMillis() - benchmarkStartTime;

		debug(where, "[%s] end in: %d s %d ms",
			benchmarkTaskNames.pop(), elapsed / 1000, (elapsed - 1000 * (elapsed / 1000)));
	}
}
