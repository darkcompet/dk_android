/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.util;

class MyStrings {
    static int compare(CharSequence a, CharSequence b) {
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
}
