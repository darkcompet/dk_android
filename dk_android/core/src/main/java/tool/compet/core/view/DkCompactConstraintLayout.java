/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import tool.compet.core.DkConfig;

/**
 * This extends compat-version and provided some optional below features:
 * - [Optional] corner-rounded view (default: true)
 * - [Optional] foreground with ripple animation
 */
public class DkCompactConstraintLayout extends DkCompatConstraintLayout {
	//-- For corner-rounded feature
	protected boolean roundEnabled = true;
	protected int roundColor;
	protected float roundStrokeWidth;
	protected float roundRadius; // for all (4) corners
	protected float[] roundRadiusArr; // top-left, top-right, bottom-right, bottom-left
	protected Path clipRoundPath;
	protected Path roundPath;
	protected Paint roundPaint;

	public DkCompactConstraintLayout(Context context) {
		super(context);
		init(context);
	}

	public DkCompactConstraintLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DkCompactConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (roundEnabled) {
			initForRoundedCorner(w, h);
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public void draw(Canvas canvas) {
		if (roundEnabled) {
			drawRoundedCorner(canvas);
		}

		super.draw(canvas);
	}

	// region Private

	private void initForRoundedCorner(int w, int h) {
		if (clipRoundPath == null) {
			clipRoundPath = new Path();
		}
		if (roundPath == null) {
			roundPath = new Path();
		}
		if (roundPaint == null) {
			roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			roundPaint.setStyle(Paint.Style.STROKE);

			roundColor = DkConfig.colorAccent(getContext());
			roundRadiusArr = new float[] {
				roundRadius, roundRadius,
				roundRadius, roundRadius,
				roundRadius, roundRadius,
				roundRadius, roundRadius,
			};
		}

		// Clip round-path
		clipRoundPath.reset();
		clipRoundPath.addRoundRect(new RectF(0, 0, w, h), roundRadiusArr, Path.Direction.CCW);

		final float density = DkConfig.density();
		roundPath.reset();
		roundPath.addRoundRect(new RectF(density, density, w - density, h - density), roundRadiusArr, Path.Direction.CCW);

		roundPaint.setColor(roundColor);
		roundPaint.setStrokeWidth(roundStrokeWidth);
	}

	private void drawRoundedCorner(Canvas canvas) {
		//todo Buggy: setLayerType() makes redraw called repeatly !!!
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) { // api 17-
			this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		canvas.drawPath(roundPath, roundPaint);
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
	 * @param roundStrokeWidth In dp since we will multiply it with device density.
	 */
	public void setRoundStrokeWidth(float roundStrokeWidth) {
		this.roundStrokeWidth = roundStrokeWidth * DkConfig.density();
	}

	public void setRoundRadius(float roundRadius) {
		this.roundRadius = roundRadius * DkConfig.density();
	}

	public float[] getRoundRadiusArr() {
		return roundRadiusArr;
	}

	public void setRoundRadiusArr(float[] roundRadiusArr) {
		this.roundRadiusArr = roundRadiusArr;
	}

	// endregion Get/Set
}
