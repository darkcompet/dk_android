package compet.library.presentation.settingsMap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tool.compet.core.util.DkLogs;

public class SettingsMap {
	@Expose
	@SerializedName("areaWidth")
	float areaWidth;

	@Expose
	@SerializedName("areaHeight")
	float areaHeight;

	@Expose
	@SerializedName("actionInfos")
	private MapActionInfo[] actionInfos;

	private HashMap<Long, Point> points = new HashMap<>();

	void prepareCaching(float areaWidth, float areaHeight, List<SettingsMapView.Action> actions) {
		this.areaWidth = areaWidth;
		this.areaHeight = areaHeight;
		this.actionInfos = new MapActionInfo[actions.size()];

		for (int i = actions.size() - 1; i >= 0; --i) {
			SettingsMapView.Action action = actions.get(i);
			MapActionInfo info = new MapActionInfo();

			if (action instanceof SettingsMapView.AddPointAction) {
				SettingsMapView.AddPointAction addAction = (SettingsMapView.AddPointAction) action;

				info.type = MapActionInfo.TYPE_ADD_POINT;
				info.id1 = addAction.point.id;
				info.x1 = addAction.point.x;
				info.y1 = addAction.point.y;
			}
			else if (action instanceof SettingsMapView.AddLineAction) {
				SettingsMapView.AddLineAction lineAction = (SettingsMapView.AddLineAction) action;

				info.type = MapActionInfo.TYPE_ADD_LINE;
				info.id1 = lineAction.p1.id;
				info.x1 = lineAction.p1.x;
				info.y1 = lineAction.p1.y;

				info.id2 = lineAction.p2.id;
				info.x2 = lineAction.p2.x;
				info.y2 = lineAction.p2.y;
			}
			else {
				DkLogs.complain(this, "invalid action");
			}

			actionInfos[i] = info;
		}
	}

	List<SettingsMapView.Action> getActions() {
		List<SettingsMapView.Action> actions = new ArrayList<>();

		if (actionInfos != null) {
			for (MapActionInfo info : actionInfos) {
				actions.add(info.toAction(points));
			}
		}

		return actions;
	}
}
