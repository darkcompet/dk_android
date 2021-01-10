/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.log;

import android.util.Log;

import java.util.ArrayDeque;

import tool.compet.core.constant.DkConst;
import tool.compet.core.util.DkStrings;
import tool.compet.core.util.DkUtils;

import static tool.compet.core.BuildConfig.DEBUG;

class MyLogs {
    // For benchmark
    private static long benchmarkStartTime;
    private static ArrayDeque<String> benchmarkTaskNames;

    static String makePrefix(Object where) {
        String prefix = "~ ";

        if (where != null) {
            String loc = where instanceof Class ? ((Class) where).getName() : where.getClass().getName();
            loc = loc.substring(loc.lastIndexOf('.') + 1);
            prefix = loc + prefix;
        }

        return prefix;
    }

    static void logcat(boolean validInProduct, char type, Object where, String format, Object... args) {
        if (!DEBUG && !validInProduct) {
            DkUtils.complainAt(DkLogs.class, "Can not use Log.%c() in product version. You maybe need wrap it in DEBUG constant.", type);
        }
        if (args != null && args.length > 0) {
            format = DkStrings.format(format, args);
        }

        String msg = makePrefix(where) + format;

        switch (type) {
            case 'd': {
                Log.d("xxx_debug", msg);
                break;
            }
            case 'i': {
                Log.i("xxx_info", msg);
                break;
            }
            case 'w': {
                Log.w("xxx_warn", msg);
                break;
            }
            case 'e': {
                Log.e("xxx_error", msg);
                break;
            }
        }
    }

    static void logcatException(Object where, Throwable e, String format, Object[] args) {
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

        logcat(true, 'e', where, sb.toString());
    }

    static void tick(Object where, String task) {
        if (benchmarkTaskNames == null) {
            benchmarkTaskNames = new ArrayDeque<>();
        }

        benchmarkTaskNames.push(task);
        logcat(false, 'd', where, "Task [%s] was started", task);
        benchmarkStartTime = System.currentTimeMillis();
    }

    static void tock(Object where) {
        long elapsed = System.currentTimeMillis() - benchmarkStartTime;
        logcat(false, 'd', where,
            "Task [%s] end in: %d s %d ms",
            benchmarkTaskNames.pop(),
            elapsed / 1000,
            (elapsed - 1000 * (elapsed / 1000)));
    }
}
