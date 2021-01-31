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

    /**
     * @return 0 (if equals), -1 (if a < b), 1 (if a > b).
     */
    public static int compare(CharSequence a, CharSequence b) {
        if (a == null) {
            return b == null ? 0 : -1;
        }
        if (b == null) {
            return 1;
        }

        final int M = a.length();
        final int N = b.length();
        final int C = Math.min(M, N);
        char ch1, ch2;

        for (int i = 0; i < C; ++i) {
            ch1 = a.charAt(i);
            ch2 = b.charAt(i);

            if (ch1 < ch2) {
                return -1;
            }
            if (ch1 > ch2) {
                return 1;
            }
        }
        return Integer.compare(M, N);
    }

    /**
     * Remove from given `msg` start-leading and end-leading characters which is WHITESPACE character or character in given `delimiters`.
     */
    public static String trimMore(String msg, char... delimiters) {
        return MyStringTrimmer.trimMore(msg, delimiters);
    }

    /**
     * Remove from given `msg` start-leading and end-leading characters which is character in given `delimiters`.
     */
    public static String trimExact(String msg, char... delimiters) {
        return MyStringTrimmer.trimExact(msg, delimiters);
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
