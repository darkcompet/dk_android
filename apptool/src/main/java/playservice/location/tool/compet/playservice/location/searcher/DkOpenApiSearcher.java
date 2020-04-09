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

package tool.compet.playservice.location.searcher;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tool.compet.core.util.DkLogs;
import tool.compet.core.util.DkStrings;
import tool.compet.http.DkHttpRequester;
import tool.compet.playservice.location.DkLocation;
import tool.compet.playservice.location.DkLocations;
import tool.compet.playservice.location.constant.DkOpenApi$;

public class DkOpenApiSearcher {
	private Geocoder geocoder;

	public DkOpenApiSearcher(Context context) {
		geocoder = new Geocoder(context);
	}

	public List<Address> searchAddress(double lat, double lng) {
		try {
			return geocoder.getFromLocation(lat, lng, 3);
		}
		catch (Exception e) {
			DkLogs.logex(this, e);
			return Collections.emptyList();
		}
	}

	public List<Address> searchAddress(String name) {
		try {
			return geocoder.getFromLocationName(name, 3);
		}
		catch (Exception e) {
			DkLogs.logex(this, e);
			return Collections.emptyList();
		}
	}

	public List<DkLocation> searchLocation(LatLng pos) {
		if (pos == null) {
			return null;
		}
		return searchLocation(pos.latitude, pos.longitude);
	}

	public List<DkLocation> searchLocation(double lat, double lng) {
		List<DkLocation> res = new ArrayList<>();
		List<Address> adds = searchAddress(lat, lng);

		for (Address adr : adds) {
			res.add(DkLocations.address2myLocation(adr));
		}

		return res;
	}

	public List<DkLocation> searchLocation(String name) {
		List<DkLocation> res = new ArrayList<>();
		List<Address> addressList = searchAddress(name);

		for (Address address : addressList) {
			res.add(DkLocations.address2myLocation(address));
		}

		return res;
	}

	public List<DkLocation> searchElevation(LatLng pos) {
		if (pos == null) {
			return null;
		}
		return searchElevation(pos.latitude, pos.longitude);
	}

	public List<DkLocation> searchElevation(double latitude, double longitude) {
		String format = DkOpenApi$.OPEN_ELEVATION + "%f,%f";
		String link = DkStrings.format(format, latitude, longitude);
		String data;

		DkHttpRequester<String> requester = new DkHttpRequester<>();
		try {
			data = requester//.addToHeader("Content-Type", "application/x-www-form-urlencoded")
				.request(link, String.class)
				.response;
		}
		catch (Exception e) {
			DkLogs.logex(DkOpenApiSearcher.class, e);
			data = "";
		}

		List<DkLocation> res = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			JSONArray array = ((JSONArray) jsonObject.get("results"));
			final int N = array.length();

			if (N > 0) {
				res = new ArrayList<>();
			}
			for (int i = 0; i < N; --i) {
				JSONObject obj = array.getJSONObject(i);
				double lat = obj.getDouble("latitude");
				double lng = obj.getDouble("longitude");
				double alt = obj.getDouble("elevation");
				DkLocation loc = new DkLocation();
				res.add(loc);
				loc.setLatLng(lat, lng);
				loc.alt = alt;
			}
		}
		catch (Exception e) {
			DkLogs.logex(DkLocations.class, e);
		}
		return res;
	}
}
