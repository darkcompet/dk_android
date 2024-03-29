/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.compassview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import tool.compet.R;
import tool.compet.core.DkConfig;
import tool.compet.core.DkLogcats;
import tool.compet.core4j.DkMaths;
import tool.compet.core.graphics.DkBitmaps;
import tool.compet.core.graphics.DkColors;
import tool.compet.stream.DkObservable;
import tool.compet.core.view.DkDoubleFingerDetector;

import static tool.compet.BuildConfig.DEBUG;
import static tool.compet.core.view.DkViews.getTextViewDrawPoint;

public class DkCompassView extends View implements View.OnTouchListener {
	// Compass modes: normal, rotator and pointer.
	public static final int MODE_NORMAL = 0;
	public static final int MODE_ROTATE = 1;
	public static final int MODE_POINT = 2;

	private static final float DEFAULT_WORD_TEXT_SIZE = 12 * DkConfig.scaledDensity();

	private final Context context;

	private final MyCompassController controller;
	private DkDoubleFingerDetector gestureDetector;
	private ValueAnimator countdownAnimator;
	private DkCompassView.Listener listener;
	private int compassMode = MODE_NORMAL;
	private final Rect tmpRect = new Rect();

	// Board
	private int boardWidth;
	private int boardHeight;
	private int oldBoardWidth;
	private int oldBoardHeight;
	private int boardCenterX;
	private int boardCenterY;
	private int boardInnerRadius;

	// Compass
	private Bitmap compass;
	private boolean isRequestBuildCompass;
	private boolean isBuildingCompass;
	private boolean isFitCompassInsideBoard;
	private boolean isBoardSizeChanged;
	private int compassColor;
	private int compassSemiColor;
	private final Matrix compassBitmapMatrix = new Matrix();
	private float compassCx, compassCy;
	private double compassDegreesInNormalMode;
	private double compassDegreesInRotateMode;
	private double compassDegreesInPointMode;
	private float compassBitmapZoomLevel = 1f;
	private boolean isCompassMovable = true;
	private double nextAnimateDegrees;
	private double lastAnimatedDegrees;
	private List<DkCompassRing> buildCompassRings;
	private Locale buildCompassLocale;

	private final Paint compassPaint;
	private final Paint linePaint;
	private final Paint fillPaint;
	private final Paint textPaint;

	// North, East, South, West poles
	private String[] poleShortNames;
	private String[] poleLongNames;

	// 24 lines
	private boolean isShow24PointerLines;

	// Rotator (circle + arrow)
	private int handlerColor;
	private boolean isShowRotator = true;
	private float distFromRotatorCenterToBoardCenter;
	private double rotatorRadius;
	private double rotatorRotatedDegrees;
	private boolean isTouchInsideRotator;
	private long rotatorDisabledCountDown = 800;
	private double rotationFactor = 0.1;

	// Pointer
	private double pointerDegrees;
	private boolean isShowPointer = true;
	private float pointerStopY;

	// Count down for centerize compass
	private boolean hasCmpCenterLeftCountDown;

	// Navigation Ox, Oy
	private int naviArrowStartY, naviArrowStopY;
	private Path naviArrow;

	// Touch event
	private double touchStartDegrees;
	private long lastStopTime;
	private boolean isAdjustCompass = false;

