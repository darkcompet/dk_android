/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

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
