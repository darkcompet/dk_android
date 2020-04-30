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

package tool.compet.core.view.gesturedetector;

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
