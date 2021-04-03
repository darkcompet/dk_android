/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.packages.map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tool.compet.core.DkLogs;
import tool.compet.packages.location.DkLocation;
import tool.compet.packages.location.DkLocations;

/**
 * Search address with `Geocoder` of Android framework.
 */
public class DkGeoSearcher {
	private final Geocoder geocoder;

	public DkGeoSearcher(Context context) {
		geocoder = new Geocoder(context);
	}

	public List<Address> searchAddress(double lat, double lng) {
		try {
			return geocoder.getFromLocation(lat, lng, 3);
		}
		catch (Exception e) {
			DkLogs.error(this, e);
			return Collections.emptyList();
		}
	}

	public List<Address> searchAddress(String name) {
		try {
			return geocoder.getFromLocationName(name, 3);
		}
		catch (Exception e) {
			DkLogs.error(this, e);
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
}
