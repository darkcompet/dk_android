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

package tool.compet.core.constant;

import android.Manifest;
import android.content.pm.PackageManager;

import java.io.File;

public interface DkConst {
	// darkcompet developer
	String DEVELOPER_NAME = "DarkCompet";
	String DEVELOPER_EMAIL = "darkcompet@gmail.com";

	// Separator
	String LS = System.getProperty("line.separator");
	String FS = File.separator;

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
	String PKG_TWITTER =  "com.twitter.android";
	String PKG_INSTAGRAM = "com.instagram.android";
	String PKG_PINTEREST = "com.pinterest";
}
