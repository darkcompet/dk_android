/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.util;

class MySearchHelper {
    /**
     * Arrays.binarySearch() version but it does not check range.
     */
    static int binarySearch(int[] array, int size, int value) {
        int lo = 0;
        int hi = size - 1;

        while (lo <= hi) {
            final int mid = (lo + hi) >>> 1;
            final int midVal = array[mid];
            if (midVal < value) {
                lo = mid + 1;
            }
            else if (midVal > value) {
                hi = mid - 1;
            }
            else {
                // value found
                return mid;
            }
        }
        // value not present
        return ~lo;
    }

    /**
     * Arrays.binarySearch() version but it does not check range.
     */
    static int binarySearch(long[] array, int size, long value) {
        int lo = 0;
        int hi = size - 1;

        while (lo <= hi) {
            final int mid = (lo + hi) >>> 1;
            final long midVal = array[mid];
            if (midVal < value) {
                lo = mid + 1;
            }
            else if (midVal > value) {
                hi = mid - 1;
            }
            else {
                // value found
                return mid;
            }
        }
        // value not present
        return ~lo;
    }
}
