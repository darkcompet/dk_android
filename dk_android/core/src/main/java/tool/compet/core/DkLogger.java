/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DkLogger {
	public interface LogType {
		String TYPE_DEBUG = "debug";
		String TYPE_INFO = "info";
		String TYPE_NOTICE = "notice";
		String TYPE_WARNING = "warning";
		String TYPE_ERROR = "error";
		String TYPE_EMERGENCY = "emergency";
	}

	public interface LogAdapter {
		void log(String logType, String message);
	}

	// Log adapter for actual log
	private LogAdapter adapter;

	public DkLogger(LogAdapter adapter) {
		this.adapter = adapter;
	}

	public void setAdapter(LogAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * Log debug.
	 */
	public void debug(@Nullable Object where, @Nullable String format, Object... args) {
		log(LogType.TYPE_DEBUG, where, format, args);
	}

	/**
	 * Log info.
	 */
	public void info(@Nullable Object where, @Nullable String format, Object... args) {
		log(LogType.TYPE_INFO, where, format, args);
	}

	/**
	 * Log notice.
	 */
	public void notice(@Nullable Object where, @Nullable String format, Object... args) {
		log(LogType.TYPE_NOTICE, where, format, args);
	}

	/**
	 * Log warning.
	 */
	public void warning(@Nullable Object where, @Nullable String format, Object... args) {
		log(LogType.TYPE_WARNING, where, format, args);
	}

	/**
	 * Log error.
	 */
	public void error(@Nullable Object where, @Nullable String format, Object... args) {
		log(LogType.TYPE_ERROR, where, format, args);
	}

	/**
	 * Log exception.
	 */
	public void error(@Nullable Object where, Throwable e) {
		error(where, e, null);
	}

	/**
	 * Log exception.
	 */
	public void error(@Nullable Object where, Throwable e, @Nullable String format, Object... args) {
		log(LogType.TYPE_ERROR, where, makeExceptionMessage(e, format, args));
	}

	/**
	 * Log emergency.
	 */
	public void emergency(@Nullable Object where, @Nullable String format, Object... args) {
		log(LogType.TYPE_EMERGENCY, where, format, args);
	}

	// region: Protected

	protected String makePrefix(@Nullable Object where) {
		String prefix = "~ ";

		if (where != null) {
			String loc = where instanceof Class ? ((Class) where).getName() : where.getClass().getName();
			loc = loc.substring(loc.lastIndexOf('.') + 1);
			prefix = loc + prefix;
		}

		return prefix;
	}

	protected void log(String logType, @Nullable Object where, @Nullable String format, Object... args) {
		String message = format;
		if (args != null && args.length > 0) {
			message = DkStrings.format(format, args);
		}

		message = makePrefix(where) + message;

		adapter.log(logType, message);
	}

	protected String makeExceptionMessage(Throwable e, @Nullable String format, Object... args) {
		StringBuilder sb = new StringBuilder(256);

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

		return sb.toString();
	}

	protected String makeBacktraceMessage() {
		ArrayList<String> descriptions = new ArrayList<>();
		for (StackTraceElement elm : Thread.currentThread().getStackTrace()) {
			String description = DkStrings.format("%s (%d) ==> %s.%s()", elm.getFileName(), elm.getLineNumber(), elm.getClassName(), elm.getMethodName());
			descriptions.add(description);
		}
		return "\nStack Trace:\n" + DkStrings.join('\n', descriptions);
	}

	// endregion: Protected
}
