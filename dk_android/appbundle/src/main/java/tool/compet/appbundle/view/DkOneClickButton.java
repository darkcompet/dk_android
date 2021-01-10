/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.view;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import android.util.AttributeSet;
import android.view.View;

/**
 * This class prevents fast continuous clicking. Next click
 * will be available again after specific duration elapsed.
 */
public class DkOneClickButton extends AppCompatButton implements View.OnClickListener {
    private long lastClickTime;
    private long duration = 1000L;
    private OnClickListener onClickListener;

    public DkOneClickButton(Context context) {
        super(context);
    }

    public DkOneClickButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DkOneClickButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.setOnClickListener(this);
        super.onFinishInflate();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener clickListener) {
        this.onClickListener = clickListener;
    }

    @Override
    public void onClick(View view) {
        long now = System.currentTimeMillis();

        if (now - lastClickTime >= duration) {
            lastClickTime = now;

            if (onClickListener != null) {
                onClickListener.onClick(this);
            }
        }
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
