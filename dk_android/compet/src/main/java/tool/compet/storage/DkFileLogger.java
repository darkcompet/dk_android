/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.storage;

import androidx.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import tool.compet.BuildConfig;
import tool.compet.core4j.DkDateTimes;
import tool.compet.core4j.DkLogger;
import tool.compet.core4j.DkRunner2;
import tool.compet.core4j.DkStrings;
import tool.compet.core4j.DkUtils;

/**
 * File log. It provides log callback, benchmark...
 */
public class DkFileLogger implements DkLogger.LogType {
	private final DkLogger logger = new DkLogger(this::logActual);

	// Enable this to log back trace of current thread
	public static boolean logBackTrace;
	public static DkRunner2<Integer, String> logCallback; // params: (logType, message)

	// For benchmark
	private static long benchmarkStartTime;
	private static ArrayDeque<String> benchmarkTaskNames;

	// To persist log to file
	private DkRunner2<Integer, String> storage; // logType vs logMessage

	public DkFileLogger(DkRunner2<Integer, String> storage) {
		this.storage = storage;
	}

	public void setStorage(DkRunner2<Integer, String> storage) {
		this.storage = storage;
	}

	/**
	 * Debug log. Can't be invoked in production.
	 * Note that, we should remove all debug code when release.
	 */
	// todo: Remove all debug line which call this
	public void debug(@Nullable Object where, @Nullable String format, Object... args) {
		if (! BuildConfig.DEBUG) {
			DkUtils.complainAt(DkFileLogger.class, "Can not use debug-log at product version");
		}
		logger.debug(where, format, args);
	}

	/**
	 * Log info. Can be invoked in production.
	 */
	public void info(@Nullable Object where, @Nullable String format, Object... args) {
		logger.info(where, format, args);
	}

	/**
	 * Log notice. Can be invoked in production.
	 */
	public void notice(@Nullable Object where, @Nullable String format, Object... args) {
		logger.notice(where, format, args);
	}

	/**
	 * Warning log. Can be invoked in production.
	 */
	public void warning(@Nullable Object where, @Nullable String format, Object... args) {
		logger.warning(where, format, args);
	}

	/**
	 * Error log. Can be invoked in production.
	 */
	public void error(@Nullable Object where, @Nullable String format, Object... args) {
		logger.error(where, format, args);
	}

	/**
	 * Exception log. Can be invoked in production.
	 */
	public void error(@Nullable Object where, Throwable e) {
		error(where, e, null);
	}

	/**
	 * Exception log. Can be invoked in production.
	 */
	public void error(@Nullable Object where, Throwable e, @Nullable String format, Object... args) {
		logger.error(where, e, format, args);
	}

	/**
	 * Critical log. Run at both debug and production env.
	 */
	public void critical(@Nullable Object where, @Nullable String format, Object... args) {
		logger.critical(where, format, args);
	}

	/**
	 * Emergency log. Run at both debug and production env.
	 */
	public void emergency(@Nullable Object where, @Nullable String format, Object... args) {
		logger.emergency(where, format, args);
	}

	/**
	 * Start benchmark. Can't be invoked in production.
	 */
	public void tick(@Nullable Object where, String task) {
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
	public void tock(@Nullable Object where) {
		long elapsed = System.currentTimeMillis() - benchmarkStartTime;
		logger.debug(where,
			"Task [%s] end in: %d s %d ms",
			benchmarkTaskNames.pop(),
			elapsed / 1000,
			(elapsed - 1000 * (elapsed / 1000)));
	}

	private void logActual(int logType, String message) {
		if (logBackTrace) {
			List<String> descriptions = new ArrayList<>();
			for (StackTraceElement elm : Thread.currentThread().getStackTrace()) {
				String description = DkStrings.format("%s (%d) ==> %s.%s()", elm.getFileName(), elm.getLineNumber(), elm.getClassName(), elm.getMethodName());
				descriptions.add(description);
			}
			String trace = DkStrings.join('\n', descriptions);
			message += "\nStack Trace:\n" + trace;
		}

		String logMessage = "[" + DkLogger.LogType.name(logType).toUpperCase() + "] " + DkDateTimes.formatNow() + ": " + message;

		try {
			storage.run(logType, logMessage);

			if (logCallback != null) {
				logCallback.run(logType, message);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
