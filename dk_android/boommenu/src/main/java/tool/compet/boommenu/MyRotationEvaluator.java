/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.boommenu;

import android.view.animation.Interpolator;

import androidx.core.view.animation.PathInterpolatorCompat;

class MyRotationEvaluator {
    private final float startAngle;
    private final float endAngle;
    private final Interpolator rotationInterpolator;

    public MyRotationEvaluator(float startAngle, float endAngle) {
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.rotationInterpolator = PathInterpolatorCompat.create(
            0.22f,
            1.51f,
            0.84f,
            1.38f);
    }

    public float getAnimatedValue(float fraction) {
        return startAngle + rotationInterpolator.getInterpolation(fraction) * (endAngle - startAngle);
    }
}
