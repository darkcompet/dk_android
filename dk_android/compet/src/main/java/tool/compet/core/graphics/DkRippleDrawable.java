/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.graphics;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tool.compet.core4j.DkMaths;

/**
 * This is compat version of Ripple animation from Lollipop (api 21+).
 */
public class DkRippleDrawable extends Drawable implements DkDrawable {
	// Hotspot is pressed-touch coordinator from user into the callback
	protected boolean requestAnimation;
	protected float hotspotX;
	protected float hotspotY;

	// Ripple animation
	private final AnimatorSet rippleEnterAnimatorSet = new AnimatorSet();
	private final AnimatorSet rippleExitAnimatorSet = new AnimatorSet();
	protected Paint ripplePaint;
	protected float rippleAnimRadius; // radius while animating
	private int rippleColor; // orginal color of ripple background
	private boolean rippleActive; // indicate ripple is active or not
	private float rippleAnimOpacity; // opacity while animating
	private long radiusAnimDuration = 225; // expand radius duration
	private long opacityAnimDuration = 225; // opacity hold duration
	private long opacityEnterAnimDuration = 75; // up opacity duration
	private long opacityExitAnimDuration = opacityAnimDuration - opacityEnterAnimDuration; // down opacity duration
	private long opacityExitAnimDelay = opacityEnterAnimDuration; // delay time before animate exit-opacity
	private float startAnimRadius; // calculated when redraw
	private float endAnimRadius; // calculated when redraw
	private long enterRippleTime; // to calculate exit-anim duration

	public DkRippleDrawable() {
		rippleColor = Color.LTGRAY;
		init();
	}

	public DkRippleDrawable(ColorStateList colorStateList) {
		rippleColor = colorStateList.getColorForState(new int[] {android.R.attr.state_pressed}, Color.LTGRAY);
		init();
	}

	private void init() {
		ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		ripplePaint.setStyle(Paint.Style.FILL);
	}

	@Override
	protected boolean onStateChange(int[] stateSet) {
		final boolean changed = super.onStateChange(stateSet);

		boolean enabled = false;
		boolean pressed = false;
		boolean focused = false;
		boolean hovered = false;

		for (int state : stateSet) {
			if (state == android.R.attr.state_enabled) {
				enabled = true;
			}
			else if (state == android.R.attr.state_focused) {
				focused = true;
			}
			else if (state == android.R.attr.state_pressed) {
				pressed = true;
			}
			else if (state == android.R.attr.state_hovered) {
				hovered = true;
			}
		}

		// Redraw here
		setRippleActive(enabled && pressed);
//		setBackgroundActive(hovered, focused, pressed);

		return changed;
	}

	private void setRippleActive(boolean active) {
		if (rippleActive != active) {
			rippleActive = active;

			if (active) {
				enterRipple();
			}
			else {
				exitRipple();
			}
		}
	}

	@Override
	public boolean setVisible(boolean visible, boolean restart) {
		final boolean changed = super.setVisible(visible, restart);

		if (! visible) {
			clearRippleAnims();
		}
		else if (changed) {
			// If we just became visible, ensure the background and ripple
			// visibilities are consistent with their internal states
			if (rippleActive) {
				enterRipple();
			}

			// Skip animations, just show the correct final states
			jumpToCurrentState();
		}

		return changed;
	}

	@Override
	public void jumpToCurrentState() {
		super.jumpToCurrentState();

		// State was changed -> jump to next state
		if (rippleEnterAnimatorSet.isRunning()) {
			rippleEnterAnimatorSet.end();
		}
		// State was changed -> jump to next state
		if (rippleExitAnimatorSet.isRunning()) {
			rippleExitAnimatorSet.end();
		}
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(@Nullable ColorFilter colorFilter) {
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setHotspot(float x, float y) {
		// It maybe not need but just give a change to super
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			super.setHotspot(x, y);
		}

		// Just remember hotspot without invalidating
		// this is touch point of action: down, move, up
		hotspotX = x;
		hotspotY = y;
	}

	// Override from api 21
	public void setHotspotBounds(int left, int top, int right, int bottom) {
		// Don't really need, but just give to super a chance to handle
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			super.setHotspotBounds(left, top, right, bottom);
		}

//		rippleBounds.set(left, top, right, bottom);
	}

