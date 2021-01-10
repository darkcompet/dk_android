/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.boommenu;

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
