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

package tool.compet.boommenu.helper;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class MyGestureDetector {
	public interface Listener {
		boolean onTranslate(float dx, float dy);
		boolean onClick(float rawX, float rawY);
	}

	private float downX;
	private float downY;
	private float lastX;
	private float lastY;
	private Listener listener;

	private final int touchSlop2;
	private boolean clickable;
	private boolean movable;

	public MyGestureDetector(Context context) {
		int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		touchSlop2 = touchSlop * touchSlop;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public boolean onTouchEvent(View v, MotionEvent event) {
		float rawX = event.getRawX();
		float rawY = event.getRawY();
		boolean eatEvent = false;

		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN: {
				clickable = true;
				movable = false;
				downX = lastX = rawX;
				downY = lastY = rawY;
				eatEvent = true;
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				if (movable) {
					if (listener != null) {
						eatEvent = listener.onTranslate(rawX - lastX, rawY - lastY);
					}
					lastX = rawX;
					lastY = rawY;
				}
				else {
					float dx = rawX - downX;
					float dy = rawY - downY;

					if (dx * dx + dy * dy >= touchSlop2) {
						movable = true;
						clickable = false;
						lastX = rawX;
						lastY = rawY;
					}
				}
				break;
			}
			case MotionEvent.ACTION_UP: {
				if (clickable && listener != null) {
					eatEvent = listener.onClick(rawX, rawY);
				}
				break;
			}
		}

		return eatEvent;
	}
}
