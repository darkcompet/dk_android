/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.boommenu;

import android.content.Context;

public class DkDualTextItemBuilder extends DkItemBuilder<DkDualTextItemBuilder> {
    public static final int STYLE_TEXT_LEFT = 1;
    public static final int STYLE_TEXT_RIGHT = 2;

    private int style = STYLE_TEXT_RIGHT;
    private int iconRes;
    private int textRes;
    private int subTextRes;

    private DkDualTextItemBuilder() {
    }

    public static DkDualTextItemBuilder newIns() {
        return new DkDualTextItemBuilder();
    }

    @Override
    protected DkBaseItemView getView(Context context) {
        int layoutRes;

        if (style == STYLE_TEXT_LEFT) {
            layoutRes = R.layout.item_dual_text_left;
        }
        else if (style == STYLE_TEXT_RIGHT) {
            layoutRes = R.layout.item_dual_text_right;
        }
        else {
            throw new RuntimeException("Invalid style");
        }

        DkDualTextItemView v = super.prepareView(context, layoutRes);

        if (iconRes > 0) {
            v.ivIcon.setImageResource(iconRes);
        }
        if (textRes > 0) {
            v.tvText.setText(textRes);
        }
        if (subTextRes > 0) {
            v.tvSubText.setText(subTextRes);
        }

        return v;
    }

    public DkDualTextItemBuilder setStyle(int style) {
        this.style = style;
        return this;
    }

    public DkDualTextItemBuilder setImage(int iconRes) {
        this.iconRes = iconRes;
        return this;
    }

    public DkDualTextItemBuilder setText(int textRes) {
        this.textRes = textRes;
        return this;
    }

    public DkDualTextItemBuilder setSubText(int subTextRes) {
        this.subTextRes = subTextRes;
        return this;
    }
}
