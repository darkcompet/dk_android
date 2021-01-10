/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.color;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * Utility class, provides common basic operations for color.
 */
public class DkColors {
    public static int toSemiColor(int color) {
        return Color.argb(128, (color >> 16) & 0xff, (color >> 8) & 0xff, (color) & 0xff);
    }

    public static int toSemiColor(@NonNull String color) {
        return Color.parseColor(color.replace("#", "#80"));
    }

    public static String toHex(int color) {
//		return "#" + Integer.toHexString(color);
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public static int getColor(View view, int resId, Resources.Theme theme) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return view.getResources().getColor(resId);
        }
        else {
            return view.getResources().getColor(resId, theme);
        }
    }

    public static int getColor(TypedArray typedArray, int resId, Resources.Theme theme) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return typedArray.getResources().getColor(resId);
        }
        else {
            return typedArray.getResources().getColor(resId, theme);
        }
    }

    public static int getColor(View view, int resId) {
        return getColor(view, resId, null);
    }

    public static int getColor(TypedArray typedArray, int resId) {
        return getColor(typedArray, resId, null);
    }

    public static int getColor(Context context, int resId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return context.getResources().getColor(resId);
        }
        return context.getResources().getColor(resId, null);
    }

    public static int getColor(TypedArray typedArray, int resId, int defaultId) {
        return typedArray.getColor(resId, getColor(typedArray, defaultId));
    }

    public static int getColor(Context context, int resId, int color) {
        return resId == 0 ? color : getColor(context, resId);
    }
}
