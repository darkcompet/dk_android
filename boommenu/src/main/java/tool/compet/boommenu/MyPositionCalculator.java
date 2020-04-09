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

package tool.compet.boommenu;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Random;

import tool.compet.boommenu.type.DkGravity;
import tool.compet.boommenu.type.DkShape;
import tool.compet.core.math.DkMaths;
import tool.compet.core.view.DkViews;

import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.min;
import static java.lang.Math.sin;

class MyPositionCalculator implements DiPositionCalculator {
	private static final Random rnd = new Random();
	private final boolean randomStartPosition;
	private final boolean autoScaleIfOversize;

	MyPositionCalculator(boolean randomStartPosition, boolean autoScaleIfOversize) {
		this.randomStartPosition = randomStartPosition;
		this.autoScaleIfOversize = autoScaleIfOversize;
	}

	@Override
	public RectF calcStartEndPositions(ArrayList<DkItem> items, View anchor, ViewGroup board,
		DkShape shape, DkGravity gravity, float horizontalOffset, float verticalOffset) {
		// Calculate size for cluster, and positions of each item in the bounds
		float[] size = calcBoundsSizeAndItemsPositionInBounds(items, shape);

		// Scale down each item size to fit cluster inside the board
		if (autoScaleIfOversize) {
			float scaleFactor = 1f;
			float left = horizontalOffset;
			float right = board.getWidth();
			float top = verticalOffset;
			float bottom = board.getHeight();
			final Rect offset = DkViews.getOffsetFromDescendantToAncestor(anchor, board);

			switch (gravity) {
				case ANCHOR_LEFT: {
					right = offset.left;
					break;
				}
				case ANCHOR_TOP: {
					bottom = offset.top;
					break;
				}
				case ANCHOR_RIGHT: {
					left = offset.left + anchor.getWidth();
					break;
				}
				case ANCHOR_BOTTOM: {
					top = offset.top + anchor.getHeight();
					break;
				}
				case ANCHOR_LEFT_TOP: {
					right = offset.left;
					bottom = offset.top;
					break;
				}
				case ANCHOR_TOP_RIGHT: {
					bottom = offset.top;
					left = offset.left + anchor.getWidth();
					break;
				}
				case ANCHOR_RIGHT_BOTTOM: {
					left = offset.left + anchor.getWidth();
					top = offset.top + anchor.getHeight();
					break;
				}
				case ANCHOR_BOTTOM_LEFT: {
					top = offset.top + anchor.getHeight();
					right = offset.left;
					break;
				}
			}

			if (size[0] > right - left) {
				scaleFactor = size[0] / (right - left);
			}
			if (size[1] > bottom - top) {
				scaleFactor = Math.max(scaleFactor, size[1] / (bottom - top));
			}

			if (scaleFactor > 1f) {
				for (DkItem item : items) {
					item.width /= scaleFactor;
					item.height /= scaleFactor;
					item.margin /= scaleFactor;
				}

				size = calcBoundsSizeAndItemsPositionInBounds(items, shape);
			}
		}

		// Calculate start, end positions in board for items
		float[] offset = calcItemPositionsInBoard(items, anchor, board, gravity, size, horizontalOffset, verticalOffset);

		return new RectF(offset[0], offset[1], offset[0] + size[0], offset[1] + size[1]);
	}

