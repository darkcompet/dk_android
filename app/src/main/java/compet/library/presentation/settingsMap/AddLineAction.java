package compet.library.presentation.settingsMap;

import java.util.List;

public class AddLineAction extends Action {
	Point p1;
	Point p2;

	AddLineAction(Point p1, Point p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	@Override
	void delete(List<Point> points) { // delete line
		p1.removeNeighbour(p2);
	}

	@Override
	void recovery(List<Point> points) { // recovery line
		p1.addNeighbour(p2);
	}
}
