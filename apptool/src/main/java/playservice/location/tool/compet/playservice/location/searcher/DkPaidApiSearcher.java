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

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tool.compet.core.util.DkStrings;
import tool.compet.http.DkHttpRequester;
import tool.compet.playservice.location.DkLocation;
import tool.compet.playservice.location.DkLocations;
import tool.compet.playservice.location.constant.DkPaidApi$;

import static java.util.Locale.US;
import static tool.compet.core.BuildConfig.DEBUG;
import static tool.compet.core.util.DkLogs.log;
import static tool.compet.core.util.DkLogs.logex;
import static tool.compet.core.util.DkStrings.isWhite;

public class DkPaidApiSearcher implements DkPaidApi$ {
	private String gmapKey;

	public DkPaidApiSearcher(String gmapKey) {
		this.gmapKey = gmapKey;
	}

	public List<DkLocation> searchLocation(LatLng latLng, String address, boolean useGeo, boolean usePlace, boolean useText, boolean useAuto) {
		List<DkLocation> res = new ArrayList<>();

		String tmpAddress = address;
		if (!isWhite(tmpAddress)) {
			tmpAddress = tmpAddress.trim().replaceAll(" ", "%20");
		}

		final String finalAddress = tmpAddress;

		String geoLink = "", placeLink = "", textLink = "", autoLink = "";
		String keyParam = String.format(US, "key=%s&radius=500", gmapKey);

		if (useGeo) {
			String geoParam = keyParam;
			if (!isWhite(finalAddress)) {
				geoParam += String.format(US, "&address=%s&sensor=false", finalAddress);
			}
			if (latLng != null) {
				geoParam += String.format(US, "&latlng=%f,%f", latLng.latitude, latLng.longitude);
			}
			geoLink = URL_GEO + geoParam;
		}

		if (usePlace) {
			String placeParam = keyParam;
			if (!isWhite(finalAddress)) {
				placeParam += String.format(US, "&name=%s&sensor=false", finalAddress);
			}
			if (latLng != null) {
				placeParam += String.format(US, "&location=%f,%f", latLng.latitude, latLng.longitude);
			}
			placeLink = URL_PLACES + placeParam;
		}

		if (useText) {
			String textParam = keyParam;
			if (!isWhite(finalAddress)) {
				textParam += String.format(US, "&query=%s&sensor=false", finalAddress);
			}
			if (latLng != null) {
				textParam += String.format(US, "&location=%f,%f", latLng.latitude, latLng.longitude);
			}
			textLink = URL_PLACES_TEXT + textParam;
		}

		if (useAuto) {
			String autoParam = keyParam;
			if (!isWhite(finalAddress)) {
				autoParam += String.format(US, "&input=%s&sensor=false", finalAddress);
			}
			if (latLng != null) {
				autoParam += String.format(US, "&location=%f,%f", latLng.latitude, latLng.longitude);
			}
			autoLink = URL_PLACES_AUTOCOMPLETE + autoParam;
		}

		if (useGeo) {
			String jsonGeo = getResponse(geoLink);
			DkLocation[] geoLoc = DkLocations.jsonGeo2myLocation(jsonGeo);
			if (geoLoc != null && geoLoc.length > 0) {
				res.add(geoLoc[0]);
			}
		}

		if (usePlace) {
			String jsonPlace = getResponse(placeLink);
			DkLocation[] placeLoc = DkLocations.jsonPlace2myLocation(jsonPlace);
			if (placeLoc != null) {
				Collections.addAll(res, placeLoc);
			}
		}

		if (useText) {
			String jsonText = getResponse(textLink);
			DkLocation[] textLoc = DkLocations.jsonPlace2myLocation(jsonText);
			if (textLoc != null) {
				Collections.addAll(res, textLoc);
			}
		}

		if (useAuto) {
			String jsonAuto = getResponse(autoLink);
			DkLocation[] autoLoc = DkLocations.jsonAutoComplete2myLocation(jsonAuto);
			if (autoLoc != null) {
				Collections.addAll(res, autoLoc);
			}
		}

		if (DEBUG) log(DkLocations.class, "geo: %s\nplace: %s\ntext: %s\nauto: %s",
			geoLink, placeLink, textLink, autoLink);

		return res;
	}

	private String getResponse(String link) {
		try {
			DkHttpRequester<String> req = new DkHttpRequester<>();
			return req.request(link, String.class).response;
		}
		catch (Exception e) {
			logex(DkLocations.class, e);
			return "";
		}
	}

	public DkLocation[] searchAltitude(LatLng latLng) {
		if (latLng == null) return null;
		return searchAltitude(latLng.latitude, latLng.longitude);
	}

	public DkLocation[] searchAltitude(double latitude, double longitude) {
		String format = URL_ALTITUDE + "key=%s&locations=%f,%f";
		String link = DkStrings.format(format, gmapKey, latitude, longitude);
		String data;

		DkHttpRequester<String> requester = new DkHttpRequester<>();
		try {
			data = requester
				.addToHeader("Content-Type", "application/x-www-form-urlencoded")
				.request(link, String.class)
				.response;
		} catch (Exception e) {
			logex(DkPaidApiSearcher.class, e);
			data = "";
		}

		DkLocation[] res = null;
		try {
			JSONObject jsonObject = new JSONObject(data);
			JSONArray array = ((JSONArray) jsonObject.get("results"));
			final int N = array.length();
			res = new DkLocation[N];

			for (int i = 0; i < N; ++i) {
				JSONObject object = array.getJSONObject(i);
				double alt = object.getDouble("elevation");
				double lat = object.getJSONObject("location").getDouble("lat");
				double lng = object.getJSONObject("location").getDouble("lng");
				double resolution = object.getDouble("resolution");
				res[i] = new DkLocation();
				res[i].setLatLng(lat, lng);
				res[i].alt = alt;
				res[i].resolution = resolution;
			}
		} catch (Exception e) {
			logex(DkLocations.class, e);
		}
		return res;
	}
}
