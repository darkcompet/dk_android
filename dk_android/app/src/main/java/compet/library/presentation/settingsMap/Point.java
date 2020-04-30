package compet.library.presentation.settingsMap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import tool.compet.core.config.DkConfig;

public class Point {
	private static AtomicLong counter = new AtomicLong();

	long id;
	float x;
	float y;
	float radius;

	private List<Point> neighbours = new ArrayList<>();

	private Paint paint;
	private boolean isHighlight;
	private Paint linePaint;

	Point(float x, float y) {
		this.id = counter.incrementAndGet();
		this.x = x;
		this.y = y;
		this.radius = 12 * DkConfig.device.density;

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLUE);

		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setStrokeWidth(2f);
		linePaint.setColor(Color.BLUE);
	}

	void draw(Canvas canvas) {
		paint.setColor(isHighlight ? Color.RED : Color.BLUE);
		canvas.drawCircle(x, y, isHighlight ? 2f * radius : radius, paint);
	}

	boolean isHighlightCover(float x, float y) {
		return Math.hypot(x - this.x, y - this.y) <= 2f * radius;
	}

	void tryMoveTo(float x, float y, RectF bounds) {
		if (x - bounds.left >= radius && x + radius <= bounds.right) {
			this.x = x;
		}
		if (y - bounds.top >= radius && y + radius <= bounds.bottom) {
			this.y = y;
		}
	}

	void setHighlight(boolean highlight) {
		isHighlight = highlight;
	}

	boolean addNeighbour(Point other) {
		if (other == null || neighbours.contains(other) || other.neighbours.contains(this)) {
			return false;
		}

		neighbours.add(other);
		other.neighbours.add(this);
		return true;
	}

	boolean removeNeighbour(Point other) {
		if (other == null) {
			return false;
		}

		neighbours.remove(other);
		other.neighbours.remove(this);
		return true;
	}

	boolean isInside(RectF bounds) {
		return x - radius >= bounds.left && x + radius <= bounds.right
			&& y - radius >= bounds.top && y + radius <= bounds.bottom;
	}

	void drawLinesToNeighbours(Canvas canvas) {
		for (Point other : neighbours) {
			canvas.drawLine(x, y, other.x, other.y, linePaint);
		}
	}

	void drawLineTo(Canvas canvas, float x, float y) {
		canvas.drawLine(x, y, this.x, this.y, linePaint);
	}

	boolean isEnoughSpaceWith(RectF curBounds, RectF nextBounds) {
		float boundsWidth = curBounds.right - curBounds.left;
		float boundsHeight = curBounds.bottom - curBounds.top;

		float widthRate = (x - curBounds.left) / boundsWidth;
		float heightRate = (y - curBounds.top) / boundsHeight;

		float ox = nextBounds.left + widthRate * (nextBounds.right - nextBounds.left);
		float oy = nextBounds.top + heightRate * (nextBounds.bottom - nextBounds.top);

		return ox - radius >= nextBounds.left
			&& ox + radius <= nextBounds.right
			&& oy - radius >= nextBounds.top
			&& oy + radius <= nextBounds.bottom;
	}

	void updatePosition(RectF curBounds, RectF newBounds) {
		float boundsWidth = curBounds.right - curBounds.left;
		float boundsHeight = curBounds.bottom - curBounds.top;

		float widthRate = (x - curBounds.left) / boundsWidth;
		float heightRate = (y - curBounds.top) / boundsHeight;

		x = newBounds.left + widthRate * (newBounds.right - newBounds.left);
		y = newBounds.top + heightRate * (newBounds.bottom - newBounds.top);
	}

	boolean hasConnectionWith(Point other) {
		return other != null && (neighbours.contains(other) || other.neighbours.contains(this));
	}
}
