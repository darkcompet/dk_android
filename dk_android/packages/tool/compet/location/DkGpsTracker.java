/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.location;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import tool.compet.core.DkConst;
import tool.compet.core.DkLogs;
import tool.compet.core.DkUtils;

import static tool.compet.core.BuildConfig.DEBUG;

/**
 * This is location tracker which uses Google api provider.
 */
public class DkGpsTracker extends LocationCallback implements
	GoogleApiClient.ConnectionCallbacks,
	GoogleApiClient.OnConnectionFailedListener, LocationListener {

	public interface Listener {
		void onGoogleServiceConnected();

		void onLastLocationUpdated(Location location);

		void onLocationResult(@NonNull List<Location> locations);

		void onLocationChanged(Location location);
	}

	private static final ArrayMap<String, DkGpsTracker> trackers = new ArrayMap<>();
	private FragmentActivity host;
	private long requestIntervalTime = 10000;
	private long requestFastestIntervalTime = 5000;
	private FusedLocationProviderClient providerClient;
	private LocationRequest locationRequest;
	private ArrayList<Listener> listeners = new ArrayList<>();

	private DkGpsTracker(FragmentActivity host) {
		this.host = host;

		GoogleApiClient googleApiClient = new GoogleApiClient.Builder(host)
			.enableAutoManage(host, this)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(LocationServices.API)
			.build();

		LocationRequest locationRequest = this.locationRequest = LocationRequest.create();
		locationRequest.setInterval(requestIntervalTime);
		locationRequest.setFastestInterval(requestFastestIntervalTime);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		providerClient = LocationServices.getFusedLocationProviderClient(host);

		googleApiClient.connect();
		requestLastLocation();
	}

	public static DkGpsTracker getIns(FragmentActivity host) {
		final String key = host.getClass().getName();

		synchronized (DkGpsTracker.class) {
			DkGpsTracker tracker = trackers.get(key);

			if (tracker == null) {
				tracker = new DkGpsTracker(host);
				trackers.put(key, tracker);
			}

			return tracker;
		}
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		for (Listener listener : listeners) {
			listener.onGoogleServiceConnected();
		}
	}

	@Override
	public void onLocationResult(@NonNull LocationResult result) {
		for (Listener listener : listeners) {
			listener.onLocationResult(result.getLocations());
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		for (Listener listener : listeners) {
			listener.onLocationChanged(location);
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		if (DEBUG) {
			DkLogs.info(this, "onConnectionSuspended: " + i);
		}
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult result) {
		if (DEBUG) {
			DkLogs.info(this, "onConnectionFailed !!");
		}
	}

	public DkGpsTracker register(Listener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
		return this;
	}

	public DkGpsTracker unregister(Listener listener) {
		listeners.remove(listener);
		return this;
	}

	public void start() {
		if (!DkUtils.checkPermission(host, DkConst.ACCESS_FINE_LOCATION, DkConst.ACCESS_COARSE_LOCATION)) {
			DkLogs.warning(this, "Could not start gps tracker since lack of permission");
			return;
		}
		providerClient.requestLocationUpdates(locationRequest, this, null);
	}

	public void stop() {
		providerClient.removeLocationUpdates(this);
	}

	public void requestLastLocation() {
		if (!DkUtils.checkPermission(host, DkConst.ACCESS_FINE_LOCATION, DkConst.ACCESS_COARSE_LOCATION)) {
			return;
		}
		providerClient.getLastLocation().addOnCompleteListener(host, (Task<Location> task) -> {
			if (task.isSuccessful() && task.getResult() != null) {
				for (Listener listener : listeners) {
					listener.onLastLocationUpdated(task.getResult());
				}
			}
		});
	}

	public void setRequestIntervalTime(long requestIntervalTime) {
		this.requestIntervalTime = requestIntervalTime;
		locationRequest.setInterval(requestIntervalTime);
	}

	public void setRequestFastestIntervalTime(long requestFastestIntervalTime) {
		this.requestFastestIntervalTime = requestFastestIntervalTime;
		locationRequest.setFastestInterval(requestFastestIntervalTime);
	}

	public void onDestroy() {
		trackers.remove(host.getClass().getName());
		host = null;
	}
}
