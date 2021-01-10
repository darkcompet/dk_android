/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.util;

import java.util.Iterator;

class MyStringJoiner {
    static String join(char delimiter, String[] items) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : items) {
            if (first) {
                first = false;
            }
            else {
                sb.append(delimiter);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    static String join(char delimiter, Iterable<String> items) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = items.iterator();

        if (it.hasNext()) {
            sb.append(it.next());

            while (it.hasNext()) {
                sb.append(delimiter);
                sb.append(it.next());
            }
        }
        return sb.toString();
    }

    static String join(CharSequence delimiter, Iterable<String> items) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = items.iterator();

        if (it.hasNext()) {
            sb.append(it.next());

            while (it.hasNext()) {
                sb.append(delimiter);
                sb.append(it.next());
            }
        }
        return sb.toString();
    }

    static String join(CharSequence delimiter, String... items) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (String item : items) {
            if (first) {
                first = false;
            }
            else {
                sb.append(delimiter);
            }
            sb.append(item);
        }

        return sb.toString();
    }
}
