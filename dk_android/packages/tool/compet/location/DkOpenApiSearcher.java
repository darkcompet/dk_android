/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.location;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tool.compet.core.DkLogs;
import tool.compet.core.DkStrings;
import tool.compet.http.DkHttpRequester;

/**
 * Search location (address, elevation...) with open api.
 */
public class DkOpenApiSearcher {
	private static final String OPEN_ELEVATION = "https://api.open-elevation.com/api/v1/lookup?locations=";
	private static final String ELEVATION_IO = "https://elevation-api.io/api/elevation?points=";

	public static List<DkLocation> searchElevation(LatLng pos) {
		if (pos == null) {
			return null;
		}
		return searchElevation(pos.latitude, pos.longitude);
	}

	public static List<DkLocation> searchElevation(double latitude, double longitude) {
		String format = OPEN_ELEVATION + "%f,%f";
		String link = DkStrings.format(format, latitude, longitude);
		String data;

		DkHttpRequester<String> requester = new DkHttpRequester<>();
		try {
			data = requester//.addToHeader("Content-Type", "application/x-www-form-urlencoded")
				.request(link, String.class)
				.response;
		}
		catch (Exception e) {
			DkLogs.error(DkOpenApiSearcher.class, e);
			data = "";
		}

		List<DkLocation> result = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			JSONArray array = ((JSONArray) jsonObject.get("results"));
			final int N = array.length();

			if (N > 0) {
				result = new ArrayList<>();
			}
			for (int index = 0; index < N; ++index) {
				JSONObject obj = array.getJSONObject(index);
				double lat = obj.getDouble("latitude");
				double lng = obj.getDouble("longitude");
				double alt = obj.getDouble("elevation");

				DkLocation loc = new DkLocation();
				loc.setLatLng(lat, lng);
				loc.alt = alt;

				result.add(loc);
			}
		}
		catch (Exception e) {
			DkLogs.error(DkLocations.class, e);
		}
		return result;
	}
}
