/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import tool.compet.core.DkLogs;

import static tool.compet.core.BuildConfig.DEBUG;

public class DkDoubleFingerDetector {
	public interface Listener {
		/**
		 * @param scaleFactor scale ratio between last touch and current touch
		 * @param px center X of 2 fingers
		 * @param py center Y of 2 fingers
		 *
		 * @return false if you are happy with this detection, so other events will be ignored to check.
		 */
		boolean onScale(float scaleFactor, float px, float py);

		/**
		 * @param x1 x of start point
		 * @param y1 y of start point
		 * @param x2 x of end point
		 * @param y2 y of end point
		 *
		 * @return false if you are happy with this detection, so other events will be ignored to check.
		 */
		boolean onDrag(float x1, float y1, float x2, float y2);

		/**
		 * @param degrees rotated angle between last touches and current touches
		 * @param px center X of 2 fingers
		 * @param py center Y of 2 fingers
		 *
		 * @return false if you are happy with this detection, so other events will be ignored.
		 */
		boolean onRotate(float degrees, float px, float py);

		/**
		 * @return false if you are happy with this detection, so other events will be ignored to check.
		 */
		boolean onDoubleTap();
	}

	private static final int INVALID_PID = -1;

	private Listener listener;

	// Fingure 1
	private int f1Pid = INVALID_PID;
	private float f1LastX;
	private float f1LastY;

	// Fingure 2
	private int f2Pid = INVALID_PID;
	private float f2LastX;
	private float f2LastY;

	private long firstLastTouchTime = -1;
	private boolean isFirstTouched;
	private final int touchSlopSquare;
	private final long doubleTapTimeout;

	public DkDoubleFingerDetector(Context context, Listener listener) {
		int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		this.listener = listener;
		this.doubleTapTimeout = ViewConfiguration.getDoubleTapTimeout();
		this.touchSlopSquare = touchSlop * touchSlop;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN: {
				onActionDown(event);
				break;
			}
			case MotionEvent.ACTION_UP: {
				onActionUp(event);
				break;
			}
			case MotionEvent.ACTION_POINTER_DOWN: {
				onActionPointerDown(event);
				break;
			}
			case MotionEvent.ACTION_POINTER_UP: {
				onActionPointerUp(event);
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				onActionMove(event);
				break;
			}
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_OUTSIDE: {
				onActionCancel(event);
				break;
			}
		}

