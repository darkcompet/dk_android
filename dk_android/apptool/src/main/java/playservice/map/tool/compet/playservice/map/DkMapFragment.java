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

package tool.compet.playservice.map;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import tool.compet.core.type.DkCallback;

/**
 * Abstract Google maps class. It provides basic implementations for features of Gmaps.
 * You can override them to customize whatever you want.
 */
public abstract class DkMapFragment extends SupportMapFragment implements OnMapReadyCallback {
	protected GoogleMap map;
	protected Listener listener;
	protected float zoomLevel = 17;
	protected float zoomDiff = 0.5f;
	protected boolean is3d;

   public interface Listener {
		void onMapReady(GoogleMap map);

		// it is called frequent
		void onCameraChangeListener(CameraPosition position);

		// it is sometimes called, not frequent
		void onCameraMoveListener(CameraPosition position);
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		super.getMapAsync(this);
	}

	public void getMapAsync() {
		super.getMapAsync(this);
	}

	@Override
	public void getMapAsync(OnMapReadyCallback onMapReadyCallback) {
		super.getMapAsync(this);
	}

	@Override
	public void onMapReady(final GoogleMap map) {
		if (this.map == null) {
			this.map = map;
		}
	}

	public void moveCameraTo(LatLng dst) {
		if (map != null) {
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(dst, zoomLevel));
		}
	}

	public Marker addMarker(Bitmap icon, LatLng latLng) {
		if (map == null) {
			return null;
		}
		MarkerOptions markerOptions = new MarkerOptions()
			.position(latLng)
			.icon(BitmapDescriptorFactory.fromBitmap(icon))
			.anchor(.5f, .5f);

		return map.addMarker(markerOptions);
	}

	public Marker setMarker(Marker marker, Bitmap icon, LatLng pos) {
		if (icon != null && pos != null) {
			if (marker != null) {
				marker.remove();
			}
			return addMarker(icon, pos);
		}
		return null;
	}

	public void rotateMap(double degrees, LatLng pos) {
		if (map == null) {
			return;
		}
		if (pos == null) {
			pos = map.getCameraPosition().target;
		}

		CameraPosition.Builder builder = new CameraPosition.Builder()
			.target(pos)
			.bearing((float) degrees)
			.tilt(is3d ? 90f : 0f)
			.zoom(zoomLevel);

		map.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
	}

	public void turn3d(boolean on, double degrees) {
		is3d = on;
		rotateMap(degrees, null);
	}

	public boolean is3D() {
		return is3d;
	}

	public DkMapFragment zoom(boolean larger) {
		if (map != null) {
			zoomLevel += larger ? zoomDiff : -zoomDiff;

			if (zoomLevel > map.getMaxZoomLevel()) {
				zoomLevel = map.getMaxZoomLevel();
			}
			else if (zoomLevel < map.getMinZoomLevel()) {
				zoomLevel = map.getMinZoomLevel();
			}

			map.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
		}
		return this;
	}

	public DkMapFragment setMapType(int mapType) {
		if (map != null) {
			map.setMapType(mapType);
		}
		return this;
	}

	public void addCircle(LatLng center, double radius, float strokeWidth, int strokeColor) {
		if (map == null) {
			return;
		}
		map.addCircle(new CircleOptions().center(center)
			.radius(radius)
			.strokeWidth(strokeWidth)
			.strokeColor(strokeColor)
			.visible(true));
	}

	public void getSnapshot(final DkCallback<Bitmap> callback) {
		if (callback == null) {
			return;
		}
		if (map == null) {
			callback.call(null);
		}
		else {
			map.snapshot(callback::call);
		}
	}

	public void showMap(boolean isShow) {
		FragmentManager fm = getFragmentManager();

		if (fm != null) {
			if (isShow) {
				fm.beginTransaction().show(this).commit();
			}
			else {
				fm.beginTransaction().hide(this).commit();
			}
		}
	}

	public void setZoomDiff(float diff) {
		zoomDiff = Math.abs(diff);
	}
}