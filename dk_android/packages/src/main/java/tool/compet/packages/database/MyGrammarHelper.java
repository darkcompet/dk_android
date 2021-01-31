/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.database;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tool.compet.core.helper.DkTypeHelper;
import tool.compet.core.util.DkCollections;

class MyGrammarHelper {
    static String wrapName(String name) {
        if ("*".equals(name)) {
            return "*";
        }
        name = name.trim();
        if (name.toLowerCase().contains(" as ")) {
            String[] arr = name.split("\\s+(?i)as\\s+"); // (?i) for case-insensitive
            return wrapName(arr[0]) + " as " + wrapName(arr[1]);
        }
        if (name.contains(".")) {
            String[] arr = name.split("\\.");
            return wrapName(arr[0]) + '.' + wrapName(arr[1]);
        }
        return '`' + name + '`';
    }

    static List<String> wrapNameList(Collection<String> names) {
        List<String> items = new ArrayList<>();
        if (! DkCollections.isEmpty(names)) {
            for (String name : names) {
                items.add('`' + name + '`');
            }
        }
        return items;
    }

    static String wrapPrimitiveValue(@Nullable Object value) {
        if (value == null) {
            return null;
        }

        final String singleQuote = "'";
        final String doubleQuote = "''";

        if (value instanceof String) {
            return singleQuote + ((String) value).replace(singleQuote, doubleQuote) + singleQuote;
        }
        return singleQuote + value.toString().replace(singleQuote, doubleQuote) + singleQuote;
    }

    static List<String> wrapPrimitiveValues(Iterable values) {
        List<String> items = new ArrayList<>();
        for (Object value : values) {
            items.add(wrapPrimitiveValue(value));
        }
        return items;
    }

    static Object todbvalue(Object obj) {
        if (obj == null) {
            return null;
        }
        switch (DkTypeHelper.getTypeMasked(obj.getClass())) {
            case DkTypeHelper.TYPE_BOOLEAN_MASKED: {
                return ((boolean) obj) ? 1 : 0;
            }
            default: {
                return obj;
            }
        }
    }
}
