/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.boommenu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import tool.compet.boommenu.DkBaseItemView;
import tool.compet.boommenu.R;

public class DkImageOnlyItemView extends DkBaseItemView {
    ImageView ivIcon;

    public DkImageOnlyItemView(Context context) {
        super(context);
    }

    public DkImageOnlyItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DkImageOnlyItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        ivIcon = findViewById(R.id.ivIcon);

        super.onFinishInflate();
    }
}
