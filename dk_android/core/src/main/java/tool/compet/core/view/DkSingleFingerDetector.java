/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.view;

import android.view.MotionEvent;

public class DkSingleFingerDetector {
	private float downX;
	private float downY;
	private float lastX;
	private float lastY;
	private Listener listener;

	public interface Listener {
		boolean onDown(float x, float y);

		boolean onMove(float dx, float dy);

		boolean onUp(float x, float y);

		boolean onDoubleTap();
	}

	public DkSingleFingerDetector() {
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		boolean eatEvent = false;

		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN: {
				if (listener != null) {
					listener.onDown(x, y);
				}
				eatEvent = true;
				downX = lastX = x;
				downY = lastY = y;
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				if (listener != null) {
					listener.onMove(x - lastX, y - lastY);
				}
				lastX = x;
				lastY = y;
				break;
			}
			case MotionEvent.ACTION_UP: {
				if (listener != null) {
					listener.onUp(x, y);
				}
				break;
			}
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_OUTSIDE: {
				break;
			}
		}

		return eatEvent;
	}
}
