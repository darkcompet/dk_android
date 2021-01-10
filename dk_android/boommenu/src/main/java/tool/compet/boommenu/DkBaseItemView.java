/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.boommenu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;

import tool.compet.core.graphic.DkDrawables;

/**
 * Base item view which be used in DkItemBuilder.getView().
 * For customize view, you must extend this class and provide settings via builder class.
 */
public class DkBaseItemView extends ConstraintLayout implements View.OnTouchListener {
    boolean isCircleShape;
    float cornerRadius;
    int normalColor;
    int pressedColor;
    int unableColor;
    boolean useRippleEffect;

    MyGestureDetector detector;

    public DkBaseItemView(Context context) {
        this(context, null);
    }

    public DkBaseItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DkBaseItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        detector = new MyGestureDetector(context);
        setOnTouchListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Drawable background = isCircleShape ?
            DkDrawables.createCircleBackground(
                useRippleEffect,
                getResources(),
                w,
                h,
                normalColor,
                pressedColor,
                unableColor) :
            DkDrawables.createRectBackground(
                useRippleEffect,
                getResources(),
                w,
                h,
                normalColor,
                pressedColor,
                unableColor,
                cornerRadius
            );

        ViewCompat.setBackground(this, background);

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        detector.onTouchEvent(v, event);
        return super.onTouchEvent(event);
    }
}
