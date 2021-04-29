/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.location;

import android.location.Address;

//import com.google.android.gms.location.places.Place;

import org.json.JSONArray;
import org.json.JSONObject;

import tool.compet.core.DkLogs;

public class DkLocations {
	public static DkLocation[] jsonAutoComplete2myLocation(String jsonAuto) {
		//todo
		return null;
	}

	public static DkLocation address2myLocation(Address address) {
		if (address == null) {
			return null;
		}
		String formatted_address = "";

		if (address.getMaxAddressLineIndex() >= 0) {
			formatted_address = address.getAddressLine(0);
		}

		DkLocation loc = new DkLocation();
		loc.setLatLng(address.getLatitude(), address.getLongitude());
		loc.address = formatted_address;

		return loc;
	}

//	public static DkLocation place2myLocation(Place place) {
//		if (place == null) {
//			return null;
//		}
//		DkLocation loc = new DkLocation();
//		CharSequence cs = place.getAddress();
//		loc.setLatLng(place.getLatLng());
//		loc.address = cs == null ? "" : cs.toString();
//
//		return loc;
//	}

	public static DkLocation[] jsonGeo2myLocation(String jsonGeo) {
		try {
			JSONObject jsonObject = new JSONObject(jsonGeo);
			JSONArray array = ((JSONArray) jsonObject.get("results"));
			final int N = array.length();
			DkLocation[] res = new DkLocation[N];
			for (int i = 0; i < N; ++i) {
				JSONObject object = array.getJSONObject(i);

				double lat = object.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
				double lon = object.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
				res[i] = new DkLocation();
				res[i].setLatLng(lat, lon);
				res[i].address = object.getString("formatted_address");
			}
			return res;
		}
		catch (Exception e) {
			DkLogs.error(DkLocations.class, e);
			return null;
		}
	}

	public static DkLocation[] jsonPlace2myLocation(String jsonPlace) {
		try {
			JSONObject jsonObject = new JSONObject(jsonPlace);
			JSONArray array = ((JSONArray) jsonObject.get("results"));
			int N = array.length();
			DkLocation[] res = new DkLocation[N];

			for (int i = 0; i < N; ++i) {
				JSONObject object = array.getJSONObject(i);
				double lat = object.getJSONObject("geometry").getJSONObject("location").getDouble("latitude");
				double lon = object.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
				res[i] = new DkLocation();
				res[i].setLatLng(lat, lon);
				res[i].address = object.getString("name");
			}

			return res;
		}
		catch (Exception e) {
			DkLogs.error(DkLocations.class, e);
			return null;
		}
	}
}
