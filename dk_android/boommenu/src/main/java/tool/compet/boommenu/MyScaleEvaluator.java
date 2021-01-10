/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.boommenu;

import android.view.animation.Interpolator;

import tool.compet.core.animation.DkInterpolatorProvider;

class MyScaleEvaluator {
    private final float startValue;
    private final float endValue;
    private final Interpolator scaleInterpolator;

    public MyScaleEvaluator(float startValue, float endValue) {
        this.startValue = startValue;
        this.endValue = endValue;
        this.scaleInterpolator = DkInterpolatorProvider.newCubicOut(true);
    }

    public float getAnimatedValue(float fraction) {
        return startValue + scaleInterpolator.getInterpolation(fraction) * (endValue - startValue);
    }
}
