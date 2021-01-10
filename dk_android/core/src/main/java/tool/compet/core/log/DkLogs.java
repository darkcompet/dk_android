/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.log;

import tool.compet.core.util.DkStrings;

/**
 * Android log in Logcat.
 * It contains some helpful logs as: performance benchmark
 */
public class DkLogs {
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
        throw new RuntimeException(MyLogs.makePrefix(where) + DkStrings.format(format, args));
    }

    /**
     * Debug log. Can't be invoked in production.
     */
    public static void debug(Object where, String format, Object... args) {
        MyLogs.logcat(false, 'd', where, "__________ " + format, args);
    }

    /**
     * Normal log. Can't call in production.
     */
    public static void info(Object where, String format, Object... args) {
        MyLogs.logcat(false, 'i', where, format, args);
    }

    /**
     * Warning log. Can be invoked in production.
     */
    public static void warn(Object where, String format, Object... args) {
        MyLogs.logcat(true, 'w', where, format, args);
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
        MyLogs.logcatException(where, e, format, args);
    }

    /**
     * Start benchmark. Can't be invoked in production.
     */
    public static void tick(Object where, String task) {
        MyLogs.tick(where, task);
    }

    /**
     * End benchmark. Can't be invoked in production.
     */
    public static void tock(Object where) {
        MyLogs.tock(where);
    }
}
