/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import tool.compet.core.DkConfig;

/**
 * This extends compat-version and provided some optional below features:
 * - [Optional] corner-rounded view (default: true)
 * - [Optional] foreground with ripple animation
 */
public class DkCompactImageView extends DkCompatImageView {
	//-- For corner-rounded feature
	protected boolean roundEnabled = true;
	protected int roundColor;
	protected float roundStrokeWidth;
	protected float[] roundRadiusArr; // top-left, top-right, bottom-right, bottom-left
	protected Path clipRoundPath;
	protected Path roundPath;
	protected Paint roundPaint;

	public DkCompactImageView(Context context) {
		super(context);
		init(context);
	}

	public DkCompactImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DkCompactImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
	}

	@Override
	public void draw(Canvas canvas) {
		if (roundEnabled) {
			drawRoundedCorner(canvas);
		}

		super.draw(canvas);
	}

	// region Private

	private void initForRoundedCorner() {
		if (clipRoundPath == null) {
			clipRoundPath = new Path();
		}
		if (roundPath == null) {
			roundPath = new Path();
		}
		if (roundPaint == null) {
			final float density = DkConfig.density();
			roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			roundPaint.setStyle(Paint.Style.STROKE);

			roundColor = DkConfig.colorAccent(getContext());
			roundStrokeWidth = 1f * density;

			float roundRadius = 12 * density;
			roundRadiusArr = new float[]{
				roundRadius, roundRadius,
				roundRadius, roundRadius,
				roundRadius, roundRadius,
				roundRadius, roundRadius,
			};
		}
	}

	private void drawRoundedCorner(Canvas canvas) {
		// Init & Refresh
		initForRoundedCorner();

		// Clip path requires draw on software
		setLayerType(LAYER_TYPE_SOFTWARE, null);

		final int w = getWidth();
		final int h = getHeight();
		final float density = DkConfig.density();

		// Clip round-path
		clipRoundPath.reset();
		clipRoundPath.addRoundRect(new RectF(0, 0, w, h), roundRadiusArr, Path.Direction.CCW);

		canvas.clipPath(clipRoundPath);

		// Draw round-corner if required
		if (roundStrokeWidth >= 0f) {
			roundPaint.setColor(roundColor);
			roundPaint.setStrokeWidth(roundStrokeWidth);

			roundPath.reset();
			roundPath.addRoundRect(new RectF(density, density, w - density, h - density), roundRadiusArr, Path.Direction.CCW);

			canvas.drawPath(roundPath, roundPaint);
		}
	}

	// endregion Private

	// region Get/Set

	public boolean isRoundEnabled() {
		return roundEnabled;
	}

	public void setRoundEnabled(boolean roundEnabled) {
		this.roundEnabled = roundEnabled;
	}

	public int getRoundColor() {
		return roundColor;
	}

	public void setRoundColor(int roundColor) {
		this.roundColor = roundColor;
	}

	public float getRoundStrokeWidth() {
		return roundStrokeWidth;
	}

	/**
	 * @param roundStrokeWidth In pixel, so caller should pass value after multiplied with device density.
	 */
	public void setRoundStrokeWidth(float roundStrokeWidth) {
		this.roundStrokeWidth = roundStrokeWidth;
	}

	public float[] getRoundRadiusArr() {
		return roundRadiusArr;
	}

	public void setRoundRadiusArr(float[] roundRadiusArr) {
		this.roundRadiusArr = roundRadiusArr;
	}

	// endregion Get/Set
}
