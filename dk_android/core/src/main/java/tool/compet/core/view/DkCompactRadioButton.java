/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * This extends compat-version and provided some optional below features:
 * - [Optional] corner-rounded view (default: true)
 * - [Optional] foreground with ripple animation
 */
public class DkCompactRadioButton extends DkCompatRadioButton {
	protected Context context;

	// Rounded corner feature
	private boolean isRoundCornerFeatureEnabled = true;
	private TheCompactComponentRoundCorner cmpRoundCorner;

	public DkCompactRadioButton(Context context) {
		super(context);
		init(context);
	}

	public DkCompactRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DkCompactRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		if (isRoundCornerFeatureEnabled()) {
			cmpRoundCorner = new TheCompactComponentRoundCorner(context);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (isRoundCornerFeatureEnabled()) {
			acquireRoundCornerComponent().onSizeChanged(w, h);
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public void draw(Canvas canvas) {
		if (isRoundCornerFeatureEnabled()) {
			acquireRoundCornerComponent().drawRoundedCorner(this, canvas);
		}
		super.draw(canvas);
	}

	public void setRoundCornerFeatureEnabled(boolean enable) {
		isRoundCornerFeatureEnabled = enable;
	}

	public boolean isRoundCornerFeatureEnabled() {
		return isRoundCornerFeatureEnabled;
	}

	// region Private

	private TheCompactComponentRoundCorner acquireRoundCornerComponent() {
		if (cmpRoundCorner == null) {
			cmpRoundCorner = new TheCompactComponentRoundCorner(context);
		}
		return cmpRoundCorner;
	}

	// endregion Private
}
