/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import android.content.Context;

/**
 * String utilities for Android.
 */
public class DkStrings extends tool.compet.core4j.DkStrings {
	public static String format(Context context, int format, Object... args) {
		return tool.compet.core4j.DkStrings.format(context.getString(format), args);
	}
}
