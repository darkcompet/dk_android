/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.log;

import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import tool.compet.core.constant.DkConst;
import tool.compet.core.util.DkStrings;
import tool.compet.core.util.DkUtils;

import static tool.compet.core.BuildConfig.DEBUG;

/**
 * Android log in Logcat.
 * It contains some helpful logs as: performance benchmark
 */
public class DkLogs {
    // Log types
    private static final int TYPE_DEBUG = 1;
    private static final int TYPE_INFO = 2;
    private static final int TYPE_WARN = 3;
    private static final int TYPE_ERROR = 4;

    // For benchmark
    private static long benchmarkStartTime;
    private static ArrayDeque<String> benchmarkTaskNames;

    /**
     * Throw RuntimeException.
     */
    public static void complain(Object where, String format, Object... args) {
        throw new RuntimeException(makePrefix(where) + DkStrings.format(format, args));
    }

    /**
     * Debug log. Can't be invoked in production.
     * Note that, we should remove all debug code when release.
     */
    // todo: Remove all debug line which call this
    public static void debug(Object where, String format, Object... args) {
        logcat(false, TYPE_DEBUG, where, format, args);
    }

    /**
     * Debug log with stack trace. Can't be invoked in production.
     * Note that, we should remove all debug code when release.
     */
    // todo: Remove all debug line which call this
    public static void debugWithTrace(String format, Object... args) {
        logcatWithStackTrace(false, TYPE_DEBUG, format, args);
    }

    /**
     * Log info. Can be invoked in production.
     *
     * If sometime caller wanna log it only in local env, so caller can
     * wrap this function with DEBUG constant instead of call it directly.
     */
    public static void info(Object where, String format, Object... args) {
        logcat(true, TYPE_INFO, where, format, args);
    }

    /**
     * Normal log with stack trace. Can't call in production.
     */
    public static void infoWithTrace(String format, Object... args) {
        logcatWithStackTrace(false, TYPE_INFO, format, args);
    }

    /**
     * Warning log. Can be invoked in production.
     */
    public static void warn(Object where, String format, Object... args) {
        logcat(true, TYPE_WARN, where, format, args);
    }

    /**
     * Warning log. Can be invoked in production.
     */
    public static void warnWithTrace(String format, Object... args) {
        logcatWithStackTrace(true, TYPE_WARN, format, args);
    }

    /**
     * Error log. Can be invoked in production.
     */
    public static void error(Object where, String format, Object... args) {
        logcat(true, TYPE_ERROR, where, format, args);
    }

    /**
     * Error log with stack trace. Can be invoked in production.
     */
    public static void errorWithTrace(String format, Object... args) {
        logcatWithStackTrace(true, TYPE_ERROR, format, args);
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
        logcatException(where, e, format, args);
    }

    /**
     * Start benchmark. Can't be invoked in production.
     */
    public static void tick(Object where, String task) {
        if (benchmarkTaskNames == null) {
            benchmarkTaskNames = new ArrayDeque<>();
        }

        benchmarkTaskNames.push(task);
        logcat(false, TYPE_DEBUG, where, "Task [%s] was started", task);
        benchmarkStartTime = System.currentTimeMillis();
    }

    /**
     * End benchmark. Can't be invoked in production.
     */
    public static void tock(Object where) {
        long elapsed = System.currentTimeMillis() - benchmarkStartTime;
        logcat(false, TYPE_DEBUG, where,
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

    private static void logcat(boolean validAtHonban, int logType, Object where, String format, Object... args) {
        String message = format;
        if (args != null && args.length > 0) {
            message = DkStrings.format(format, args);
        }

        message = makePrefix(where) + message;

        logActual(validAtHonban, logType, message);
    }

    private static void logcatWithStackTrace(boolean validAtHonban, int logType, String format, Object... args) {
        String message = format;
        if (args != null && args.length > 0) {
            message = DkStrings.format(format, args);
        }

        List<String> descriptions = new ArrayList<>();
        for (StackTraceElement elm : Thread.currentThread().getStackTrace()) {
            String description = DkStrings.format("%s (%d) ==> %s.%s()", elm.getFileName(), elm.getLineNumber(), elm.getClassName(), elm.getMethodName());
            descriptions.add(description);
        }
        String trace = DkStrings.join('\n', descriptions);
        message += "\nStack Trace:\n" + trace;

        logActual(validAtHonban, logType, message);
    }

    private static void logcatException(Object where, Throwable e, String format, Object[] args) {
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

        logcat(true, TYPE_ERROR, where, sb.toString());
    }

    private static void logActual(boolean validAtHonban, int logType, String message) {
        if (! DEBUG && ! validAtHonban) {
            DkUtils.complainAt(DkLogs.class, "Can not use log type %d in product version. You maybe need wrap it in DEBUG constant.", logType);
        }

        switch (logType) {
            case TYPE_DEBUG: {
                Log.d("xxx_debug", message);
                break;
            }
            case TYPE_INFO: {
                Log.i("xxx_info", message);
                break;
            }
            case TYPE_WARN: {
                Log.w("xxx_warn", message);
                break;
            }
            case TYPE_ERROR: {
                Log.e("xxx_error", message);
                break;
            }
        }
    }
}