	// Highlight, move compass
	private final int MSG_ALLOW_MOVE_COMPASS = 1;
	private final int MSG_TURN_OFF_HIGH_LIGHT = 2;
	private final Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_ALLOW_MOVE_COMPASS: {
					isCompassMovable = true;
					break;
				}
				case MSG_TURN_OFF_HIGH_LIGHT: {
					handlerColor = compassSemiColor;
					invalidate();
					break;
				}
			}
		}
	};

	public interface Listener {
		void onRotatorElapsed(double delta);

		void onPointerChanged(double pointerDegreesOnNotRotatedCompass);
	}

	public DkCompassView(Context context) {
		this(context, null);
	}

	public DkCompassView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DkCompassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		this.context = context;

		controller = new MyCompassController();

		countdownAnimator = new ValueAnimator();
		countdownAnimator.setDuration(1000);

		compassPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		compassPaint.setColorFilter(new PorterDuffColorFilter(compassColor, PorterDuff.Mode.SRC_ATOP));

		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setStrokeWidth(3);
		linePaint.setStyle(Paint.Style.STROKE);

		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(DEFAULT_WORD_TEXT_SIZE);

		fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		fillPaint.setStrokeWidth(3);
		fillPaint.setStyle(Paint.Style.FILL_AND_STROKE);

		gestureDetector = new DkDoubleFingerDetector(context, gestureDetectorListener);

		setOnTouchListener(this);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		isBoardSizeChanged = true;
		boardWidth = w;
		boardHeight = h;
		oldBoardWidth = oldw;
		oldBoardHeight = oldh;

		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Build compass if we got a request before
		if (isRequestBuildCompass) {
			isRequestBuildCompass = false;
			buildCompassActual(buildCompassLocale);
			return;
		}
		// Wait until compass is built
		if (isBuildingCompass) {
			return;
		}

		// Fit compass inside board
		// omit event for layout-size-change
		if (isFitCompassInsideBoard) {
			isFitCompassInsideBoard = false;
			isBoardSizeChanged = false;
			updateMaterialsOnBoardSizeChanged();
			fitCompassInsideBoard();
		}

		// Translate compass
		if (isBoardSizeChanged) {
			isBoardSizeChanged = false;
			updateMaterialsOnBoardSizeChanged();
			float newCx = compassCx * boardWidth / oldBoardWidth;
			float newCy = compassCy * boardHeight / oldBoardHeight;
			postTranslateCompassMatrix(newCx - compassCx, newCy - compassCy);
		}

		int boardCx = boardCenterX, boardCy = boardCenterY;
		float handlerRadius = (float) this.rotatorRadius;
		float handlerToCenter = distFromRotatorCenterToBoardCenter;
		double animateDegrees = nextAnimateDegrees;

		// Reset before draw
		linePaint.setColor(compassColor);
		fillPaint.setColor(compassColor);

		// Draw Ox, Oy navigation-axis
		canvas.drawLine(boardCx, naviArrowStartY, boardCx, naviArrowStopY, linePaint);
		canvas.drawLine(0, boardCy, boardWidth, boardCy, linePaint);
		canvas.drawPath(naviArrow, fillPaint);

		// Setup to draw from 0 degrees
		canvas.save();
		canvas.rotate(-(float) animateDegrees, boardCx, boardCy);

		boolean isHighlight = (handlerColor == compassColor);
		Paint paint = isHighlight ? fillPaint : linePaint;

		// Draw rotator
		if (isShowRotator && compassMode == MODE_ROTATE) {
			canvas.save();
			canvas.rotate((float) (animateDegrees + rotatorRotatedDegrees), boardCx, boardCy);
			canvas.drawCircle(boardCx, boardCy + handlerToCenter, handlerRadius, paint);
			canvas.drawCircle(boardCx, boardCy - handlerToCenter, handlerRadius, paint);
			canvas.restore();
		}
		// Draw pointer
		else if (isShowPointer && compassMode == MODE_POINT) {
			final float arrowDim = boardInnerRadius / 15f;
			final float startY = (float) (boardCy + handlerToCenter - this.rotatorRadius);
			final float stopY = pointerStopY;
			//			final float stopY = mPointerStopY * (1 - mCompassBitmapZoomLevel);
			Path handlerArrow = DkCompassHelper.newArrowAt(boardCx, stopY, arrowDim, arrowDim);
			canvas.save();
			canvas.rotate((float) (animateDegrees + pointerDegrees), boardCx, boardCy);
			canvas.drawLine(boardCx, startY, boardCx, stopY, paint);
			canvas.drawPath(handlerArrow, fillPaint);
			canvas.drawCircle(boardCx, boardCy + handlerToCenter, handlerRadius, paint);
			canvas.restore();
		}

		// Draw 24 pointer lines
		if (isShow24PointerLines) {
			final float defaultStrokeWidth = linePaint.getStrokeWidth();
			linePaint.setStrokeWidth(defaultStrokeWidth / 2);
			canvas.save();
			canvas.rotate(7.5f, boardCx, boardCy);
			float startY = (float) (boardCy - Math.hypot(boardCx, boardCy));
			float stopY = boardCy - boardCy / 35f;
			for (int i = 0; i < 24; ++i) {
				canvas.drawLine((float) boardCx, startY, (float) boardCx, stopY, linePaint);
				canvas.rotate(15f, boardCx, boardCy);
			}
			canvas.restore();
			linePaint.setStrokeWidth(defaultStrokeWidth);
		}

		canvas.restore();

		// Count down animation
		if (countdownAnimator.isRunning()) {
			fillPaint.setColor((int) countdownAnimator.getAnimatedValue());
			canvas.drawCircle(boardCx, boardCy, handlerRadius, fillPaint);
		}

		// Draw compass
		if (compass != null) {
			postRotateCompassMatrix(animateDegrees);
			canvas.drawBitmap(compass, compassBitmapMatrix, compassPaint);
		}
	}

	/**
	 * This method just call invalidate() since we need the view's dimension to build compass.
	 * Compass will be built as soon as possible when the view is laid out.
	 */
	public void buildCompass(List<DkCompassRing> rings, Locale locale) {
		this.buildCompassRings = rings;
		this.buildCompassLocale = locale;

		if (getWidth() > 0 && getHeight() > 0) {
			buildCompassActual(locale);
		}
		else {
			isRequestBuildCompass = true;
			invalidate();
		}
	}

	public double calcPointerDegreesOnRotatedCompass() {
		return DkCompassHelper.calcDisplayAngle(pointerDegrees + compassDegreesInPointMode);
	}

	public DkCompassInfo readCurInfo() {
		return controller.readInfo(context, nextAnimateDegrees, buildCompassRings, getPoleLongNames());
	}

	/**
	 * Rotate the compass clockwisely an angle in degrees based North pole.
	 *
	 * @param degrees clockwise angle in degrees.
	 */
	public void rotate(double degrees) {
		if (compassMode == MODE_NORMAL) {
			compassDegreesInNormalMode = degrees;
		}
		else if (compassMode == MODE_ROTATE) {
			compassDegreesInRotateMode = degrees;
		}
		else if (compassMode == MODE_POINT) {
			compassDegreesInPointMode = degrees;
		}

		nextAnimateDegrees = degrees;
		invalidate();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean isRequestNextEvent = false;
		float x = event.getX();
		float y = event.getY();

		if (isAdjustCompass) {
			isRequestNextEvent = true;
			gestureDetector.onTouchEvent(event);
		}

		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN: {
				lastStopTime = System.currentTimeMillis();
				touchStartDegrees = DkCompassHelper.point2degrees(x, y, boardCenterX, boardCenterY);
				isTouchInsideRotator = isInsideHandlers(x, y);
				handlerColor = isTouchInsideRotator ? compassColor : compassSemiColor;
				if (isTouchInsideRotator) {
					handler.sendMessageDelayed(handler.obtainMessage(MSG_TURN_OFF_HIGH_LIGHT), 500);
				}
				invalidate();
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				if (!isTouchInsideRotator) {
					break;
				}
				// do not move handler if time passed over the specific value
				long stopTimeElapsed = System.currentTimeMillis() - lastStopTime;
				if (stopTimeElapsed >= rotatorDisabledCountDown) {
					lastStopTime = 0;
					break;
				}

				lastStopTime = System.currentTimeMillis();
				double touchEndDegrees = DkCompassHelper.point2degrees(x, y, boardCenterX, boardCenterY);

				if (compassMode == MODE_ROTATE) {
					rotatorRotatedDegrees = touchEndDegrees;
					double rotatedDegrees = DkMaths.convertAngleToRange180(touchEndDegrees - touchStartDegrees);

					if (rotatedDegrees <= -1 || rotatedDegrees >= 1) {
						touchStartDegrees = touchEndDegrees;
						if (listener != null) {
							listener.onRotatorElapsed(rotatedDegrees * rotationFactor);
						}
					}
				}
				else if (compassMode == MODE_POINT) {
					pointerDegrees = DkMaths.convertAngleToRange180(touchEndDegrees - 180);

					if (listener != null) {
						listener.onPointerChanged(touchEndDegrees);
					}
				}
				invalidate();
				break;
			}
			case MotionEvent.ACTION_OUTSIDE:
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP: {
				handlerColor = compassSemiColor;

				if (countdownAnimator.isRunning()) {
					moveCompassToCenterAndStopCountdown();
				}
				invalidate();
				break;
			}
		}

		return isRequestNextEvent || isTouchInsideRotator;
	}

	//region GetSet

	public DkCompassView setIsAdjustCompass(boolean isAdjust) {
		isAdjustCompass = isAdjust;
		return this;
	}

	/**
	 * @param poleShortNames Array contains 4 elements, for eg,. ['N', 'E', 'S', 'W']
	 */
	public void setPoleShortNames(String[] poleShortNames) {
		this.poleShortNames = poleShortNames;
	}

	/**
	 * @param poleLongNames Array contains 4 elements, for eg,. ['North', 'East', 'South', 'West']
	 */
	public void setPoleLongNames(String[] poleLongNames) {
		this.poleLongNames = poleLongNames;
	}

	public void setRotationFactor(double rotationFactor) {
		this.rotationFactor = rotationFactor;
	}

	public void setHandlerDisablePeriodTime(long handlerDisablePeriodTime) {
		rotatorDisabledCountDown = handlerDisablePeriodTime;
	}

	public DkCompassView setListener(DkCompassView.Listener listener) {
		this.listener = listener;
		return this;
	}

	public DkCompassView setCompassColor(int color) {
		compassColor = color;
		compassSemiColor = handlerColor = DkColors.toSemiColor(color);
		compassPaint.setColorFilter(new PorterDuffColorFilter(compassColor, PorterDuff.Mode.SRC_ATOP));

		countdownAnimator.setIntValues(color, compassSemiColor);
		countdownAnimator.setEvaluator(controller.getArgbEvaluator());
		countdownAnimator.removeAllUpdateListeners();
		countdownAnimator.addUpdateListener((va) -> {
			invalidate();
		});

		return this;
	}

	public DkCompassView setMode(int compassMode) {
		this.compassMode = compassMode;
		return this;
	}

	public DkCompassView setShow24PointerLines(boolean show) {
		isShow24PointerLines = show;
		return this;
	}

	public DkCompassView setShowRotator(boolean show) {
		isShowRotator = show;
		return this;
	}

	public DkCompassView setShowPointer(boolean show) {
		isShowPointer = show;
		return this;
	}

	public boolean isShowRotator() {
		return isShowRotator;
	}

	public boolean isShowPointer() {
		return isShowPointer;
	}

	public int getMode() {
		return compassMode;
	}

	public boolean isShow24PointerLines() {
		return isShow24PointerLines;
	}

	/**
	 * @return degrees (Oy based rotation) of the compass
	 */
	public double getCompassDegrees() {
		switch (compassMode) {
			case MODE_NORMAL: {
				return compassDegreesInNormalMode;
			}
			case MODE_ROTATE: {
				return compassDegreesInRotateMode;
			}
			case MODE_POINT: {
				return compassDegreesInPointMode;
			}
		}
		throw new RuntimeException("Invalid compass mode");
	}

	public int getCompassColor() {
		return compassColor;
	}

	public int getCompassSemiColor() {
		return compassSemiColor;
	}

	public boolean isAdjustCompass() {
		return isAdjustCompass;
	}

	//endregion GetSet

	private final DkDoubleFingerDetector.Listener gestureDetectorListener = new DkDoubleFingerDetector.Listener() {
		@Override
		public boolean onScale(float scaleFactor, float px, float py) {
			if (! isAdjustCompass) {
				return true;
			}
			if (! isTouchInsideRotator) {
				scaleFactor = scaleFactor > 1.0f ? 1.01f : 0.99f;
				compassBitmapZoomLevel += (scaleFactor - 1.0);
				compassBitmapMatrix.postScale(scaleFactor, scaleFactor, compassCx, compassCy);
				invalidate();
				return true;
			}
			return false;
		}

		@Override
		public boolean onDrag(float x1, float y1, float x2, float y2) {
			if (! isAdjustCompass) {
				return true;
			}
			if (isCompassMovable && ! isTouchInsideRotator) {
				if (isShouldStartCountDown()) {
					if (! countdownAnimator.isRunning()) {
						countdownAnimator.start();
					}
				}
				else {
					postTranslateCompassMatrix(x2 - x1, y2 - y1);
				}
				invalidate();
				return true;
			}
			return false;
		}

		@Override
		public boolean onRotate(float deltaDegrees, float px, float py) {
			if (! isAdjustCompass) {
				return true;
			}
			if (isTouchInsideRotator) {
				postRotateCompassMatrix(deltaDegrees + lastAnimatedDegrees);
				invalidate();
				return true;
			}
			return false;
		}

		@Override
		public boolean onDoubleTap() {
			return false;
		}
	};

	private void moveCompassToCenterAndStopCountdown() {
		isCompassMovable = false;
		hasCmpCenterLeftCountDown = false;
		countdownAnimator.cancel();

		double dx = boardCenterX - compassCx;
		double dy = boardCenterY - compassCy;

		compassCx = boardCenterX;
		compassCy = boardCenterY;
		compassBitmapMatrix.postTranslate((float) dx, (float) dy);

		handler.sendMessageDelayed(handler.obtainMessage(MSG_ALLOW_MOVE_COMPASS), 1000);
	}

	private boolean isShouldStartCountDown() {
		double dx = boardCenterX - compassCx;
		double dy = boardCenterY - compassCy;

		double cmpToCenter = dx * dx + dy * dy;
		double countdown = rotatorRadius * rotatorRadius;

		boolean isShouldStart = false;

		// when compass center leave countdown
		if (cmpToCenter > countdown && !hasCmpCenterLeftCountDown) {
			isShouldStart = true;
			hasCmpCenterLeftCountDown = true;
		}

		// when compass center come back
		if (cmpToCenter < countdown && hasCmpCenterLeftCountDown) {
			hasCmpCenterLeftCountDown = false;
		}

		return isShouldStart;
	}

	private boolean isInsideHandlers(double x, double y) {
		final int boardCx = boardCenterX, boardCy = boardCenterY;
		final float handlerToCenter = distFromRotatorCenterToBoardCenter;

		if (compassMode == MODE_POINT && isShowPointer) {
			double radian = Math.toRadians(pointerDegrees);
			double sin = Math.sin(radian), cos = Math.cos(radian);
			double cx = boardCx - handlerToCenter * sin, cy = boardCy + handlerToCenter * cos;

			return Math.hypot(x - cx, y - cy) <= rotatorRadius;
		}
		if (compassMode == MODE_ROTATE && isShowRotator) {
			double radian = Math.toRadians(rotatorRotatedDegrees);
			double sin = Math.sin(radian);
			double cos = Math.cos(radian);
			double cx = boardCx + handlerToCenter * sin;
			double cy = boardCy - handlerToCenter * cos;
			if (Math.hypot(x - cx, y - cy) <= rotatorRadius) {
				return true;
			}
			cx = boardCx - handlerToCenter * sin;
			cy = boardCy + handlerToCenter * cos;

			return Math.hypot(x - cx, y - cy) <= rotatorRadius;
		}
		return false;
	}

	private void buildCompassActual(Locale locale) {
		isBuildingCompass = true;

		DkObservable
			.fromCallable(() -> {
				updateMaterialsOnBoardSizeChanged();
				return buildCompassInternal(buildCompassRings, locale);
			})
			.scheduleInBackgroundAndObserveOnForeground()
			.doOnNext(bitmap -> {
				compass = bitmap;
				isFitCompassInsideBoard = true;
				isBuildingCompass = false;

				invalidate();
			})
			.doOnError(th -> {
				isBuildingCompass = false;
			})
			.subscribe();
	}

	private void updateMaterialsOnBoardSizeChanged() {
		// Board
		boardCenterX = boardWidth >> 1;
		boardCenterY = boardHeight >> 1;
		boardInnerRadius = Math.min(boardCenterX, boardCenterY);

		// Compass color
		int cmpColor = compassColor;
		linePaint.setColor(cmpColor);
		textPaint.setColor(cmpColor);
		fillPaint.setColor(cmpColor);

		// Rotator, pointer and navigation
		int boardInnerRadius = this.boardInnerRadius;
		int arrowTall = (boardInnerRadius >> 4) + (boardInnerRadius >> 8);
		int boardPadding = boardCenterY - boardInnerRadius;
		distFromRotatorCenterToBoardCenter = (boardInnerRadius >> 2) + (boardInnerRadius >> 3);
		rotatorRadius = (boardInnerRadius >> 3) + (boardInnerRadius >> 4);

		// Pointer
		pointerStopY = boardCenterY - Math.max(boardCenterX, boardCenterY) + arrowTall;

		// Navigation Ox, Oy
		naviArrowStartY = boardCenterY + boardInnerRadius + (boardPadding >> 1);
		naviArrowStopY = boardCenterY - boardInnerRadius - (boardPadding >> 1);
		naviArrow = DkCompassHelper.newArrowAt(boardCenterX, naviArrowStopY, arrowTall, arrowTall);
	}

	private void fitCompassInsideBoard() {
		if (DEBUG) {
			DkLogcats.info(this, "fitCompassInsideBoard");
		}
		if (compass == null) {
			return;
		}

		int boardCx = boardCenterX, boardCy = boardCenterY;
		compassCx = boardCx;
		compassCy = boardCy;

		float cmpHalfWidth = compass.getWidth() >> 1, cmpHalfHeight = compass.getHeight() >> 1;
		float scaleFactor = Math.min(boardCx, boardCy) / Math.min(cmpHalfWidth, cmpHalfHeight);

		compassBitmapMatrix.reset();
		compassBitmapMatrix.postTranslate(boardCx - cmpHalfWidth, boardCy - cmpHalfHeight);
		compassBitmapMatrix.postScale(scaleFactor, scaleFactor, (float) boardCx, (float) boardCy);

		compassBitmapZoomLevel = 1f;
		lastAnimatedDegrees = 0;
	}

	private Bitmap buildCompassInternal(List<DkCompassRing> rings, Locale locale) {
		// use to determine default values (padding, height...)
		final String defaultText = "360";
		final Paint textPaint = this.textPaint;
		final Rect tmpRect = this.tmpRect;

		textPaint.setTextSize(DEFAULT_WORD_TEXT_SIZE);
		textPaint.getTextBounds(defaultText, 0, defaultText.length(), tmpRect);
		final int defaultSpace = tmpRect.height();
		final int rayPadding = Math.max(4, defaultSpace >> 2);

		// Step 1. measure radius of compass should be. Also calculate preferred
		// word-fontsize for each ring to fit with compass bitmap size.
		final float cmpMaxRadius = getCompassMaxRadius();
		float cmpRadius;

		float[] wordTextSizes = null;
		float[][][] wordDims = null;
		float[][] maxWordDims = null;

		if (rings == null) {
			rings = Collections.emptyList();
		}

		final int ringCnt = rings.size();

		if (ringCnt > 0) {
			wordTextSizes = new float[ringCnt];
			maxWordDims = new float[ringCnt][2];
			wordDims = new float[ringCnt][][];
		}

		for (int time = 0; ; ++time) {
			float tmpCmpRadius = boardInnerRadius >> 3;

			int numberUnnecessaryMeasureRing = 0;

			for (int ringInd = ringCnt - 1; ringInd >= 0; --ringInd) {
				DkCompassRing ring = rings.get(ringInd);
				if (!ring.isVisible()) {
					++numberUnnecessaryMeasureRing;
					continue;
				}

				// word font size should between [1, 100]
				int wordFontSize = Math.max(1, Math.min(100, ring.wordFontSize));

				float ringTextSize = wordFontSize * DkConfig.scaledDensity() - time;
				if (ringTextSize < 1) {
					ringTextSize = 1;
					++numberUnnecessaryMeasureRing;
				}
				wordTextSizes[ringInd] = ringTextSize;

				List<String> words = ring.getWords();
				boolean isHorizontal = ring.isHorizontalWord();

				final int wordCnt = words.size();
				wordDims[ringInd] = new float[2][wordCnt];

				for (int i = 0; i < wordCnt; ++i) {
					String word = words.get(i);

					if (ring.isWordLowerCase()) {
						word = word.toLowerCase(locale);
					}
					else if (ring.isWordUpperCase()) {
						word = word.toUpperCase(locale);
					}

					int endLength = ring.getShownCharCount();
					if (endLength > word.length()) {
						endLength = word.length();
					}
					if (endLength < word.length()) {
						word = word.substring(0, endLength);
					}

					textPaint.setTextSize(ringTextSize);
					textPaint.getTextBounds(word, 0, endLength, tmpRect);
					wordDims[ringInd][0][i] = isHorizontal ? tmpRect.width() : tmpRect.height();
					wordDims[ringInd][1][i] = isHorizontal ? tmpRect.height() : tmpRect.width();
				}

				float maxWidth = 0, maxHeight = 0;

				for (float width : wordDims[ringInd][0]) {
					if (maxWidth < width) {
						maxWidth = width;
					}
				}
				for (float height : wordDims[ringInd][1]) {
					if (maxHeight < height) {
						maxHeight = height;
					}
				}

				maxWordDims[ringInd][0] = maxWidth;
				maxWordDims[ringInd][1] = maxHeight;

				float sumLength = wordCnt * (maxWidth + (rayPadding << 1));
				float nextRadius = (float) (sumLength / 2 / Math.PI + maxHeight);
				float minRadius = tmpCmpRadius + maxHeight;

				tmpCmpRadius = Math.max(minRadius, nextRadius);
			}

			if (tmpCmpRadius <= cmpMaxRadius) {
				cmpRadius = tmpCmpRadius;
				break;
			}

			if (numberUnnecessaryMeasureRing == ringCnt) {
				cmpRadius = cmpMaxRadius;
				break;
			}
		}

		// calculate radius for 36 angles
		textPaint.setTextSize(DEFAULT_WORD_TEXT_SIZE);
		textPaint.getTextBounds(defaultText, 0, defaultText.length(), tmpRect);
		float sumLength = 36 * (tmpRect.width() + (rayPadding << 1));
		float justNeedLength = (float) (sumLength / 2 / Math.PI + tmpRect.width());
		float minLengthRequired = cmpRadius + tmpRect.height();
		cmpRadius = Math.max(minLengthRequired, justNeedLength);

		// calculate radius for 4 pole-indicators of N, E, S, W
		int indicatorHeight = (defaultSpace >> 1) + (defaultSpace >> 2);
		cmpRadius += indicatorHeight;

		if (DEBUG) {
			DkLogcats.info(this, "compass radius: %f/%f, inner radius: %d", cmpRadius, cmpMaxRadius, boardInnerRadius);
		}
		if (cmpRadius < boardInnerRadius) {
			cmpRadius = boardInnerRadius;
		}

		// Step 2. create, draw default compass
		int cmpDiameter = (int) (cmpRadius * 2);
		Bitmap compass = Bitmap.createBitmap(cmpDiameter, cmpDiameter, Bitmap.Config.ALPHA_8);
		Canvas canvas = new Canvas(compass);

		DkLogcats.info(this, "compass size: " + DkBitmaps.size(compass));

		// obtain compass radius and center coordinate
		final int cmpSemiWidth = compass.getWidth() >> 1, cmpSemiHeight = compass.getHeight() >> 1;
		cmpRadius = Math.min(cmpSemiWidth, cmpSemiHeight);

		float belowRayRadius = cmpRadius - indicatorHeight;

		// draw 4-arrow-indicator for north, east, south and west direction
		canvas.save();
		for (int i = 0; i < 4; ++i) {
			Path arrow = DkCompassHelper.newArrowAt(cmpSemiWidth, 0, defaultSpace, indicatorHeight);
			canvas.drawPath(arrow, fillPaint);
			canvas.rotate(90, cmpSemiWidth, cmpSemiHeight);
		}
		canvas.restore();

		// draw center lines for compass
		double halfHandlerRadius = rotatorRadius / 2;
		float vsy = (float) (cmpSemiHeight - halfHandlerRadius);
		float vey = (float) (cmpSemiHeight + halfHandlerRadius);
		float hsx = (float) (cmpSemiWidth - halfHandlerRadius);
		float hex = (float) (cmpSemiWidth + halfHandlerRadius);
		canvas.drawLine(cmpSemiWidth, vsy, cmpSemiWidth, vey, linePaint);
		canvas.drawLine(hsx, cmpSemiHeight, hex, cmpSemiHeight, linePaint);

		// draw most-outer circle
		canvas.drawCircle(cmpSemiWidth, cmpSemiHeight, belowRayRadius, linePaint);

		// draw 360 dividers
		canvas.save();
		int divHeight = 2 * defaultSpace / 3;
		for (int i = 0; i < 360; ++i) {
			float stopY = (i % 10 == 0) ? (cmpRadius - belowRayRadius) + defaultSpace
				: (cmpRadius - belowRayRadius) + divHeight;
			canvas.drawLine(cmpSemiWidth, indicatorHeight, cmpSemiWidth, stopY, linePaint);
			canvas.rotate(1f, cmpSemiWidth, cmpSemiHeight);
		}
		canvas.restore();

		// draw 36 angles: 0, 10, 20,..., 350
		canvas.save();
		belowRayRadius -= (defaultSpace + rayPadding) << 1;

		for (int i = 0; i < 36; ++i) {
			String number;
			if (i == 0) {
				number = getPoleShortName(0);
			}
			else if (i == 9) {
				number = getPoleShortName(1);
			}
			else if (i == 18) {
				number = getPoleShortName(2);
			}
			else if (i == 27) {
				number = getPoleShortName(3);
			}
			else {
				number = (i * 10) + "";
			}

			if (i % 9 == 0) {
				textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
			}
			else {
				textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
			}

			textPaint.getTextBounds(number, 0, number.length(), tmpRect);
			float x = cmpSemiWidth - (tmpRect.width() >> 1);
			float y = cmpSemiHeight - belowRayRadius - rayPadding;
			float a[] = getTextViewDrawPoint(tmpRect, x, y);
			canvas.drawText(number, a[0], a[1], textPaint);
			canvas.rotate(10f, cmpSemiWidth, cmpSemiHeight);
		}
		textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		canvas.restore();

		// draw circle below 36 angles
		canvas.drawCircle(cmpSemiWidth, cmpSemiHeight, belowRayRadius, linePaint);

		// Step 3. draw user customed compass
		for (int ringInd = 0; ringInd < ringCnt; ++ringInd) {
			DkCompassRing ring = rings.get(ringInd);
			if (!ring.isVisible()) {
				continue;
			}
			textPaint.setTextSize(wordTextSizes[ringInd]);
			textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, ring.wordStyle));
			float defaultRotateDegrees = ring.getRotatedDegrees();
			List<String> words = ring.getWords();

			final int wordCnt = words.size();
			float prevBelowRayRadius;

			prevBelowRayRadius = belowRayRadius;
			belowRayRadius -= (rayPadding << 1) + maxWordDims[ringInd][1];

			if (belowRayRadius < 0) {
				break;
			}

			// now draw words on this ring
			canvas.save();
			canvas.rotate(defaultRotateDegrees, cmpSemiWidth, cmpSemiHeight);
			boolean isHorizontal = ring.isHorizontalWord();
			boolean isCurveText = ring.isCurvedWord;
			float fixedWordRangeDegrees = 360f / wordCnt;
			Path path = new Path();
			path.addCircle(cmpSemiWidth, cmpSemiHeight, belowRayRadius + rayPadding, Path.Direction.CW);

			for (int wordInd = 0; wordInd < wordCnt; ++wordInd) {
				canvas.save();
				String word = words.get(wordInd);

				if (ring.isWordLowerCase()) {
					word = word.toLowerCase(locale);
				}
				else if (ring.isWordUpperCase()) {
					word = word.toUpperCase(locale);
				}

				int endLength = ring.getShownCharCount();
				if (endLength > word.length()) {
					endLength = word.length();
				}
				if (endLength < word.length()) {
					word = word.substring(0, endLength);
				}

				textPaint.getTextBounds(word, 0, endLength, tmpRect);

				if (isHorizontal) {
					if (isCurveText) {
						float wordRangeDegrees = (float) (180 * wordDims[ringInd][0][wordInd]
							/ Math.PI
							/ (belowRayRadius + rayPadding));
						canvas.rotate(-90 - wordRangeDegrees / 2, cmpSemiWidth, cmpSemiHeight);
						canvas.drawTextOnPath(word, path, 0, -tmpRect.bottom, textPaint);
					}
					else {
						float leftBottomX = cmpSemiWidth - wordDims[ringInd][0][wordInd] / 2;
						float leftBottomY = cmpSemiHeight - belowRayRadius - rayPadding;
						float a[] = getTextViewDrawPoint(tmpRect, leftBottomX, leftBottomY);
						canvas.drawText(word, a[0], a[1], textPaint);
					}
				}
				else {
					float pad = prevBelowRayRadius - belowRayRadius;
					pad = (pad - wordDims[ringInd][1][wordInd]) / 2;
					float dy = cmpSemiHeight - belowRayRadius - pad;
					canvas.translate(cmpSemiWidth, dy);
					canvas.rotate(-90, 0, 0);
					float[] a = getTextViewDrawPoint(tmpRect,
						0,
						wordDims[ringInd][0][wordInd] / 2);
					canvas.drawText(word, a[0], a[1], textPaint);
				}
				canvas.restore();

				// draw word-divider line
				canvas.save();
				canvas.rotate(fixedWordRangeDegrees / 2, cmpSemiWidth, cmpSemiHeight);
				canvas.drawLine(cmpSemiWidth, cmpSemiHeight - belowRayRadius,
					cmpSemiWidth, cmpSemiHeight - prevBelowRayRadius, linePaint);
				canvas.restore();

				canvas.rotate(fixedWordRangeDegrees, cmpSemiWidth, cmpSemiHeight);
			}
			canvas.restore();

			// draw under circle
			canvas.drawCircle(cmpSemiWidth, cmpSemiHeight, belowRayRadius, linePaint);
		}

		// Step 4. restore default settings
		textPaint.setTextSize(DEFAULT_WORD_TEXT_SIZE);
		textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

		return compass;
	}

	private void postRotateCompassMatrix(double degrees) {
		float delta = (float) (degrees - lastAnimatedDegrees);
		lastAnimatedDegrees = degrees;
		compassBitmapMatrix.postRotate(-delta, compassCx, compassCy);
	}

	private void postTranslateCompassMatrix(double dx, double dy) {
		compassCx += dx;
		compassCy += dy;
		compassBitmapMatrix.postTranslate((float) dx, (float) dy);
	}

	private float getCompassMaxRadius() {
		//todo take care max-memory
		long maxMemory = Runtime.getRuntime().maxMemory();
		return 2000f;
	}

	private String getPoleShortName(int index) {
		return getPoleShortNames()[index];
	}

	private String[] getPoleShortNames() {
		if (poleShortNames == null) {
			poleShortNames = new String[]{
				context.getString(R.string.compass_north_short),
				context.getString(R.string.compass_east_short),
				context.getString(R.string.compass_south_short),
				context.getString(R.string.compass_west_short),
			};
		}
		return poleShortNames;
	}

	private String getPoleLongName(int index) {
		return getPoleLongNames()[index];
	}

	private String[] getPoleLongNames() {
		if (poleLongNames == null) {
			poleLongNames = new String[]{
				context.getString(R.string.north),
				context.getString(R.string.east),
				context.getString(R.string.south),
				context.getString(R.string.west),
			};
		}
		return poleLongNames;
	}
}
