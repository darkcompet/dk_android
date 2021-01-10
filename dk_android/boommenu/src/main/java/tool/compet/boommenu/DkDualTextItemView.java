/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.boommenu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import tool.compet.boommenu.DkBaseItemView;
import tool.compet.boommenu.R;

public class DkDualTextItemView extends DkBaseItemView {
    ImageView ivIcon;
    TextView tvText;
    TextView tvSubText;

    public DkDualTextItemView(Context context) {
        super(context);
    }

    public DkDualTextItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DkDualTextItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        ivIcon = findViewById(R.id.ivIcon);
        tvText = findViewById(R.id.tvText);
        tvSubText = findViewById(R.id.tvSubText);

        tvText.setSelected(true);
        tvSubText.setSelected(true);

        super.onFinishInflate();
    }
}
