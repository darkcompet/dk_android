/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;

import tool.compet.core4j.DkLogger;
import tool.compet.core4j.DkRunner2;
import tool.compet.core4j.DkStrings;

import static tool.compet.BuildConfig.DEBUG;

/**
 * Console log in Logcat. It provides log-callback, benchmark...
 */
public class DkLogcats implements DkLogger.LogType {
	private static final DkLogger logger = new DkLogger(DkLogcats::logActual);

	// Enable this to log back trace of current thread
	public static boolean logBackTrace;
	public static DkRunner2<Integer, String> logCallback; // logType vs logMessage

	// For benchmark
	private static long benchmarkStartTime;
	private static ArrayDeque<String> benchmarkTaskNames;

	/**
	 * Debug log. Only run at debug env (ignored at production env).
	 * Notice: should remove all debug code when release.
	 */
	public static void debug(@Nullable Object where, @Nullable String format, Object... args) {
		logger.debug(where, format, args);
	}

	/**
	 * Log info. Only run at debug env (ignored at production env).
	 */
	public static void info(@Nullable Object where, @Nullable String format, Object... args) {
		logger.info(where, format, args);
	}

	/**
	 * Log notice. Only run at debug env (ignored at production env).
	 */
	public static void notice(@Nullable Object where, @Nullable String format, Object... args) {
		logger.notice(where, format, args);
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
	 * Critical log. Run at both debug and production env.
	 */
	public static void critical(@Nullable Object where, @Nullable String format, Object... args) {
		logger.critical(where, format, args);
	}

	/**
	 * Emergency log. Run at both debug and production env.
	 */
	public static void emergency(@Nullable Object where, @Nullable String format, Object... args) {
		logger.emergency(where, format, args);
	}

	/**
	 * Start benchmark. Only run at debug env (ignored at production env).
	 */
	public static void tick(@Nullable Object where, String task) {
		if (DEBUG) {
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
		if (DEBUG) {
			long elapsed = System.currentTimeMillis() - benchmarkStartTime;
			logger.debug(where,
				"Task [%s] end in: %d s %d ms",
				benchmarkTaskNames.pop(),
				elapsed / 1000,
				(elapsed - 1000 * (elapsed / 1000)));
		}
	}

	private static void logActual(int logType, String message) {
		String logTag = "xxx_" + DkLogger.LogType.name(logType);

		if (logBackTrace) {
			ArrayList<String> descriptions = new ArrayList<>();
			for (StackTraceElement elm : Thread.currentThread().getStackTrace()) {
				String description = tool.compet.core4j.DkStrings.format("%s (%d) ==> %s.%s()", elm.getFileName(), elm.getLineNumber(), elm.getClassName(), elm.getMethodName());
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
			case TYPE_INFO:
			case TYPE_NOTICE: {
				Log.i(logTag, message);
				break;
			}
			case TYPE_WARNING: {
				Log.w(logTag, message);
				break;
			}
			case TYPE_ERROR:
			case TYPE_CRITICAL:
			case TYPE_EMERGENCY: {
				Log.e(logTag, message);
				break;
			}
		}

		if (logCallback != null) {
			logCallback.run(logType, message);
		}
	}
}
