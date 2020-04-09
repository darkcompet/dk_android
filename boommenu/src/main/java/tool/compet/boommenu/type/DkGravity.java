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
 * Indicate the way how to arrange gravity of rectangle which bounds items (cluster).
 * Eg, for gravity ANCHOR_LEFT_TOP, the bottom-right point of cluster-rectangle will be
 * moved to left-top point of the anchor.
 */
public enum DkGravity {
	ANCHOR_LEFT,
	ANCHOR_RIGHT,
	ANCHOR_TOP,
	ANCHOR_BOTTOM,
	ANCHOR_LEFT_TOP,
	ANCHOR_TOP_RIGHT,
	ANCHOR_RIGHT_BOTTOM,
	ANCHOR_BOTTOM_LEFT,

	CENTER,
	CENTER_TOP,
	CENTER_BOTTOM,
	CENTER_LEFT,
	CENTER_RIGHT,

	LEFT_TOP,
	TOP_RIGHT,
	BOTTOM_LEFT,
	RIGHT_BOTTOM,
}
