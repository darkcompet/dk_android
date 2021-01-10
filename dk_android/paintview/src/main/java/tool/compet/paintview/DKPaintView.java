/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.paintview;

import android.content.Context;
import android.graphics.Canvas;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DKPaintView extends View {
	private int mBoardWidth;
	private int mBoardHeight;
	private boolean mPassTouchEvent;

	public DKPaintView(Context context) {
		super(context);
	}

	public DKPaintView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public DKPaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mBoardWidth = w;
		mBoardHeight = h;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mPassTouchEvent) return false;

		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN: {
				break;
			}
			case MotionEvent.ACTION_UP: {
				break;
			}
			default: {
			}
		}
		return super.onTouchEvent(event);
	}

	public DKPaintView setPassTouchEvent(boolean pass) {
		mPassTouchEvent = pass;
		return this;
	}
}
