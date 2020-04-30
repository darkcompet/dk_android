package compet.library.presentation.settingsMap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Objects;

class MapActionInfo {
	static final int TYPE_ADD_POINT = 1;
	static final int TYPE_ADD_LINE = 2;

	@Expose
	@SerializedName("type")
	int type;

	@Expose
	@SerializedName("id1")
	long id1;

	@Expose
	@SerializedName("x1")
	float x1;

	@Expose
	@SerializedName("y1")
	float y1;

	@Expose
	@SerializedName("id2")
	long id2;

	@Expose
	@SerializedName("x2")
	float x2;

	@Expose
	@SerializedName("y2")
	float y2;

	SettingsMapView.Action toAction(HashMap<Long, Point> points) {
		SettingsMapView.Action action = null;

		if (type == TYPE_ADD_POINT) {
			Point point = getOrCreatePoint(points, id1, x1, y1);

			action = new SettingsMapView.AddPointAction(point);
		}
		else if (type == TYPE_ADD_LINE) {
			Point p1 = getOrCreatePoint(points, id1, x1, y1);
			Point p2 = getOrCreatePoint(points, id2, x2, y2);

			action = new SettingsMapView.AddLineAction(p1, p2);
		}

		Objects.requireNonNull(action);

		return action;
	}

	private Point getOrCreatePoint(HashMap<Long, Point> points, long id, float x, float y) {
		Point point = points.get(id);

		if (point == null) {
			point = new Point(x, y);
			points.put(id, point);
		}

		return point;
	}
}
