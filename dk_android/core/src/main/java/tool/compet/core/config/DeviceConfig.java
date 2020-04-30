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

package tool.compet.core.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.Locale;

/**
 * Information for current device.
 */
public class DeviceConfig {
	public String lang; // eg: vi, ja
	public String country; // eg: VN, JP
	public Locale locale;

	// Screen dimension in pixel
	public int[] displaySize;

	// density for dimension calculation
	public float density;

	// density which is expressed as Dot-per-inch
	public int densityDpi;

	// density for fontsize calculation
	public float scaledDensity;

	DeviceConfig() {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();

		displaySize = new int[] {metrics.widthPixels, metrics.heightPixels};
		density = metrics.density;
		densityDpi = metrics.densityDpi;
		scaledDensity = metrics.scaledDensity;

		Locale locale = Resources.getSystem().getConfiguration().locale;
		lang = locale.getLanguage();
		country = locale.getCountry();
	}

	public int[] getDisplaySizeInPixel() {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return new int[] {metrics.widthPixels, metrics.heightPixels};
	}

	public double[] getDisplaySizeInInches() {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return new double[] {metrics.widthPixels / metrics.xdpi, metrics.heightPixels / metrics.ydpi};
	}

	@SuppressLint("HardwareIds")
	public String getDeviceId(Context context) {
		return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
	}

	public int dp2px(int dp) {
		return Math.round((float) dp * densityDpi / DisplayMetrics.DENSITY_DEFAULT);
	}

	public int px2dp(int px) {
		return Math.round((float) px * DisplayMetrics.DENSITY_DEFAULT / densityDpi);
	}

	public float px2mm(Context context, float px) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return px / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, dm);
	}
}
