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

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import tool.compet.core.util.Dks;

import static tool.compet.core.constant.Dk$.ACCESS_COARSE_LOCATION;
import static tool.compet.core.constant.Dk$.ACCESS_FINE_LOCATION;

public class DKLocationTracker {
	private long minTimePeriodUpdate = 1000;
	private float minDistanceUpdate = 0.1f;
	private LocationManager locationManager;
	private Location location;
	private LatLng latLng;
	private Criteria criteria;
	private Listener listener;

	public DKLocationTracker(Context context) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(true);
	}

	public interface Listener {
		void onStatusChanged(String provider, int status, Bundle extras);
		void onProviderEnabled(String provider);
		void onProviderDisabled(String provider);
		void onLocationChanged(Location location);
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public Location getLocation() {
		return location;
	}

	public LatLng getLatlng() {
		return latLng;
	}

	public Location start(Context context) {
		if (!Dks.checkPermission(context, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)) {
			return null;
		}
		String bestProviderName = locationManager.getBestProvider(criteria, true);
		if (bestProviderName == null) bestProviderName = LocationManager.GPS_PROVIDER;

		locationManager.requestLocationUpdates(bestProviderName,
			minTimePeriodUpdate,
			minDistanceUpdate,
			locationListener);
		location = locationManager.getLastKnownLocation(bestProviderName);

		String networkProviderName = LocationManager.NETWORK_PROVIDER;
		boolean networkEnabled = locationManager.isProviderEnabled(networkProviderName);
		if (location == null && networkEnabled) {
			locationManager.requestLocationUpdates(networkProviderName,
				minTimePeriodUpdate,
				minDistanceUpdate,
				locationListener);
			location = locationManager.getLastKnownLocation(networkProviderName);
		}

		if (location != null) {
			latLng = new LatLng(location.getLatitude(), location.getLongitude());
		}
		return location;
	}

	public void stop() {
		locationManager.removeUpdates(locationListener);
		location = null;
		latLng = null;
	}

	public DKLocationTracker setIntervalTimeUpdate(long millis) {
		if (millis > 0) minTimePeriodUpdate = millis;
		return this;
	}

	public DKLocationTracker setDistanceUpdate(float meter) {
		if (meter > 0) minDistanceUpdate = meter;
		return this;
	}

	private LocationListener locationListener = new LocationListener() {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (listener != null) listener.onStatusChanged(provider, status, extras);
		}
		@Override
		public void onProviderEnabled(String provider) {
			if (listener != null) listener.onProviderEnabled(provider);
		}
		@Override
		public void onProviderDisabled(String provider) {
			if (listener != null) listener.onProviderDisabled(provider);
		}
		@Override
		public void onLocationChanged(Location location) {
			synchronized (this) {
				DKLocationTracker.this.location = location;

				if (location != null) {
					latLng = new LatLng(location.getLatitude(), location.getLongitude());
				}
			}
			if (listener != null) listener.onLocationChanged(location);
		}
	};
}
