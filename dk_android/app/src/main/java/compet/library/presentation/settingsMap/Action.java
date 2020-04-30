package compet.library.presentation.settingsMap;

import java.util.List;

public abstract class Action {
	abstract void delete(List<Point> points);
	abstract void recovery(List<Point> points);
}
