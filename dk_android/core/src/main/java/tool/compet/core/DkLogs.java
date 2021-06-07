/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Console log in Logcat. It provides log callback, benchmark...
 * It provide debug log
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
	 * Debug log. Only run at debug env (ignored at production env).
	 * Notice: should remove all debug code when release.
	 */
	public static void debug(@Nullable Object where, @Nullable String format, Object... args) {
		if (BuildConfig.DEBUG) {
			logger.debug(where, format, args);
		}
	}

	/**
	 * Log info. Only run at debug env (ignored at production env).
	 */
	public static void info(@Nullable Object where, @Nullable String format, Object... args) {
		if (BuildConfig.DEBUG) {
			logger.info(where, format, args);
		}
	}

	/**
	 * Log notice. Only run at debug env (ignored at production env).
	 */
	public static void notice(@Nullable Object where, @Nullable String format, Object... args) {
		if (BuildConfig.DEBUG) {
			logger.notice(where, format, args);
		}
	}

	/**
	 * Warning log. Run at both debug and production env.
	 */
	public static void warning(@Nullable Object where, @Nullable String format, Object... args) {
		logger.warning(where, format, args);
	}

	/**
	 * Error log. Run at both debug and production env.
	 */
	public static void error(@Nullable Object where, @Nullable String format, Object... args) {
		logger.error(where, format, args);
	}

	/**
	 * Exception log. Run at both debug and production env.
	 */
	public static void error(@Nullable Object where, Throwable e) {
		error(where, e, null);
	}

	/**
	 * Exception log. Run at both debug and production env.
	 */
	public static void error(@Nullable Object where, Throwable e, @Nullable String format, Object... args) {
		logger.error(where, e, format, args);
	}

	/**
	 * Start benchmark. Only run at debug env (ignored at production env).
	 */
	public static void tick(@Nullable Object where, String task) {
		if (BuildConfig.DEBUG) {
			if (benchmarkTaskNames == null) {
				benchmarkTaskNames = new ArrayDeque<>();
			}

			benchmarkTaskNames.push(task);
			logger.debug(where, "Task [%s] was started", task);
			benchmarkStartTime = System.currentTimeMillis();
		}
	}

	/**
	 * End benchmark. Only run at debug env (ignored at production env).
	 */
	public static void tock(@Nullable Object where) {
		if (BuildConfig.DEBUG) {
			long elapsed = System.currentTimeMillis() - benchmarkStartTime;
			logger.debug(where,
				"Task [%s] end in: %d s %d ms",
				benchmarkTaskNames.pop(),
				elapsed / 1000,
				(elapsed - 1000 * (elapsed / 1000)));
		}
	}

	private static void logActual(String logType, String message) {
		String logTag = "xxx_" + logType;

		if (logBackTrace) {
			ArrayList<String> descriptions = new ArrayList<>();
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
