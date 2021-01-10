/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.util;

import android.content.Context;

import java.util.Locale;

/**
 * String utilities (public api).
 */
public class DkStrings {
    public static boolean isWhite(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static void requireNotEmpty(String data, String message) {
        if (isEmpty(data)) {
            throw new RuntimeException(message);
        }
    }

    /**
     * @return 0 (if equals), -1 (if a < b), 1 (if a > b).
     */
    public static int compare(CharSequence a, CharSequence b) {
        return MyStrings.compare(a, b);
    }

    /**
     * Remove start-leading and end-leading WHITESPACE characters and characters in `delimiters` from `msg`.
     */
    public static String trimExtras(String msg, char... delimiters) {
        return MyStringTrimmer.trimExtras(msg, delimiters);
    }

    /**
     * Remove start-leading and end-leading characters inside ONLY `targets` from `msg`.
     */
    public static String trimExact(String msg, char... targets) {
        return MyStringTrimmer.trimExact(msg, targets);
    }

    public static boolean isEquals(CharSequence a, CharSequence b) {
        return a == b || (a != null && a.equals(b));
    }

    public static String join(char delimiter, String... items) {
        return MyStringJoiner.join(delimiter, items);
    }

    public static String join(char delimiter, Iterable<String> items) {
        return MyStringJoiner.join(delimiter, items);
    }

    public static String join(CharSequence delimiter, Iterable<String> items) {
        return MyStringJoiner.join(delimiter, items);
    }

    public static String join(CharSequence delimiter, String... items) {
        return MyStringJoiner.join(delimiter, items);
    }

    public static String format(Context context, int format, Object... args) {
        return format(context.getString(format), args);
    }

    public static String format(String format, Object... args) {
        return format == null || (args == null || args.length == 0) ? format : String.format(Locale.US, format, args);
    }
}
