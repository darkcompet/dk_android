/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.graphics;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

/**
 * Support dynamic drawables for view.
 */
public class DkDrawables {
	public static Drawable loadDrawable(Context context, int resId) {
		return ResourcesCompat.getDrawable(context.getResources(), resId, null);
	}
}
