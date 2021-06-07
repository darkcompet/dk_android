/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/***
 * This supports view-compatibility for various api versions.
 */
public class DkViewCompats {
	public static void setBackground(View view, Drawable background) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { // api 16+
			view.setBackground(background);
		}
		else {
			view.setBackgroundDrawable(background);
		}
	}

	/**
	 * This provides clip path for canvas since `canvas.clipPath()` only works at api 18+.
	 *
	 * @param view View to be drawn.
	 * @param canvas Canvas of the view.
	 * @param path Path to be clipped.
	 */
	public static void clipPath(View view, Canvas canvas, Path path) {
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) { // api 17-
			view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		canvas.clipPath(path);
	}
}
