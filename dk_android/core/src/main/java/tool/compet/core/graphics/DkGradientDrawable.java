/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.graphics;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;

public class DkGradientDrawable extends GradientDrawable {
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		Rect bounds = getBounds();
	}

	public DkGradientDrawable xsetColor(int argb) {
		super.setColor(argb);
		return this;
	}
}