	private float[] calcBoundsSizeAndItemsPositionInBounds(ArrayList<DkItem> items, DkShape shape) {
		final float[] size = new float[2];
		final int N = items.size();

		switch (shape) {
			case HORIZONTAL_LINE: {
				int curLeft = 0;
				int maxHeight = 0;

				for (DkItem item : items) {
					curLeft += item.margin;

					item.endPos.x = curLeft;
					item.endPos.y = item.margin;

					curLeft += item.width + item.margin;
					maxHeight = Math.max(maxHeight, item.height + (item.margin << 1));
				}

				size[0] = curLeft;
				size[1] = maxHeight;

				return size;
			}
			case VERTICAL_LINE: {
				int maxWidth = 0;
				int curTop = 0;

				for (DkItem item : items) {
					curTop += item.margin;

					item.endPos.x = item.margin;
					item.endPos.y = curTop;

					curTop += item.height + item.margin;
					maxWidth = Math.max(maxWidth, item.width + (item.margin << 1));
				}

				size[0] = maxWidth;
				size[1] = curTop;

				return size;
			}
			case CIRCLE:
			case CIRCLE_AND_CENTER: {
				if (N <= 1) {
					DkItem center = items.get(0);
					float maxItemRadius = (float) (hypot(center.width, center.height) / 2);
					float maxMargin = center.margin;

					// Radius of outer-circle which bounds item
					float R2 = maxItemRadius + maxMargin;

					size[0] = size[1] = (float) (2.0 * R2);

					// Calculate end-position in bounds
					center.endPos.x = R2 - center.margin;
					center.endPos.y = R2 - center.margin;
				}
				else {
					// For beauty, we consider bounds of items as a Square
					final boolean hasCenter = shape == DkShape.CIRCLE_AND_CENTER;
					// Radius of inner-circle which through all items center
					float R1 = 0;
					// Radius of outer-circle which bounds all items, of course R2 > R1
					float R2;

					// We consider each item as a circle
					float[] item_radius = new float[N];
					float maxItemRadius = 0;
					float maxMargin = 0;
					int biggestIndex = 0;

					// Estimate max-margin, max-item-radius...
					for (int i = 0; i < N; ++i) {
						DkItem item = items.get(i);
						item_radius[i] = estimateItemRadius(item);

						if (item_radius[i] + item.margin >= maxMargin + maxItemRadius) {
							if (hasCenter && i > 0) {
								biggestIndex = i;
							}
						}

						maxMargin = Math.max(maxMargin, item.margin);
						maxItemRadius = Math.max(maxItemRadius, item_radius[i]);
					}

					// In bounds: calculate inner circle radius R1
					float teta = 360f / (hasCenter ? N - 1 : N);
					float tmp = (float) Math.sqrt(2 * (1 - DkMaths.cos(teta)));

					for (int i = hasCenter ? 1 : 0; i < N; ++i) {
						int next = i + 1;

						if (next == N) {
							next = hasCenter ? 1 : 0;
						}
						DkItem it1 = items.get(i);
						DkItem it2 = items.get(next);
						R1 = Math.max(R1, (item_radius[i] + item_radius[next] + it1.margin + it2.margin) / tmp);
					}
					// try to make R1 bigger if has center (note that, centerIndex = 0)
					if (hasCenter) {
						DkItem center = items.get(0);
						DkItem biggest = items.get(biggestIndex);

						R1 += Math.max(0, item_radius[biggestIndex] + biggest.margin + item_radius[0] + center.margin - R1);
					}

					// In bounds: calculate outer circle radius R2
					R2 = R1 + maxItemRadius + maxMargin;
					size[0] = size[1] = (float) (2.0 * R2);

					// Calculate end-position in bounds
					final float angle = hasCenter ? 360f / (N - 1) : 360f / N;

					if (hasCenter) {
						DkItem center = items.get(0);
						center.endPos.x = R2 - (center.width >> 1);
						center.endPos.y = R2 - (center.height >> 1);
					}

					for (int i = hasCenter ? 1 : 0; i < N; ++i) {
						DkItem item = items.get(i);
						float degrees = i * angle;
						item.endPos.x = (float) (R2 + R1 * DkMaths.sin(degrees) - (item.width >> 1));
						item.endPos.y = (float) (R2 - R1 * DkMaths.cos(degrees) - (item.height >> 1));
					}
				}

				return size;
			}
			case GRID_2_COLS:
			case GRID_3_COLS:
			case GRID_4_COLS:
			case GRID_5_COLS:
			case GRID_6_COLS:
			case GRID_7_COLS:
			case GRID_8_COLS:
			case GRID_9_COLS:
			case GRID_10_COLS: {
				int col = shape.getVal();
				int row = N / col;

				if (col * row < N) {
					++row;
				}

				int maxWidth = 0;
				int curTop = 0;

				for (int r = 0; r < row; ++r) {
					int curLeft = 0;
					int maxHeight = 0;

					for (int c = 0; c < col; ++c) {
						int i = r * col + c;

						if (i >= N) {
							continue;
						}

						DkItem item = items.get(i);
						curLeft += item.margin;

						item.endPos.x = curLeft;
						item.endPos.y = curTop + item.margin;

						curLeft += item.width + item.margin;
						maxHeight = Math.max(maxHeight, item.height + (item.margin << 1));
					}

					maxWidth = Math.max(maxWidth, curLeft);
					curTop += maxHeight;
				}

				size[0] = maxWidth;
				size[1] = curTop;

				return size;
			}
			case QUARTER_LEFT:
			case QUARTER_RIGHT:
			case QUARTER_TOP:
			case QUARTER_BOTTOM:
			case QUARTER_LEFT_TOP:
			case QUARTER_TOP_RIGHT:
			case QUARTER_RIGHT_BOTTOM:
			case QUARTER_BOTTOM_LEFT: {
				final DkItem mock = items.get(0);

				if (N == 1) {
					mock.endPos.x = mock.endPos.y = mock.margin;
					size[0] = mock.width + (mock.margin << 1);
					size[1] = mock.height + (mock.margin << 1);
				}
				else {
					// To make life easer, we suppose all item has same radius and margin
					final double teta = Math.PI / (1 + (N << 1));
					final double half_teta = teta / 2;
					final double base_teta = (Math.PI / 2 - N * teta) / 2;
					final float item_radius = estimateItemRadius(mock);

					// Center to center
					float R_cc = (float) (4 * (mock.margin + N * (item_radius + mock.margin)) / Math.PI);

					size[0] = size[1] = R_cc + item_radius + mock.margin;

					double angle = base_teta;
					double bx = 0;
					double by = 0;
					double fx = R_cc;
					double fy = R_cc;
					boolean xsin = true;
					boolean ycos = true;

					// Code-volume optimization for end-position calculation at next step
					if (shape == DkShape.QUARTER_LEFT) {
						size[1] = (float) (size[0] * Math.sqrt(2));
						angle += Math.PI / 4;
						bx = size[0];
						by = size[1] / 2;
						fx = -R_cc;
					}
					else if(shape == DkShape.QUARTER_RIGHT) {
						size[1] = (float) (size[0] * Math.sqrt(2));
						angle += Math.PI / 4;
						by = size[1] / 2;
						fy = -R_cc;
					}
					else if(shape == DkShape.QUARTER_TOP) {
						size[0] = (float) (size[1] * Math.sqrt(2));
						angle += Math.PI / 4;
						bx = size[0] / 2;
						by = size[1];
						fx = -R_cc;
						fy = -R_cc;
						xsin = ycos = false;
					}
					else if(shape == DkShape.QUARTER_BOTTOM) {
						size[0] = (float) (size[1] * Math.sqrt(2));
						angle += Math.PI / 4;
						bx = size[0] / 2;
						xsin = ycos = false;
					}
					else if(shape == DkShape.QUARTER_LEFT_TOP) {
						bx = size[0];
						by = size[1];
						fx = -R_cc;
						fy = -R_cc;
						xsin = ycos = false;
					}
					else if(shape == DkShape.QUARTER_TOP_RIGHT) {
						by = size[1];
						fy = -R_cc;
					}
					else if(shape == DkShape.QUARTER_RIGHT_BOTTOM) {
						xsin = ycos = false;
					}
					else if(shape == DkShape.QUARTER_BOTTOM_LEFT) {
						bx = size[0];
						fx = -R_cc;
					}
					else {
						throw new RuntimeException("Invalid shape");
					}

					// Calculate end-position for each item
					for (DkItem item : items) {
						double curAngle = angle + half_teta;

						item.endPos.x = (float) (bx + fx * (xsin ? sin(curAngle) : cos(curAngle)) - (item.width >> 1));
						item.endPos.y = (float) (by + fy * (ycos ? cos(curAngle) : sin(curAngle)) - (item.height >> 1));

						angle += teta;
					}
				}

				return size;
			}
			default: {
				throw new RuntimeException("Invalid shape: " + shape);
			}
		}
	}

