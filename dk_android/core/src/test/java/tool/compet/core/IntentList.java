/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import org.junit.Test;

public class IntentList {
	@Test
	public void foo() {
		Context context = null;

		// Goto GPS setting page
		context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}
}
