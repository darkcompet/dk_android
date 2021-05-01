/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Android log in Logcat.
 * It contains some helpful logs as: performance benchmark
 */
public class DkLogs {
	// Enable this to log back trace of current thread
	public static boolean logBackTrace = false;

	// Log types
	private static final String TYPE_DEBUG = "debug";
	private static final String TYPE_INFO = "info";
	private static final String TYPE_WARNING = "warning";
	private static final String TYPE_ERROR = "error";

	// For benchmark
	private static long benchmarkStartTime;
	private static ArrayDeque<String> benchmarkTaskNames;

	/**
	 * Throw RuntimeException.
	 */
	public static void complain(@Nullable Object where, @Nullable String format, Object... args) {
		throw new RuntimeException(makePrefix(where) + DkStrings.format(format, args));
	}

	/**
	 * Debug log. Can't be invoked in production.
	 * Note that, we should remove all debug code when release.
	 */
	// todo: Remove all debug line which call this
	public static void debug(@Nullable Object where, @Nullable String format, Object... args) {
		if (! BuildConfig.DEBUG) {
			DkUtils.complainAt(DkLogs.class, "Can not use debug-log at product version");
		}
		log(TYPE_DEBUG, where, format, args);
	}

	/**
	 * Log info. Can be invoked in production.
	 * <p>
	 * If sometime caller wanna log it only in local env, so caller can
	 * wrap this function with DEBUG constant instead of call it directly.
	 */
	public static void info(@Nullable Object where, @Nullable String format, Object... args) {
		log(TYPE_INFO, where, format, args);
	}

	/**
	 * Warning log. Can be invoked in production.
	 */
	public static void warning(@Nullable Object where, @Nullable String format, Object... args) {
		log(TYPE_WARNING, where, format, args);
	}

	/**
	 * Error log. Can be invoked in production.
	 */
	public static void error(@Nullable Object where, @Nullable String format, Object... args) {
		log(TYPE_ERROR, where, format, args);
	}

	/**
	 * Exception log. Can be invoked in production.
	 */
	public static void error(@Nullable Object where, Throwable e) {
		error(where, e, null);
	}

	/**
	 * Exception log. Can be invoked in production.
	 */
	public static void error(@Nullable Object where, Throwable e, @Nullable String format, Object... args) {
		StringBuilder sb = new StringBuilder();

		if (format != null) {
			if (args != null) {
				format = DkStrings.format(format, args);
			}
			sb.append("Message: ").append(format).append(DkConst.LS);
		}

		sb.append(e.toString()).append(DkConst.LS);

		for (StackTraceElement traceElement : e.getStackTrace()) {
			sb.append("\tat ").append(traceElement).append(DkConst.LS);
		}

		log(TYPE_ERROR, where, sb.toString());
		e.printStackTrace();
	}

	/**
	 * Start benchmark. Can't be invoked in production.
	 */
	public static void tick(@Nullable Object where, String task) {
		if (benchmarkTaskNames == null) {
			benchmarkTaskNames = new ArrayDeque<>();
		}

		benchmarkTaskNames.push(task);
		log(TYPE_DEBUG, where, "Task [%s] was started", task);
		benchmarkStartTime = System.currentTimeMillis();
	}

	/**
	 * End benchmark. Can't be invoked in production.
	 */
	public static void tock(@Nullable Object where) {
		long elapsed = System.currentTimeMillis() - benchmarkStartTime;
		log(TYPE_DEBUG, where,
			"Task [%s] end in: %d s %d ms",
			benchmarkTaskNames.pop(),
			elapsed / 1000,
			(elapsed - 1000 * (elapsed / 1000)));
	}

	private static String makePrefix(@Nullable Object where) {
		String prefix = "~ ";

		if (where != null) {
			String loc = where instanceof Class ? ((Class) where).getName() : where.getClass().getName();
			loc = loc.substring(loc.lastIndexOf('.') + 1);
			prefix = loc + prefix;
		}

		return prefix;
	}

	private static void log(String logType, @Nullable Object where, @Nullable String format, Object... args) {
		String message = format;
		if (args != null && args.length > 0) {
			message = DkStrings.format(format, args);
		}

		message = makePrefix(where) + message;

		logActual(logType, message);
	}

	private static void logActual(String logType, String message) {
		String logTag = "xxx_" + logType;

		switch (logType) {
			case TYPE_DEBUG: {
				Log.d(logTag, message);
				break;
			}
			case TYPE_INFO: {
				Log.i(logTag, message);
				break;
			}
			case TYPE_WARNING: {
				Log.w(logTag, message);
				break;
			}
			case TYPE_ERROR: {
				if (logBackTrace) {
					List<String> descriptions = new ArrayList<>();
					for (StackTraceElement elm : Thread.currentThread().getStackTrace()) {
						String description = DkStrings.format("%s (%d) ==> %s.%s()", elm.getFileName(), elm.getLineNumber(), elm.getClassName(), elm.getMethodName());
						descriptions.add(description);
					}
					String trace = DkStrings.join('\n', descriptions);
					message += "\nStack Trace:\n" + trace;
				}
				Log.e(logTag, message);
				break;
			}
		}
	}
}