	private float estimateItemRadius(DkItem item) {
		int half_w = item.width >> 1;
		int half_h = item.height >> 1;

		return (float) ((min(half_w, half_h) + hypot(half_w, half_h)) / 2);
	}

	private float[] calcItemPositionsInBoard(ArrayList<DkItem> items, View anchor, ViewGroup board, DkGravity gravity,
		float[] bounds, float horizontalOffset, float verticalOffset) {

		final int bw = board.getWidth();
		final int bh = board.getHeight();
		final Rect offset = DkViews.getOffsetFromDescendantToAncestor(anchor, board);
		float left = horizontalOffset;
		float top = verticalOffset;

		// First, calculate end-position in bounds
		switch (gravity) {
			case CENTER: {
				left += (bw - bounds[0]) / 2f;
				top += (bh - bounds[1]) / 2f;
				break;
			}
			case CENTER_TOP: {
				left += (bw - bounds[0]) / 2f;
				break;
			}
			case CENTER_BOTTOM: {
				left += (bw - bounds[0]) / 2f;
				top += bh - bounds[1];
				break;
			}
			case CENTER_LEFT: {
				top += (bh - bounds[1]) / 2f;
				break;
			}
			case CENTER_RIGHT: {
				left += bw - bounds[0];
				top += (bh - bounds[1]) / 2f;
				break;
			}
			case LEFT_TOP: {
				break;
			}
			case TOP_RIGHT: {
				left += bw - bounds[0];
				break;
			}
			case BOTTOM_LEFT: {
				top += bh - bounds[1];
				break;
			}
			case RIGHT_BOTTOM: {
				left += bw - bounds[0];
				top += bh - bounds[1];
				break;
			}
			case ANCHOR_LEFT: {
				left += offset.left - bounds[0];
				top += offset.top + (anchor.getHeight() - bounds[1]) / 2f;
				break;
			}
			case ANCHOR_RIGHT: {
				left += offset.left + anchor.getWidth();
				top += offset.top + (anchor.getHeight() - bounds[1]) / 2f;
				break;
			}
			case ANCHOR_TOP: {
				left += offset.left + (anchor.getWidth() - bounds[0]) / 2f;
				top += offset.top - bounds[1];
				break;
			}
			case ANCHOR_BOTTOM: {
				left += offset.left + (anchor.getWidth() - bounds[0]) / 2f;
				top += offset.top + anchor.getHeight();
				break;
			}
			case ANCHOR_LEFT_TOP: {
				left += offset.left - bounds[0];
				top += offset.top - bounds[1];
				break;
			}
			case ANCHOR_TOP_RIGHT: {
				left += offset.left + anchor.getWidth();
				top += offset.top - bounds[1];
				break;
			}
			case ANCHOR_RIGHT_BOTTOM: {
				left += offset.left + anchor.getWidth();
				top += offset.top + bounds[1];
				break;
			}
			case ANCHOR_BOTTOM_LEFT: {
				left += offset.left - bounds[0];
				top += offset.top + anchor.getHeight();
				break;
			}
			default: {
				throw new RuntimeException("Invalid gravity: " + gravity);
			}
		}

		// Finally, calculate start position and end positions in board
		final int half_aw = anchor.getWidth() >> 1;
		final int half_ah = anchor.getHeight() >> 1;

		for (DkItem item : items) {
			item.startPos.x = offset.left + half_aw - (item.width >> 1);
			item.startPos.y = offset.top + half_ah - (item.height >> 1);
			item.endPos.x += left;
			item.endPos.y += top;

			if (randomStartPosition) {
				float scaleFactor = item.startScaleFactor;
				int startHalfWidth = (int) (scaleFactor * item.width / 2f);
				int startHalfHeight = (int) (scaleFactor * item.height / 2f);

				float diff_x = rnd.nextInt(Math.max(0, half_aw - startHalfWidth));
				item.startPos.x += rnd.nextBoolean() ? diff_x : -diff_x;

				float diff_y = rnd.nextInt(Math.max(0, half_ah - startHalfHeight));
				item.startPos.y += rnd.nextBoolean() ? diff_y : -diff_y;
			}
		}

		return new float[] {left, top};
	}
}
