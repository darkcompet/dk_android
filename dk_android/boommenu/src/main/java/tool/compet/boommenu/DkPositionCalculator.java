/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.boommenu;

import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Implement this class to calculate start, end position of all items.
 */
public interface DkPositionCalculator {
	/**
	 * The goal is calculate start, end positions for each item, for example:
	 * <pre><code>
	 * item.startPos.x = 100;
	 * item.startPos.y = 100;
	 * item.endPos.x = 200;
	 * item.endPos.y = 200;
	 * </code></pre>
	 * <p>
	 * By convention, start state should be simple as possible, we should take care more about end state.
	 * That is, setting for calculating start position, shape... will be omitted in this method.
	 *
	 * @param items            list of animated items
	 * @param anchor           useful for calculating start position
	 * @param board            useful for calculating end position
	 * @param shape            the figure after items boomed, maybe un-useful if you customize
	 * @param gravity          the position of the shape in board, maybe un-useful if you customize
	 * @param horizontalOffset offset which be used to move end-position of items horizontally
	 * @param verticalOffset   offset which be used to move end-position of items vertically
	 *
	 * @return [left, top, width, height] of cluster which bounds all items.
	 */
	RectF calcStartEndPositions(
		List<DkItem> items,
		View anchor, ViewGroup board,
		DkShape shape,
		DkGravity gravity,
		float horizontalOffset,
		float verticalOffset
	);
}
