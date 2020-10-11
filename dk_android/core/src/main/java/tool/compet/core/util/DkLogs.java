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
		throw new RuntimeException(_makePrefix(where) + DkStrings.format(format, args));
	}

	/**
	 * Debug log. Can't be invoked in production.
	 */
	public static void debug(Object where, String format, Object... args) {
		_log(false, 'd', where, "__________ " + format, args);
	}

	/**
	 * Normal log. Can't call in production.
	 */
	public static void info(Object where, String format, Object... args) {
		_log(false, 'i', where, format, args);
	}

	/**
	 * Warning log. Can be invoked in production.
	 */
	public static void warn(Object where, String format, Object... args) {
		_log(true, 'w', where, format, args);
	}

	/**
	 * Exception log. Can be invoked in production.
	 */
	public static void error(Object where, Throwable e) {
		error(where, e, null);
		e.printStackTrace();
	}

	/**
	 * Exception log. Can be invoked in production.
	 */
	public static void error(Object where, Throwable e, String format, Object... args) {
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

		_log(true, 'e', where, sb.toString());
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

	private static String _makePrefix(Object where) {
		String prefix = "~ ";

		if (where != null) {
			String loc = where instanceof Class ? ((Class) where).getName() : where.getClass().getName();
			loc = loc.substring(loc.lastIndexOf('.') + 1);
			prefix = loc + prefix;
		}

		return prefix;
	}
	private static void _log(boolean isValidOnRelease, char type, Object where, String format, Object... args) {
		if (!DEBUG && !isValidOnRelease) {
			complain(DkLogs.class, "Can not use log%c() in product version." +
				" You maybe need wrap it inside statement of if-DEBUG.", type);
		}
		if (args != null && args.length > 0) {
			format = DkStrings.format(format, args);
		}

		String msg = _makePrefix(where) + format;

		switch (type) {
			case 'd': {
				Log.d("dk3x_debug", msg);
				break;
			}
			case 'i': {
				Log.i("dk3x_info", msg);
				break;
			}
			case 'w': {
				Log.w("dk3x_warn", msg);
				break;
			}
			case 'e': {
				Log.e("dk3x_error", msg);
				break;
			}
		}
	}
}
