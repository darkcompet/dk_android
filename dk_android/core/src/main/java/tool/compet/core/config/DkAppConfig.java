/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.config;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import tool.compet.core.DkLogs;

/**
 * Config of the app.
 */
public class DkAppConfig {
	/**
	 * You can get version name from BuildConfig also.
	 */
	public String getVersionName(Context context) {
		try {
			return getPackageInfo(context).versionName;
		}
		catch (Exception e) {
			DkLogs.error(DkLogs.class, e);
			return "1.0.0";
		}
	}

	/**
	 * You can get version code from BuildConfig also.
	 */
	public int getVersionCode(Context context) {
		try {
			return getPackageInfo(context).versionCode;
		}
		catch (Exception e) {
			DkLogs.error(DkLogs.class, e);
			return 0;
		}
	}

	private PackageInfo getPackageInfo(Context context) throws Exception {
		PackageManager manager = context.getPackageManager();
		return manager.getPackageInfo(context.getPackageName(), 0);
	}
}
