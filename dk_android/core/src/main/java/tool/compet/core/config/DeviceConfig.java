/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
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
 * Config of current device.
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

		displaySize = new int[]{metrics.widthPixels, metrics.heightPixels};
		density = metrics.density;
		densityDpi = metrics.densityDpi;
		scaledDensity = metrics.scaledDensity;

		Locale locale = Resources.getSystem().getConfiguration().locale;
		lang = locale.getLanguage();
		country = locale.getCountry();
	}

	public int[] getDisplaySizeInPixel() {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return new int[]{metrics.widthPixels, metrics.heightPixels};
	}

	public double[] getDisplaySizeInInches() {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return new double[]{metrics.widthPixels / metrics.xdpi, metrics.heightPixels / metrics.ydpi};
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
