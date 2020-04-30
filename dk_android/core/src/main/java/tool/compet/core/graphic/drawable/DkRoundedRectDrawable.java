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

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DkRoundedRectDrawable extends DkDrawable {
	private final Paint paint;

	public DkRoundedRectDrawable() {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1 * metrics.scaledDensity);
		paint.setColor(Color.LTGRAY);
	}

	@Override
	public void draw(@NonNull Canvas canvas) {
		int width = getBounds().width();
		int height = getBounds().height();
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		int radius = (int) (12 * metrics.density);
		int padding = (int) (4 * metrics.density);
		int padding2 = padding << 1;

		Path border = generateBorder(padding, padding, width - padding2, height - padding2, radius, radius);
		canvas.clipPath(border);

		canvas.drawColor(Color.LTGRAY);

		canvas.drawPath(border, paint);
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(@Nullable ColorFilter colorFilter) {
	}

	@Override
	public int getOpacity() {
		return PixelFormat.OPAQUE;
	}

	private Path generateBorder(float left, float top, float right, float bottom, float rx, float ry) {
		Path path = new Path();
		float width = right - left;
		float height = bottom - top;

		rx = Math.max(0, Math.min(width / 2, rx));
		ry = Math.max(0, Math.min(height / 2, ry));

		float widthMinusCorners = (width - (2 * rx));
		float heightMinusCorners = (height - (2 * ry));

		path.moveTo(right, top + ry);
		path.rQuadTo(0, -ry, -rx, -ry); // top-right corner
		path.rLineTo(-widthMinusCorners, 0);
		path.rQuadTo(-rx, 0, -rx, ry); // top-left corner
		path.rLineTo(0, heightMinusCorners);

		path.rQuadTo(0, ry, rx, ry); // bottom-left corner
		path.rLineTo(widthMinusCorners, 0);
		path.rQuadTo(rx, 0, rx, -ry); // bottom-right corner
		path.rLineTo(0, -heightMinusCorners);

		path.close();

		return path;
	}
}
