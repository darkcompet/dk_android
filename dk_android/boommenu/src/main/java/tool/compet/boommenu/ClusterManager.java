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

import android.content.Context;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tool.compet.boommenu.type.DkEmissionOrder;
import tool.compet.boommenu.type.DkGravity;
import tool.compet.boommenu.type.DkShape;
import tool.compet.core.util.DkLogs;

/**
 * This class, manages for cluster (items).
 */
class ClusterManager {
	private static final Random rnd = new Random();

	// Emission order for each item
	DkEmissionOrder emissionOrder = DkEmissionOrder.RANDOM;

	// Cluster gravity (for calculating cluster position in board)
	DkGravity gravity = DkGravity.CENTER;

	// Cluster shape
	DkShape shape = DkShape.CIRCLE;

	// Items and builders
	List<DkItemBuilder> itemBuilders = new ArrayList<>();
	ArrayList<DkItem> items = new ArrayList<>();

	// Offset (margin) of this cluster, maybe negative.
	float horizontalOffset;
	float verticalOffset;

	// In case bounds of cluster oversize of board, scale-down will be applied
	boolean autoScaleIfOversize = true;

	// Auto make all items same size with biggest item
	boolean autoUnisize = true;

	// Allow whether or not this cluster can go outside of board
	boolean allowOutsideBoard;

	// Indicates whether each item should be started with random position inside the anchor
	boolean randomStartPosition = true;

	// To calculate start, end position for items
	DiPositionCalculator calculator;

	// Bounds of cluster in board
	private RectF bounds;

	void buildItems(Context context, View anchor, ViewGroup board, DkItemBuilder.Listener listener) {
		int anchorWidth = anchor.getWidth();
		int anchorHeight = anchor.getHeight();
		int boardWidth = board.getWidth();
		int boardHeight = board.getHeight();
		int N = itemBuilders.size();

		if (N == 0) {
			DkLogs.complain(this, "No item to build");
		}

		items.clear();

		int maxWidth = -1;
		int maxHeight = -1;

		for (int index = 0; index < N; ++index) {
			DkItemBuilder builder = itemBuilders.get(index);
			builder.index = index;
			builder.anchorWidth = anchorWidth;
			builder.anchorHeight = anchorHeight;
			builder.boardWidth = boardWidth;
			builder.boardHeight = boardHeight;
			builder.internalListener = listener;

			DkItem item = builder.build(context);

			items.add(item);

			if (autoUnisize) {
				if (maxWidth < item.width) {
					maxWidth = item.width;
				}
				if (maxHeight < item.height) {
					maxHeight = item.height;
				}
			}
		}

		if (autoUnisize) {
			for (DkItem item : items) {
				item.updateViewDimension(maxWidth, maxHeight);
			}
		}
	}

	void setupAnimation(long animStartDelay, long emissionDelayBetweenItems, long animDuration, long totalDuration) {
		final int N = items.size();

		switch (emissionOrder) {
			case NATURAL: {
				for (int i = 0; i < N; ++i) {
					items.get(i).setupAnimation(
						animStartDelay + emissionDelayBetweenItems * i,
						animDuration,
						totalDuration);
				}
				break;
			}
			case RESERVE: {
				for (int i = 0, lastIndex = N - 1; i < N; ++i) {
					items.get(i).setupAnimation(
						animStartDelay + emissionDelayBetweenItems * (lastIndex - i),
						animDuration,
						totalDuration);
				}
				break;
			}
			case RANDOM: {
				int[] indices = new int[N];

				for (int i = 0; i < N; ++i) {
					indices[i] = i;
				}

				for (int index = 0, cnt = N; index < N; ++index) {
					int nextIndex = rnd.nextInt(cnt);
					int itemIndex = indices[nextIndex];

					indices[nextIndex] = indices[--cnt];

					items.get(itemIndex).setupAnimation(
						animStartDelay + emissionDelayBetweenItems * index,
						animDuration,
						totalDuration);
				}
				break;
			}
			default: {
				DkLogs.complain(this, "Invalid emission type");
			}
		}
	}

	void calcStartEndPositions(View anchor, ViewGroup board) {
		if (calculator == null) {
			calculator = new MyPositionCalculator(randomStartPosition, autoScaleIfOversize);
		}
		bounds = calculator.calcStartEndPositions(items, anchor, board, shape, gravity, horizontalOffset, verticalOffset);
	}

	void translateItems(float dx, float dy, int bw, int bh) {
		boolean okx = false;
		boolean oky = false;

		if (allowOutsideBoard) {
			okx = oky = true;
		}
		else {
			if (bounds.left + dx >= 0 && bounds.right + dx <= bw) {
				okx = true;
				bounds.left += dx;
				bounds.right += dx;
			}
			if (bounds.top + dy >= 0 && bounds.bottom + dy <= bh) {
				oky = true;
				bounds.top += dy;
				bounds.bottom += dy;
			}
		}

		for (DkItem item : items) {
			if (okx) {
				item.view.setX(item.endPos.x += dx);
			}
			if (oky) {
				item.view.setY(item.endPos.y += dy);
			}
		}
	}
}
