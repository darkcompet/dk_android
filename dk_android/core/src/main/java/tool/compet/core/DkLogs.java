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
 * Console log in Logcat. It provides log callback, benchmark...
 */
public class DkLogs implements DkLogger.LogType {
	private static final DkLogger logger = new DkLogger(DkLogs::logActual);

	// Enable this to log back trace of current thread
	public static boolean logBackTrace;
	public static DkRunner2<String, String> logCallback;

	// For benchmark
	private static long benchmarkStartTime;
	private static ArrayDeque<String> benchmarkTaskNames;

	/**
	 * Throw RuntimeException.
	 */
	public static void complain(@Nullable Object where, @Nullable String format, Object... args) {
		throw new RuntimeException(logger.makePrefix(where) + DkStrings.format(format, args));
	}

	/**
	 * Debug log. Can't be invoked in production.
	 * Notice: should remove all debug code when release.
	 */
	// todo: Remove all debug line which call this
	public static void debug(@Nullable Object where, @Nullable String format, Object... args) {
		if (! BuildConfig.DEBUG) {
			DkUtils.complainAt(DkLogs.class, "Can not use debug-log at product version");
		}
		logger.debug(where, format, args);
	}

	/**
	 * Log info. Can be invoked in production.
	 */
	public static void info(@Nullable Object where, @Nullable String format, Object... args) {
		logger.info(where, format, args);
	}

	/**
	 * Log notice. Can be invoked in production.
	 */
	public static void notice(@Nullable Object where, @Nullable String format, Object... args) {
		logger.notice(where, format, args);
	}

	/**
	 * Warning log. Can be invoked in production.
	 */
	public static void warning(@Nullable Object where, @Nullable String format, Object... args) {
		logger.warning(where, format, args);
	}

	/**
	 * Error log. Can be invoked in production.
	 */
	public static void error(@Nullable Object where, @Nullable String format, Object... args) {
		logger.error(where, format, args);
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
		logger.error(where, e, format, args);
	}

	/**
	 * Start benchmark. Can't be invoked in production.
	 */
	public static void tick(@Nullable Object where, String task) {
		if (benchmarkTaskNames == null) {
			benchmarkTaskNames = new ArrayDeque<>();
		}

		benchmarkTaskNames.push(task);
		logger.debug(where, "Task [%s] was started", task);
		benchmarkStartTime = System.currentTimeMillis();
	}

	/**
	 * End benchmark. Can't be invoked in production.
	 */
	public static void tock(@Nullable Object where) {
		long elapsed = System.currentTimeMillis() - benchmarkStartTime;
		logger.debug(where,
			"Task [%s] end in: %d s %d ms",
			benchmarkTaskNames.pop(),
			elapsed / 1000,
			(elapsed - 1000 * (elapsed / 1000)));
	}

	private static void logActual(String logType, String message) {
		String logTag = "xxx_" + logType;

		if (logBackTrace) {
			List<String> descriptions = new ArrayList<>();
			for (StackTraceElement elm : Thread.currentThread().getStackTrace()) {
				String description = DkStrings.format("%s (%d) ==> %s.%s()", elm.getFileName(), elm.getLineNumber(), elm.getClassName(), elm.getMethodName());
				descriptions.add(description);
			}
			String trace = DkStrings.join('\n', descriptions);
			message += "\nStack Trace:\n" + trace;
		}

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
				Log.e(logTag, message);
				break;
			}
		}

		if (logCallback != null) {
			logCallback.run(logType, message);
		}
	}
}
