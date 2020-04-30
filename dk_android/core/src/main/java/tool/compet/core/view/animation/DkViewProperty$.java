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

package tool.compet.core.view.animation;

public interface DkViewProperty$ {
	String X = "x"; // left
	String Y = "y"; // top
	String ALPHA = "alpha"; // alpha of background
	String PIVOT_X = "pivotX"; // used for scale and rotation
	String PIVOT_Y = "pivotY"; // used for scale and rotation
	String SCALE_X = "scaleX"; // scale as x-axis
	String SCALE_Y = "scaleY"; // scale as y-axis
	String ROTATION = "rotation"; // 2D-rotation in Oxy
	String ROTATION_X = "rotationX"; // 3D-rotation in Ox
	String ROTATION_Y = "rotationY"; // 3D-rotation in Oy
	String BKG_COLOR = "backgroundColor"; // background color
}
