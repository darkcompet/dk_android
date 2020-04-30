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

import android.location.Location;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import tool.compet.core.util.DkStrings;

public class DkLocation {
	private double lat, lng;
	private LatLng latLng;

	public String address;
	public double alt;
	public float bearing;
	public float speed;
	public float accuracy;
	public float tilt;
	public float zoom;
	public double resolution;

	public DkLocation() {
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DkLocation) {
			DkLocation that = (DkLocation) obj;
			boolean sameLatLng = (this.lat == that.lat) && (this.lng == that.lng);
			boolean samePlace = DkStrings.isEquals(this.address, that.address);
			return sameLatLng && samePlace;
		}
		return false;
	}

	public void setLatLng(double lat, double lng) {
		this.latLng = new LatLng(lat, lng);
		this.lat = lat;
		this.lng = lng;
	}

	public void setLatLng(LatLng latLng) {
		if (latLng != null) {
			this.latLng = latLng;
			this.lat = latLng.latitude;
			this.lng = latLng.longitude;
		}
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public void updateLocation(Location loc) {
		if (loc != null) {
			this.lat = loc.getLatitude();
			this.lng = loc.getLongitude();
			this.latLng = new LatLng(this.lat, this.lng);
			this.alt = loc.getAltitude();
			this.bearing = loc.getBearing();
			this.speed = loc.getSpeed();
			this.accuracy = loc.getAccuracy();
		}
	}

	public void updateLocation(CameraPosition pos) {
		if (pos != null) {
			this.lat = pos.target.latitude;
			this.lng = pos.target.longitude;
			this.latLng = new LatLng(this.lat, this.lng);
			this.bearing = pos.bearing;
			this.tilt = pos.tilt;
			this.zoom = pos.zoom;
		}
	}
}
