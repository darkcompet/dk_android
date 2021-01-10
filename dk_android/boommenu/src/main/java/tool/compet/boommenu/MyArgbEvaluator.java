/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.boommenu;

import android.util.SparseArray;
import android.util.SparseIntArray;

import tool.compet.core.animation.DkArgbEvaluator;

class MyArgbEvaluator extends DkArgbEvaluator {
    private final int defaultDimColor;
    private final int[] defaultColorValues;
    private final int defaultLastIndex;
    private static final SparseArray<SparseIntArray> cache = new SparseArray<>();

    public MyArgbEvaluator(int defaultDimColor) {
        // assert defaultDimColor == Color.parseColor("#55000000")
        this.defaultDimColor = defaultDimColor;
        this.defaultColorValues = new int[] {
            0, 0, 150994944, 218103808, 251658240, 301989888, 335544320, 369098752, 402653184, 419430400, 452984832,
            469762048, 486539264, 520093696, 536870912, 553648128, 570425344, 587202560, 603979776, 620756992, 637534208,
            654311424, 671088640, 687865856, 704643072, 721420288, 738197504, 754974720, 771751936, 788529152, 788529152,
            805306368, 822083584, 822083584, 838860800, 855638016, 855638016, 872415232, 889192448, 905969664, 905969664,
            922746880, 922746880, 939524096, 956301312, 956301312, 973078528, 973078528, 989855744, 989855744, 1006632960,
            1006632960, 1023410176, 1023410176, 1040187392, 1040187392, 1040187392, 1056964608, 1056964608, 1073741824,
            1073741824, 1090519040, 1090519040, 1090519040, 1107296256, 1107296256, 1124073472, 1124073472, 1140850688,
            1140850688, 1140850688, 1157627904, 1157627904, 1157627904, 1174405120, 1174405120, 1174405120, 1191182336,
            1191182336, 1191182336, 1207959552, 1207959552, 1207959552, 1224736768, 1224736768, 1224736768, 1224736768,
            1241513984, 1241513984, 1258291200, 1258291200, 1258291200, 1275068416, 1275068416, 1275068416, 1275068416,
            1275068416, 1291845632, 1291845632, 1291845632, 1291845632, 1308622848, 1308622848, 1308622848, 1308622848,
            1308622848, 1325400064, 1325400064, 1325400064, 1325400064, 1325400064, 1342177280, 1342177280, 1342177280,
            1342177280, 1342177280, 1342177280, 1358954496, 1358954496, 1358954496, 1358954496, 1358954496, 1358954496,
            1375731712, 1375731712, 1375731712, 1375731712, 1375731712, 1375731712, 1375731712, 1375731712, 1392508928,
            1392508928, 1392508928, 1392508928, 1392508928, 1392508928, 1392508928, 1392508928, 1392508928, 1392508928,
            1409286144, 1409286144, 1409286144, 1409286144, 1409286144, 1409286144, 1409286144, 1409286144, 1409286144,
            1409286144, 1409286144, 1409286144, 1409286144, 1409286144, 1426063360, 1426063360, 1426063360, 1426063360,
            1426063360, 1426063360, 1426063360, 1426063360, 1426063360, 1426063360, 1426063360, 1426063360, 1426063360,
            1426063360, 1426063360, 1426063360, 1426063360, 1426063360, 1426063360
        };

        defaultLastIndex = defaultColorValues.length - 1;
    }

    @Override
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        if (endValue == defaultDimColor) {
            return defaultColorValues[Math.max(0, Math.min(defaultLastIndex, (int) (fraction * defaultLastIndex)))];
        }
        if (fraction <= 0f) {
            return startValue;
        }
        if (fraction >= 1f) {
            return endValue;
        }

        SparseIntArray colors = cache.get(endValue);

        if (colors == null) {
            colors = new SparseIntArray();
            cache.put(endValue, colors);
        }

        // We consider animation take 200 steps
        int key = (int) (fraction * 200);
        int color = colors.get(key, Integer.MIN_VALUE);

        if (color == Integer.MIN_VALUE) {
            color = super.evaluate(fraction, startValue, endValue);
            colors.put(key, color);
        }

        return color;
    }
}
