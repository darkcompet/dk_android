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

package tool.compet.core.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import tool.compet.core.graphic.drawable.DkShadowDrawable;

// https://github.com/hdodenhof/CircleImageView/blob/master/circleimageview/src/main/java/de/hdodenhof/circleimageview/CircleImageView.java
public class DkCircleImageView extends AppCompatImageView {
	private int radius;
	private Path circlePath;
	private DkShadowDrawable shadowDrawable;
	private int cx;
	private int cy;

	public DkCircleImageView(Context context) {
		this(context, null, 0);
	}

	public DkCircleImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DkCircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		circlePath = new Path();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		cx = w >> 1;
		cy = h >> 1;

		circlePath.addCircle(cx, cy, radius, Path.Direction.CCW);

		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.clipPath(circlePath);

		super.onDraw(canvas);
	}
}
