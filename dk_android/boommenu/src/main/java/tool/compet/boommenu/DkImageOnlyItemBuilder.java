/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.boommenu;

import android.content.Context;

public class DkImageOnlyItemBuilder extends DkItemBuilder<DkImageOnlyItemBuilder> {
    private int iconRes;

    public DkImageOnlyItemBuilder() {
    }

    @Override
    protected DkBaseItemView getView(Context context) {
        DkImageOnlyItemView v = super.prepareView(context, R.layout.item_image_only);

        if (iconRes > 0) {
            v.ivIcon.setImageResource(iconRes);
        }

        return v;
    }

    public DkImageOnlyItemBuilder setImage(int iconRes) {
        this.iconRes = iconRes;
        return this;
    }
}
