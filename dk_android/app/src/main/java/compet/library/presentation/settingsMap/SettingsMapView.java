package compet.library.presentation.settingsMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import tool.compet.core.config.DkConfig;

public class SettingsMapView extends View implements View.OnTouchListener {
	// Real area dimension
	private float areaWidth = 15f; // meter
	private float areaHeight = 15f; // meter
	private Runnable onApplyAreaFailedCallback;

	// For map dimension
	private float padding;
	private Paint boundsPaint;
	private RectF bounds;
	private boolean isBoardSizeChanged;

	// Point list on map
	private List<Action> cacheActions;
	private ArrayList<Point> points = new ArrayList<>();

	// For undo, redo actions
	private int curActionIndex = -1;
	List<Action> actions = new ArrayList<>();

	// For touch processing to add point, line...
	static final int MODE_ADD_POINT = 1;
	static final int MODE_ADD_LINE = 2;
	int mode = -1;
	private Point draggingPoint;
	private Point startPoint;
	private Point endPoint;
	private float touchX;
	private float touchY;

	public SettingsMapView(Context context) {
		this(context, null, 0);
	}

	public SettingsMapView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SettingsMapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		boundsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		boundsPaint.setStyle(Paint.Style.FILL);
		boundsPaint.setColor(Color.parseColor("#b3ffcc"));

		bounds = new RectF();

		padding = 8 * DkConfig.device.density;

		setOnTouchListener(this);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		isBoardSizeChanged = true;
		invalidate();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean ateEvent = false;
		touchX = event.getX();
		touchY = event.getY();

		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN: {
				ateEvent = true;

				// Check for add point
				if (mode == MODE_ADD_POINT) {
					if (tryActionAddPoint(touchX, touchY)) {
						invalidate();
					}
				}
				// Check for add line
				else if (mode == MODE_ADD_LINE) {
					startPoint = findAndHighlightTouchingPoint(startPoint, touchX, touchY);

					if (startPoint != null) {
						invalidate();
					}
				}
				// Check for highlight dragging point
				else {
					draggingPoint = findAndHighlightTouchingPoint(draggingPoint, touchX, touchY);

					if (draggingPoint != null) {
						invalidate();
					}
				}
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				if (mode == MODE_ADD_LINE && startPoint != null) {
					endPoint = findAndHighlightTouchingPoint(endPoint, touchX, touchY);

					if (endPoint != null) {
						invalidate();
					}
				}
				else if (draggingPoint != null) {
					draggingPoint.tryMoveTo(touchX, touchY, bounds);
					invalidate();
				}
				break;
			}
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_OUTSIDE:
			case MotionEvent.ACTION_UP: {
				// Add line
				if (startPoint != null && endPoint != null) {
					tryActionAddLine(startPoint, endPoint);
				}
				if (startPoint != null) {
					startPoint.setHighlight(false);
					startPoint = null;
				}
				if (endPoint != null) {
					endPoint.setHighlight(false);
					endPoint = null;
				}
				if (draggingPoint != null) {
					draggingPoint.setHighlight(false);
					draggingPoint = null;
				}

				invalidate();

				break;
			}
		}

		return ateEvent;
	}

	private void performAddAction(Action action) {
		for (int i = actions.size() - 1; i > curActionIndex; --i) {
			actions.get(i).delete(points);
			actions.remove(i);
		}
		++curActionIndex;
		actions.add(action);

		action.recovery(points);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (isBoardSizeChanged) {
			isBoardSizeChanged = false;

			tryUpdateBounds();
		}

		// Draw bounds
		canvas.drawRect(bounds, boundsPaint);

		// Draw line from startPoint to current touch
		if (mode == MODE_ADD_LINE && startPoint != null) {
			startPoint.drawLineTo(canvas, touchX, touchY);
		}

		// Draw graph (points + lines)
		for (Point point : points) {
			point.draw(canvas);
			point.drawLinesToNeighbours(canvas);
		}
	}

	public void setAreaDimension(float aw, float ah, Runnable failCb) {
		areaWidth = aw;
		areaHeight = ah;
		onApplyAreaFailedCallback = failCb;

		isBoardSizeChanged = true;
		invalidate();
	}

	public void setupMapFromCache(float aw, float ah, List<Action> cacheActions) {
		this.areaWidth = aw;
		this.areaHeight = ah;
		this.cacheActions = cacheActions;

		isBoardSizeChanged = true;
		invalidate();
	}

	public boolean undo() {
		if (curActionIndex < 0) {
			return false;
		}
		actions.get(curActionIndex--).delete(points);
		invalidate();
		return true;
	}

	public boolean redo() {
		if (curActionIndex >= actions.size() - 1) {
			return false;
		}
		actions.get(++curActionIndex).recovery(points);
		invalidate();
		return true;
	}

	public float getAreaWidth() {
		return areaWidth;
	}

	public float getAreaHeight() {
		return areaHeight;
	}

	private void tryUpdateBounds() {
		final int W = getWidth();
		final int H = getHeight();

		float boundsWidth = W - padding;
		float boundsHeight = H - padding;

		if (boundsWidth > boundsHeight * areaWidth / areaHeight) {
			boundsWidth = boundsHeight * areaWidth / areaHeight;
		}
		else {
			boundsHeight = boundsWidth * areaHeight / areaWidth;
		}

		RectF nextBounds = new RectF();

		nextBounds.left = (W - boundsWidth) / 2f;
		nextBounds.right = nextBounds.left + boundsWidth;
		nextBounds.top = (H - boundsHeight) / 2f;
		nextBounds.bottom = nextBounds.top + boundsHeight;

		// Only allow change bounds if has enough space to locate all points
		boolean ok = true;

		for (Point point : points) {
			if (!point.isEnoughSpaceWith(bounds, nextBounds)) {
				ok = false;
				break;
			}
		}

		if (ok) {
			for (Point point : points) {
				point.updatePosition(bounds, nextBounds);
			}

			bounds = nextBounds;
		}
		else if (onApplyAreaFailedCallback != null) {
			onApplyAreaFailedCallback.run();
			onApplyAreaFailedCallback = null;
		}

		// If cacheActions existed, then effect them after bounds has determined
		if (cacheActions != null && points.size() == 0) {
			for (Action action : cacheActions) {
				performAddAction(action);
			}
			cacheActions = null;
		}
	}

	private Point findAndHighlightTouchingPoint(Point target, float x, float y) {
		if (target != null) {
			target.setHighlight(false);
		}

		for (int i = points.size() - 1; i >= 0; --i) {
			Point point = points.get(i);

			if (point.isHighlightCover(x, y)) {
				point.setHighlight(true);
				target = point;
				break;
			}
		}

		if (target != null) {
			target.setHighlight(true);
		}

		return target;
	}

	private boolean tryActionAddPoint(float x, float y) {
		Point point = new Point(x, y);

		if (point.isInside(bounds)) {
			performAddAction(new AddPointAction(point));
			return true;
		}

		return false;
	}

	private void tryActionAddLine(Point startPoint, Point endPoint) {
		if (!startPoint.hasConnectionWith(endPoint)) {
			performAddAction(new AddLineAction(startPoint, endPoint));
		}
	}

	public void clear() {
		points.clear();

		curActionIndex = -1;
		actions.clear();

		invalidate();
	}

	public ArrayList<Point> getPoints() {
		return points;
	}

	public RectF getBounds() {
		return bounds;
	}
}
