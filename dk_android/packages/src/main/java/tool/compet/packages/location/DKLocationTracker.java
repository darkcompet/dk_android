/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import tool.compet.core.constant.DkConst;
import tool.compet.core.util.DkUtils;

/**
 * This is location tracker which uses `LocationManager` of framework.
 */
public class DKLocationTracker {
	public interface Listener {
		void onStatusChanged(String provider, int status, Bundle extras);
		void onProviderEnabled(String provider);
		void onProviderDisabled(String provider);
		void onLocationChanged(Location location);
	}

	private long minTimePeriodUpdate = 1000;
	private float minDistanceUpdate = 0.1f;
	private final LocationManager locationManager;
	private Location curLocation; // current location
	private LatLng curLatLng; // current latlng
	private final Criteria criteria;
	private Listener listener;

	public DKLocationTracker(Context context) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(true);
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	public Location getLocation() {
		return curLocation;
	}

	public LatLng getLatlng() {
		return curLatLng;
	}

	/**
	 * Note: before call this, should check 2 permissions: DkConst.ACCESS_FINE_LOCATION, DkConst.ACCESS_COARSE_LOCATION.
	 */
	public Location start(Context context) {
		if (! DkUtils.checkPermission(context, DkConst.ACCESS_FINE_LOCATION, DkConst.ACCESS_COARSE_LOCATION)) {
			return null;
		}
		String bestProviderName = locationManager.getBestProvider(criteria, true);
		if (bestProviderName == null) {
			bestProviderName = LocationManager.GPS_PROVIDER;
		}
		locationManager.requestLocationUpdates(
			bestProviderName,
			minTimePeriodUpdate,
			minDistanceUpdate,
			locationListener
		);
		curLocation = locationManager.getLastKnownLocation(bestProviderName);

		String networkProviderName = LocationManager.NETWORK_PROVIDER;
		boolean networkEnabled = locationManager.isProviderEnabled(networkProviderName);
		if (curLocation == null && networkEnabled) {
			locationManager.requestLocationUpdates(
				networkProviderName,
				minTimePeriodUpdate,
				minDistanceUpdate,
				locationListener
			);
			curLocation = locationManager.getLastKnownLocation(networkProviderName);
		}

		if (curLocation != null) {
			curLatLng = new LatLng(curLocation.getLatitude(), curLocation.getLongitude());
		}
		return curLocation;
	}

	public void stop() {
		locationManager.removeUpdates(locationListener);
		curLocation = null;
		curLatLng = null;
	}

	public DKLocationTracker setIntervalTimeUpdate(long millis) {
		if (millis > 0) {
			minTimePeriodUpdate = millis;
		}
		return this;
	}

	public DKLocationTracker setDistanceUpdate(float meter) {
		if (meter > 0) {
			minDistanceUpdate = meter;
		}
		return this;
	}

	private final LocationListener locationListener = new LocationListener() {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (listener != null) {
				listener.onStatusChanged(provider, status, extras);
			}
		}
		@Override
		public void onProviderEnabled(String provider) {
			if (listener != null) {
				listener.onProviderEnabled(provider);
			}
		}
		@Override
		public void onProviderDisabled(String provider) {
			if (listener != null) {
				listener.onProviderDisabled(provider);
			}
		}
		@Override
		public void onLocationChanged(Location location) {
			synchronized (this) {
				DKLocationTracker.this.curLocation = location;
				if (location != null) {
					curLatLng = new LatLng(location.getLatitude(), location.getLongitude());
				}
			}
			if (listener != null) {
				listener.onLocationChanged(location);
			}
		}
	};
}
