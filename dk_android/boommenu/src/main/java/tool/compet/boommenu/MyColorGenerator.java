/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.boommenu;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Random;

import tool.compet.core.log.DkLogs;

class MyColorGenerator {
    private static final Random rnd = new Random();
    private static final int[] colors = new int[72];

    static {
        float[] hsl = new float[]{0f, 1f, 0.5f};

        for (int i = colors.length - 1; i >= 0; --i) {
            hsl[0] = (i << 2) + i;
            hsl[1] = (i & 1) == 0 ? 0.2f : 0.8f;
            colors[i] = Color.HSVToColor(hsl);
        }
    }

    private final ArrayList<Integer> usableColors = new ArrayList<>(colors.length);

    public MyColorGenerator() {
        for (int color : colors) {
            usableColors.add(color);
        }
    }

    public int nextNormalColor() {
        ArrayList<Integer> usableColors = this.usableColors;
        final int N = usableColors.size();

        if (N == 0) {
            DkLogs.complain(this, "You can generate at most %d colors", colors.length);
        }

        int nextIndex = rnd.nextInt(N);
        int color = usableColors.get(nextIndex);
        int lastIndex = N - 1;

        usableColors.set(nextIndex, usableColors.get(lastIndex));
        usableColors.remove(lastIndex);

        return color;
    }

    public int getPressedColor(int normalColor) {
        if (normalColor == Color.TRANSPARENT) {
            normalColor = nextNormalColor();
        }

        float[] hsv = new float[3];
        Color.colorToHSV(normalColor, hsv);

        hsv[2] = 0.2f;

        return Color.HSVToColor(hsv);
    }

    public int getUnableColor(int normalColor) {
        if (normalColor == Color.TRANSPARENT) {
            normalColor = nextNormalColor();
        }

        float[] hsv = new float[3];
        Color.colorToHSV(normalColor, hsv);

        hsv[2] = 0.8f;

        return Color.HSVToColor(hsv);
    }
}
