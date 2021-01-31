/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.util;

import java.util.Map;

/**
 * This class, provides common basic operations on a collection.
 */
public class DkMaps {
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.size() == 0;
    }
}