		return true;
	}

	private void updateLastTouches(float firstX, float firstY, float secondX, float secondY) {
		f1LastX = firstX;
		f1LastY = firstY;
		f2LastX = secondX;
		f2LastY = secondY;
	}

	private void detectDoubleTap() {
		long now = System.currentTimeMillis();

		if (firstLastTouchTime == -1) {
			firstLastTouchTime = now;
			return;
		}

		long timePassed = now - firstLastTouchTime;
		firstLastTouchTime = now;

		if (timePassed < doubleTapTimeout && listener != null) {
			listener.onDoubleTap();
		}
	}

	private boolean isValidEventIndex(int index, MotionEvent event) {
		return 0 <= index && index < event.getPointerCount();
	}

	/**
	 * Action down of finger 1.
	 */
	private void onActionDown(MotionEvent event) {
		final int index = event.getActionIndex();
		final int pid = event.getPointerId(index);

		if (DEBUG) {
			DkLogs.info(this, "Primary down~ pid: %d, index: %d", pid, index);
		}

		isFirstTouched = true;
		f1Pid = pid;

		if (isValidEventIndex(pid, event)) {
			f1LastX = event.getX(pid);
			f1LastY = event.getY(pid);
		}

		detectDoubleTap();
	}

	/**
	 * Action down of finger 2.
	 */
	private void onActionPointerDown(MotionEvent event) {
		final int index = event.getActionIndex();
		final int pid = event.getPointerId(index);

		if (DEBUG) {
			DkLogs.info(this, "Pointer down~ pid: %d, index: %d", pid, index);
		}

		f2Pid = pid;

		int tmpIndex1 = event.findPointerIndex(pid);

		if (isValidEventIndex(tmpIndex1, event)) {
			f2LastX = event.getX(tmpIndex1);
			f2LastY = event.getY(tmpIndex1);
		}
	}

	/**
	 * Action up of finger 1.
	 */
	private void onActionUp(MotionEvent event) {
		final int index = event.getActionIndex();
		final int pid = event.getPointerId(index);

		if (DEBUG) {
			DkLogs.info(this, "Primary up~ pid: %d, index: %d", pid, index);
		}

		f1Pid = INVALID_PID;
	}

	/**
	 * Action up of finger 2.
	 */
	private void onActionPointerUp(MotionEvent event) {
		final int index = event.getActionIndex();
		final int pid = event.getPointerId(index);

		if (DEBUG) {
			DkLogs.info(this, "Pointer up~ pid: %d, index: %d", pid, index);
		}

		f2Pid = INVALID_PID;
	}

	/**
	 * Action move of fingers will go here.
	 */
	private void onActionMove(MotionEvent event) {
		final int index = event.getActionIndex();
		final int pid = event.getPointerId(index);

		//todo should pass this to give f2 a chance perform action move?
		if (f1Pid == INVALID_PID) {
			return;
		}

		// Obtain data for first pointer
		int firstIndex = event.findPointerIndex(f1Pid);

		if (!isValidEventIndex(firstIndex, event)) {
			return;
		}

		final float firstX = event.getX(firstIndex);
		final float firstY = event.getY(firstIndex);
		final float firstDx = firstX - f1LastX;
		final float firstDy = firstY - f1LastY;

		// Skip vibrated-movation for first pointer downed
		if (isFirstTouched) {
			if (firstDx * firstDx + firstDy * firstDy < touchSlopSquare) {
				// Skip small-moving
				return;
			}
			else {
				// Start moving from this point
				isFirstTouched = false;
				f1LastX = firstX;
				f1LastY = firstY;
			}
		}

		boolean isUserAcceptedAction = false;//todo: impl detectDrag();

		if (isUserAcceptedAction) {
			return;
		}

		// Obtain data for second pointer
		int secondIndex = event.findPointerIndex(f2Pid);

		if (!isValidEventIndex(firstIndex, event) || !isValidEventIndex(secondIndex, event)) {
			return;
		}

		final float secondX = event.getX(secondIndex);
		final float secondY = event.getY(secondIndex);
		final float secondDx = secondX - f2LastX;
		final float secondDy = secondY - f2LastY;

		// Skip gesture that not be considered as rotate or scale
		double rotatedAngleDegrees = Math.abs(MyGestureHelper.calcDegreesBetweenLines(f1LastX, f1LastY, firstX, firstY,
			f2LastX, f2LastY, secondX, secondY));

		if (rotatedAngleDegrees < 150) {
			if (DEBUG) {
				DkLogs.info(this, "skipped gesture with angle: " + rotatedAngleDegrees);
			}
			updateLastTouches(firstX, firstY, secondX, secondY);
			return;
		}

		float firstRotatedDegrees = Math.abs(MyGestureHelper.calcDegreesBetweenLines(f1LastX, f1LastY,
			firstX, firstY, f1LastX, f1LastY, f2LastX, f2LastY));

		float secondRotatedDegrees = Math.abs(MyGestureHelper.calcDegreesBetweenLines(f2LastX, f2LastY,
			secondX, secondY, f2LastX, f2LastY, f1LastX, f1LastY));

		// Detect ROTATE
		if ((firstRotatedDegrees >= 60 && firstRotatedDegrees <= 120) &&
			(secondRotatedDegrees >= 60 && secondRotatedDegrees <= 120)) {

			float rotatedDegrees = MyGestureHelper.calcDegreesBetweenLines(f1LastX, f1LastY, f2LastX, f2LastY,
				firstX, firstY, secondX, secondY);

			float px = (f1LastX + f2LastX) / 2;
			float py = (f1LastY + f2LastY) / 2;

			if (listener != null) {
				isUserAcceptedAction = listener.onRotate(rotatedDegrees, px, py);
			}
			if (DEBUG) {
				DkLogs.info(this, "rotatedDegrees: " + rotatedDegrees);
			}
			updateLastTouches(firstX, firstY, secondX, secondY);
			return;
		}

		if (isUserAcceptedAction) {
			return;
		}

		// Detect SCALE
		if ((firstRotatedDegrees <= 30 || firstRotatedDegrees >= 150) &&
			(secondRotatedDegrees <= 30 || secondRotatedDegrees >= 150)) {
			double lastDx = f2LastX - f1LastX;
			double lastDy = f2LastY - f1LastY;
			double curDx = firstX - secondX;
			double curDy = firstY - secondY;
			double lastDist = lastDx * lastDx + lastDy * lastDy;
			double curDist = curDx * curDx + curDy * curDy;

			float scaleFactor = lastDist < curDist ? 1.05f : 0.95f;

			if (listener != null) {
				isUserAcceptedAction = listener.onScale(scaleFactor,
					(firstX + f1LastX + secondX + f2LastX) / 4f,
					(firstY + f1LastY + secondY + f2LastY) / 4f);
			}
			if (DEBUG) {
				DkLogs.info(this, "scaleFactor: " + scaleFactor);
			}

			updateLastTouches(firstX, firstY, secondX, secondY);

			return;
		}

		updateLastTouches(firstX, firstY, secondX, secondY);
	}

	private boolean detectDrag(float f1LastX, float f1LastY, float f1CurX, float f1CurY,
		float f2LastX, float f2LastY, float f2CurX, float f2CurY) {

		if (listener == null) {
			return false;
		}
		if (f1Pid != INVALID_PID && f2Pid == INVALID_PID) {
			//todo implement
			//			f1LastX = firstX;
			//			f1LastY = firstY;
			//
			//			return listener.onDrag(f1LastX, f1LastY, f1LastY, firstDy);
		}
		else if (f1Pid == INVALID_PID && f2Pid != INVALID_PID) {
		}

		return false;
	}

	private void onActionCancel(MotionEvent event) {
		final int index = event.getActionIndex();
		final int pid = event.getPointerId(index);

		if (DEBUG) {
			DkLogs.info(this, "Cancel or Outside pid: %d, index: %d", pid, index);
		}

		f1Pid = INVALID_PID;
		f2Pid = INVALID_PID;
	}
}
