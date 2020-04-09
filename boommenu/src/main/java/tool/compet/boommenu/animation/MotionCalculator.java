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

package tool.compet.boommenu.animation;

import android.graphics.PointF;
import android.view.animation.Interpolator;

import tool.compet.boommenu.type.DkMovingShape;

public class MotionCalculator {
	public float X;
	public float Y;

	private final PointF startPos;
	private final PointF endPos;
	private final DkMovingShape movingShape;
	private final Interpolator movingInterpolator;

	public MotionCalculator(Interpolator movingInterpolator, DkMovingShape movingShape, PointF startPos, PointF endPos) {
		this.movingInterpolator = movingInterpolator;
		this.movingShape = movingShape;
		this.startPos = startPos;
		this.endPos = endPos;
	}

	public void calculateCoordinates(float fraction) {
		final float x1 = startPos.x;
		final float y1 = startPos.y;
		final float x2 = endPos.x;
		final float y2 = endPos.y;
		final float DX = x2 - x1;
		final float DY = y2 - y1;

		// Calculate X
		this.X = x1 + DX * movingInterpolator.getInterpolation(fraction);

		// Calculate Y
		switch (movingShape) {
			case LINE: {
				this.Y = y1 + DY * (X - x1) / DX;
				break;
			}
			case PARABOL_UP: {
				float x3 = (x1 + x2) / 2f;
				float y3 = Math.min(y1, y2) * 3f / 4;

				float a = ((y2 - y3) / (x2 - x3) - (y1 - y2) / (x1 - x2)) / (x3 - x1);
				float b = (y1 - y2) / (x1 - x2) - a * (x1 + x2);
				float c = y1 - x1 * (a * x1 + b);

				this.Y = (a * X + b) * X + c;
				break;
			}
			case PARABOL_DOWN: {
				float x3 = (x1 + x2) / 2f;
				float y3 = Math.max(y1, y2) * 5f / 4;

				float a = ((y2 - y3) / (x2 - x3) - (y1 - y2) / (x1 - x2)) / (x3 - x1);
				float b = (y1 - y2) / (x1 - x2) - a * (x1 + x2);
				float c = y1 - x1 * (a * x1 + b);

				this.Y = (a * X + b) * X + c;
				break;
			}
			default: {
				throw new RuntimeException("Invalid moving shape");
			}
		}
	}
}
