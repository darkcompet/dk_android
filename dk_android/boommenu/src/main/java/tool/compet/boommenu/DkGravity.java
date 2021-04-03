/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.boommenu;

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
