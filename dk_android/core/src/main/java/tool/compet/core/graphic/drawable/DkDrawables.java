/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.core.graphic.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.StateSet;

public class DkDrawables {
	/**
	 * Create RippleDrawable or StateListDrawable circle background which can react press action.
	 */
	public static Drawable createCircleBackground(boolean useRippleEffect, Resources resources,
		int width, int height, int normalColor, int pressedColor, int unableColor) {

		if (useRippleEffect && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			ColorStateList colorStateList = ColorStateList.valueOf(pressedColor);
			GradientDrawable content = createCircleGradientDrawable(normalColor);
			return new RippleDrawable(colorStateList, content, null);
		}
		int radius = Math.min(width >> 1, height >> 1);
		return createCircleStateListDrawable(resources, radius, normalColor, pressedColor, unableColor);
	}

	/**
	 * Create RippleDrawable or StateListDrawable rectangle background which can react press action.
	 */
	public static Drawable createRectBackground(boolean useRippleEffect, Resources resources,
		int width, int height, int normalColor, int pressedColor, int unableColor, float cornerRadius) {

		if (useRippleEffect && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			ColorStateList colorStateList = ColorStateList.valueOf(pressedColor);
			GradientDrawable content = createRectGradientDrawable(normalColor, cornerRadius);
			return new RippleDrawable(colorStateList, content, null);
		}
		return createRectStateListDrawable(resources, width, height, normalColor, pressedColor, unableColor, cornerRadius);
	}

	private static GradientDrawable createCircleGradientDrawable(int color) {
		GradientDrawable drawable = new GradientDrawable();
		drawable.setShape(GradientDrawable.OVAL);
		drawable.setCornerRadii(new float[] {0, 0, 0, 0, 0, 0, 0, 0});
		drawable.setColor(color);
		return drawable;
	}

	private static GradientDrawable createRectGradientDrawable(int color, float cornerRadius) {
		GradientDrawable drawable = new GradientDrawable();
		drawable.setShape(GradientDrawable.RECTANGLE);
		drawable.setColor(color);
		drawable.setCornerRadius(cornerRadius);
		return drawable;
	}

	private static BitmapDrawable createCircleBitmapDrawable(Resources resources, int radius, int color) {
		if (radius <= 0) {
			return null;
		}
		int diameter = radius << 1;
		Bitmap bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);

		Canvas canvas = new Canvas(bitmap);
		canvas.drawCircle(radius, radius, radius, paint);

		return new BitmapDrawable(resources, bitmap);
	}

	private static BitmapDrawable createRectBitmapDrawable(Resources resources, int width, int height,
		int color, float cornerRadius) {

		if (width <= 0 || height <= 0) {
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);

		Canvas canvas = new Canvas(bitmap);
		canvas.drawRoundRect(new RectF(0, 0, width, height), cornerRadius, cornerRadius, paint);

		return new BitmapDrawable(resources, bitmap);
	}

	private static Drawable createCircleStateListDrawable(Resources resources, int radius,
		int normalColor, int pressedColor, int unableColor) {

		StateListDrawable drawable = new StateListDrawable();

		drawable.addState(new int[] {android.R.attr.state_pressed},
			createCircleBitmapDrawable(resources, radius, pressedColor));

		drawable.addState(new int[] {-android.R.attr.state_enabled},
			createCircleBitmapDrawable(resources, radius, unableColor));

		drawable.addState(StateSet.WILD_CARD,
			createCircleBitmapDrawable(resources, radius, normalColor));

		return drawable;
	}

	private static Drawable createRectStateListDrawable(Resources resources, int width, int height,
		int normalColor, int pressedColor, int unableColor, float cornerRadius) {

		StateListDrawable drawable = new StateListDrawable();

		drawable.addState(new int[] {android.R.attr.state_pressed},
			createRectBitmapDrawable(resources, width, height, pressedColor, cornerRadius));

		drawable.addState(new int[] {-android.R.attr.state_enabled},
			createRectBitmapDrawable(resources, width, height, unableColor, cornerRadius));

		drawable.addState(StateSet.WILD_CARD,
			createRectBitmapDrawable(resources, width, height, normalColor, cornerRadius));

		return drawable;
	}
}
