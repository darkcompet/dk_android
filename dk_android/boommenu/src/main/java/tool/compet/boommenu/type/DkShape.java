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

package tool.compet.boommenu.type;

/**
 * Indicate the way to place menu items after boomed.
 */
public enum DkShape {
	RANDOM(0),

	HORIZONTAL_LINE(0),
	VERTICAL_LINE(0),

	// We consider each item as circle which has radius is min(item.width / 2, item.height / 2)
	CIRCLE(0),
	CIRCLE_AND_CENTER(0),

	GRID_2_COLS(2),
	GRID_3_COLS(3),
	GRID_4_COLS(4),
	GRID_5_COLS(5),
	GRID_6_COLS(6),
	GRID_7_COLS(7),
	GRID_8_COLS(8),
	GRID_9_COLS(9),
	GRID_10_COLS(10),

	// We consider each item as circle which has radius is min(item.width / 2, item.height / 2)
	QUARTER_LEFT(0),
	QUARTER_LEFT_TOP(45),
	QUARTER_TOP_RIGHT(135),
	QUARTER_TOP(90),
	QUARTER_RIGHT(180),
	QUARTER_RIGHT_BOTTOM(225),
	QUARTER_BOTTOM(270),
	QUARTER_BOTTOM_LEFT(315);

	private final int val;

	DkShape(int val) {
		this.val = val;
	}

	public int getVal() {
		return val;
	}
}
