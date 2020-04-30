package compet.library.presentation.settingsMap;

import java.util.List;

public class AddPointAction extends Action {
	Point point;

	AddPointAction(Point point) {
		this.point = point;
	}

	@Override
	void delete(List<Point> points) {
		points.remove(point);
	}

	@Override
	void recovery(List<Point> points) {
		points.add(point);
	}
}
