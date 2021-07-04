/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.Locale;

/**
 * Configuration for both system (os) and app.
 */
public class DkConfig {
	/**
	 * @return Device language code, for eg,. "vi", "en", "ja", ...
	 */
	public static String systemLang() {
		// Or: Locale.getDefault().getLanguage()
		return Resources.getSystem().getConfiguration().locale.getLanguage();
	}

	/**
	 * @return Device country code, for eg,. "VN", "JP", "US", ....
	 */
	public static String systemCountry() {
		return Resources.getSystem().getConfiguration().locale.getCountry();
	}

	/**
	 * This uses system-resources o retrieve system locale.
	 *
	 * @return System locale of current device.
	 */
	public static Locale systemLocale() {
		// Resources.getSystem(): a global shared Resources object that provides access to only system resources (no application resources),
		// is not configured for the current screen (can not use dimension units, does not change based on orientation, etc),
		// and is not affected by Runtime Resource Overlay.
		Configuration config = Resources.getSystem().getConfiguration();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			return config.getLocales().get(0);
		}

		return config.locale;
	}

	/**
	 * This uses context-resources o retrieve app locale.
	 *
	 * @return App locale of current app.
	 */
	public static Locale appLocale(Context context) {
		// context.getResources(): Resources instance for the application's package.
		// Implementations of this method should return a Resources instance that is consistent with the AssetManager
		// instance returned by getAssets(). For example, they should share the same Configuration object.
		Configuration config = context.getResources().getConfiguration();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			return config.getLocales().get(0);
		}

		return config.locale;
	}

	/**
	 * @return Dimension [width, height] in pixel of device screen.
	 */
	public static int[] displaySize() {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return new int[] {metrics.widthPixels, metrics.heightPixels};
	}

	/**
	 * @return Dimension [width, height] in inches of device screen.
	 */
	public double[] displaySizeInInches() {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return new double[] {metrics.widthPixels / metrics.xdpi, metrics.heightPixels / metrics.ydpi};
	}

	/**
	 * @return Density (dp) for dimension calculation.
	 */
	public static float density() {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return metrics.density;
	}

	/**
	 * @return Density which is expressed as Dot-per-inch.
	 * For eg,. `DisplayMetrics.DENSITY_LOW`, `DisplayMetrics.DENSITY_MEDIUM`, `DisplayMetrics.DENSITY_HIGH`
	 */
	public static int densityDpi() {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return metrics.densityDpi;
	}

	/**
	 * @return Density (sp) for fontsize calculation.
	 */
	public static float scaledDensity() {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return metrics.scaledDensity;
	}

	public static int dp2px(int dp) {
		return Math.round((float) dp * densityDpi() / DisplayMetrics.DENSITY_DEFAULT);
	}

	public static int px2dp(int px) {
		return Math.round((float) px * DisplayMetrics.DENSITY_DEFAULT / densityDpi());
	}

	public static float px2mm(Context context, float px) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return px / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, dm);
	}

	/**
	 * @return Unique device id.
	 */
	@SuppressLint("HardwareIds")
	public String deviceId(Context context) {
		return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
	}

	/**
	 * App version name from `BuildConfig`.
	 */
	public String appVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		}
		catch (Exception e) {
			DkLogcats.error(DkLogcats.class, e);
			return "1.0.0";
		}
	}

	/**
	 * App version code from `BuildConfig`.
	 */
	public int appVersionCode(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		}
		catch (Exception e) {
			DkLogcats.error(DkLogcats.class, e);
			return 0;
		}
	}

	public static int colorPrimaryDark(Context context) {
		return attrColor(context, R.attr.colorPrimaryDark);
	}

	public static int colorPrimary(Context context) {
		return attrColor(context, R.attr.colorPrimary);
	}

	public static int colorAccent(Context context) {
		return attrColor(context, R.attr.colorAccent);
	}

	private static int attrColor(Context context, int colorAccentAttrId) {
		TypedValue typedValue = new TypedValue();
		int[] attrs = new int[] {colorAccentAttrId};
		TypedArray arr = context.obtainStyledAttributes(typedValue.data, attrs);

		int color = arr.getColor(0, 0);
		arr.recycle();

		return color;
	}
}
