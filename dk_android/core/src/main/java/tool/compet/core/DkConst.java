/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;

public interface DkConst {
	/**
	 * This is sdk version of current device (compare with `Build.VERSION_CODES.*`).
	 */
	int SDK_VERSION = Build.VERSION.SDK_INT;

	/**
	 * Useful constant.
	 */
	String LS = System.getProperty("line.separator");
	String FS = File.separator;
	String EMPTY_STRING = "";
	char SPACE_CHAR = ' '; // 1 byte

	/**
	 * Permission.
	 */
	int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;
	String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
	String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
	String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

	/**
	 * Intent.
	 */
	String INTENT_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

	/**
	 * Purchase (in-app, subscribe).
	 */
	String SKU_TEST_PURCHASED = "android.test.purchased";
	String SKU_TEST_CANCELLED = "android.test.cancelled";
	String SKU_TEST_REFUNDED = "android.test.refunded";
	String SKU_TEST_ITEM_UNAVAILABLE = "android.test.item_unavailable";

	/**
	 * language/country code.
	 */
	String LANG_VIETNAM = "vi";
	String LANG_ENGLISH = "en";
	String LANG_JAPAN = "ja";
	String COUNTRY_VIETNAM = "VN";
	String COUNTRY_ENGLISH = "US";
	String COUNTRY_JAPAN = "JP";

	/**
	 * Some popular app package name.
	 */
	String PKG_FACEBOOK = "com.facebook.katana";
	String PKG_TWITTER = "com.twitter.android";
	String PKG_INSTAGRAM = "com.instagram.android";
	String PKG_PINTEREST = "com.pinterest";
}
