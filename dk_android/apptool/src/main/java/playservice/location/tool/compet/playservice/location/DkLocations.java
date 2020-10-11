/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.playservice.location;

import android.location.Address;

import com.google.android.gms.location.places.Place;

import org.json.JSONArray;
import org.json.JSONObject;

import tool.compet.core.util.DkLogs;

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

	public static DkLocation place2myLocation(Place place) {
		if (place == null) {
			return null;
		}
		DkLocation loc = new DkLocation();
		CharSequence cs = place.getAddress();
		loc.setLatLng(place.getLatLng());
		loc.address = cs == null ? "" : cs.toString();

		return loc;
	}

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
		} catch (Exception e) {
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
