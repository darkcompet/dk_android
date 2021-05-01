/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import android.Manifest;
import android.content.pm.PackageManager;

import java.io.File;

public interface DkConst {
	// Separator
	String LS = System.getProperty("line.separator");
	String FS = File.separator;

	String EMPTY_STRING = "";
	char SPACE_CHAR = ' ';

	// Request code
	String REQ_CODE = "requestCode";

	// Permission
	int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;
	String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
	String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
	String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

	// intent
	String INTENT_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

	// in-app
	String SKU_TEST_PURCHASED = "android.test.purchased";
	String SKU_TEST_CANCELLED = "android.test.cancelled";
	String SKU_TEST_REFUNDED = "android.test.refunded";
	String SKU_TEST_ITEM_UNAVAILABLE = "android.test.item_unavailable";

	// language/country code
	String LANG_VIETNAM = "vi";
	String COUNTRY_VIETNAM = "VN";
	String LANG_ENGLISH = "en";
	String COUNTRY_ENGLISH = "US";
	String LANG_JAPAN = "ja";
	String COUNTRY_JAPAN = "JP";

	// common app package name
	String PKG_FACEBOOK = "com.facebook.katana";
	String PKG_TWITTER = "com.twitter.android";
	String PKG_INSTAGRAM = "com.instagram.android";
	String PKG_PINTEREST = "com.pinterest";
}