	@Override
	public void getHotspotBounds(@NonNull Rect outRect) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			super.getHotspotBounds(outRect);
		}

//		rippleBounds.set(outRect);
	}

	// Own ripple drawable will change its apperance based on state
	@Override
	public boolean isStateful() {
		return true;
	}

	@Override
	public void draw(@NonNull Canvas canvas) {
		final float animRadius = DkMaths.clamp(rippleAnimRadius, startAnimRadius, endAnimRadius);

		if (animRadius > 0) {
			final Paint ripplePaint = acquireRipplePaint();
			final int originalAlpha = ripplePaint.getAlpha();
			// Plus 0.5 to round the opacity 1 unit if it has 0.5+ unit
			final int animAlpha = (int) (rippleAnimOpacity * originalAlpha + 0.5f);

			final Rect bounds = getBounds();
			final float animStartX = DkMaths.clamp(hotspotX, 0, bounds.width());
			final float animStartY = DkMaths.clamp(hotspotY, 0, bounds.height());

			ripplePaint.setAlpha(animAlpha);
			canvas.drawCircle(animStartX, animStartY, animRadius, ripplePaint);

			// Revert to previous state
			ripplePaint.setAlpha(originalAlpha);
		}
	}

	// region Private

	private Paint acquireRipplePaint() {
		Paint ripplePaint = this.ripplePaint;
		int color = this.rippleColor;
		// Grab the color for the current state and cut the alpha channel in
		// half so that the ripple and background together yield full alpha.
		if (Color.alpha(color) > 128) {
			color = (color & 0x00FFFFFF) | 0x80000000; // alpha become 128
		}

		//		int maskColor = rippleColor | 0xFF000000;
		ripplePaint.setColor(color);

//		if (rippleAnimRadius > 0) {
//			RadialGradient rippleShader = new RadialGradient(
//				hotspotX, hotspotY, rippleAnimRadius,
//				adjustAlpha(rippleColor, 0.8f), rippleColor,
//				Shader.TileMode.MIRROR
//			);
//			ripplePaint.setShader(rippleShader);
//		}

		return ripplePaint;
	}

	public int adjustAlpha(int color, float factor) {
		int alpha = Math.round(Color.alpha(color) * factor);
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		return Color.argb(alpha, red, green, blue);
	}

	// Enter (show) ripple with animation
	private void enterRipple() {
		// Complete all running enter-animations (jump them to final state) and clear them from animation set
		rippleEnterAnimatorSet.end();
		rippleEnterAnimatorSet.removeAllListeners();

		// Remember enter time
		enterRippleTime = AnimationUtils.currentAnimationTimeMillis();

		// Create radius animation
		final Rect bounds = getBounds();
		float fromRadius = calcAnimationStartRadius(bounds);
		float toRadius = calcAnimationEndRadius(hotspotX, hotspotY, bounds);
		ValueAnimator radiusAnimator = ValueAnimator.ofFloat(fromRadius, toRadius);
		radiusAnimator.setDuration(radiusAnimDuration);
		radiusAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		radiusAnimator.addUpdateListener(animation -> {
			rippleAnimRadius = (float) animation.getAnimatedValue();
			invalidateSelf();
		});

		// Create opacity animation
		float fromOpacity = 0f;
		float toOpacity = 0.25f;
		ValueAnimator opacityAnimator = ValueAnimator.ofFloat(fromOpacity, toOpacity);
		opacityAnimator.setDuration(opacityEnterAnimDuration);
		opacityAnimator.setInterpolator(new LinearInterpolator());
		opacityAnimator.addUpdateListener(animation -> {
			rippleAnimOpacity = (float) animation.getAnimatedValue();
			invalidateSelf();
		});

		// Start play them (animators) together
		rippleEnterAnimatorSet.playTogether(radiusAnimator, opacityAnimator);
		rippleEnterAnimatorSet.start();
	}

	// Exit (hide) ripple with animation
	private void exitRipple() {
		// Complete all running exit-animations (jump them to final state) and clear them from animation set
		rippleExitAnimatorSet.end();
		rippleExitAnimatorSet.removeAllListeners();

		// Delay duration until enter-anim completed
		long elapsed = AnimationUtils.currentAnimationTimeMillis() - enterRippleTime;
		long opacityExitDelay = Math.max(0, radiusAnimDuration - elapsed);

		// Hide ripple color by down opaque to zero (transparent)
		float fromOpacity = 0.25f;
		float toOpacity = 0f;
		ValueAnimator opacityAnimator = ValueAnimator.ofFloat(fromOpacity, toOpacity);
		opacityAnimator.setStartDelay(opacityExitDelay);
		opacityAnimator.setDuration(opacityExitAnimDuration);
		opacityAnimator.setInterpolator(new LinearInterpolator());
		opacityAnimator.addUpdateListener(animation -> {
			rippleAnimOpacity = (float) animation.getAnimatedValue();
			invalidateSelf();
		});

		rippleExitAnimatorSet.play(opacityAnimator);
		rippleExitAnimatorSet.start();
	}

	private void clearRippleAnims() {
		rippleEnterAnimatorSet.end();
		rippleEnterAnimatorSet.removeAllListeners();
		invalidateSelf();
	}

	// Don't start from 0, lets start from 0.6 of max(width, height)
	private float calcAnimationStartRadius(Rect bounds) {
		return (startAnimRadius = 0.6f * Math.max(bounds.width(), bounds.height()));
	}

	// Get max of distance to 4 points of bounds
	private float calcAnimationEndRadius(float hotspotX, float hotspotY, Rect bounds) {
		final int width = bounds.width();
		final int height = bounds.height();
		final float x = DkMaths.clamp(hotspotX, 0, width);
		final float y = DkMaths.clamp(hotspotY, 0, height);

		float maxDx = Math.max(x, width - x);
		float maxDy = Math.max(y, height - y);

		return (endAnimRadius = (float) Math.hypot(maxDx, maxDy));
	}

	// endregion Private

	// region Get/Set

	public void setRippleColor(int rippleColor) {
		this.rippleColor = rippleColor;
		invalidateSelf();
	}

	public int getRippleColor() {
		return rippleColor;
	}

	public long getRadiusAnimDuration() {
		return radiusAnimDuration;
	}

	public void setRadiusAnimDuration(long radiusAnimDuration) {
		this.radiusAnimDuration = radiusAnimDuration;
		invalidateSelf();
	}

	public long getOpacityEnterAnimDuration() {
		return opacityEnterAnimDuration;
	}

	public void setOpacityEnterAnimDuration(long opacityAnimDuration) {
		this.opacityEnterAnimDuration = opacityAnimDuration;
		invalidateSelf();
	}

	public long getOpacityExitAnimDuration() {
		return opacityExitAnimDuration;
	}

	// Should set value equals to: `radiusAnimDuration` - `opacityEnterAnimDuration`
	public void setOpacityExitAnimDuration(long opacityExitAnimDuration) {
		this.opacityExitAnimDuration = opacityExitAnimDuration;
		invalidateSelf();
	}

	public long getOpacityExitAnimDelay() {
		return opacityExitAnimDelay;
	}

	// Should set value equals to: `opacityEnterAnimDuration`
	public void setOpacityExitAnimDelay(long opacityExitAnimDelay) {
		this.opacityExitAnimDelay = opacityExitAnimDelay;
		invalidateSelf();
	}

	// endregion Get/Set
}
