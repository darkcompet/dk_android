/*
 * Copyright (c) 2019 DarkCompet. All rights reserved.
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
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.util.TypedValue;

import androidx.fragment.app.FragmentActivity;

import java.util.Locale;

import tool.compet.core.constant.DkConst;
import tool.compet.core.util.DkLogs;

/**
 * Information for current app.
 */
public class AppConfig {
	// You should manual initialize below fields via #AppConfig.obtainAttrs()
	public int colorLayout;
	public int colorPrimary;
	public int colorPrimaryDark;
	public int colorAccent;

	// Below fields also be automatically initialized when activity created
	// so don't use them until the first activity was created and #attachBaseContext() be called.
	public String lang = DkConst.LANG_ENGLISH;
	public String country = DkConst.COUNTRY_ENGLISH;
	public Locale locale = Locale.US;

	AppConfig() {
		lang = Locale.getDefault().getLanguage();
		country = Locale.getDefault().getCountry();
	}

	public void onConfigurationChanged(Activity host) {
	}

	/**
	 * You can get version name from BuildConfig also.
	 */
	public String getVersionName(Context context) {
		try {
			return getPackageInfo(context).versionName;
		}
		catch (Exception e) {
			DkLogs.logex(DkLogs.class, e);
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
			DkLogs.logex(DkLogs.class, e);
			return 0;
		}
	}

	public PackageInfo getPackageInfo(Context context) throws Exception {
		PackageManager manager = context.getPackageManager();
		return manager.getPackageInfo(context.getPackageName(), 0);
	}

	/**
	 * @param attrs should be array of
	 *       {
	 *          R.attr.dk_color_layout_bkg,
	 *          R.attr.colorPrimary,
	 *          R.attr.colorPrimaryDark,
	 *          R.attr.colorAccent
	 *       }
	 */
	@SuppressLint("ResourceType")
	public void obtainAttrs(FragmentActivity host, int[] attrs) {
		TypedValue tv = new TypedValue();
		TypedArray arr = host.obtainStyledAttributes(tv.data, attrs);

		this.colorLayout = arr.getColor(0, 0);
		this.colorPrimary = arr.getColor(1, 0);
		this.colorPrimaryDark = arr.getColor(2, 0);
		this.colorAccent = arr.getColor(3, 0);

		arr.recycle();
	}
}
