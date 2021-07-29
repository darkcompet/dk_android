/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.http;

import android.graphics.Bitmap;

import java.net.HttpURLConnection;

import tool.compet.core.DkLogcats;
import tool.compet.core.graphics.DkBitmaps;

public class TheHttpResponseBody extends tool.compet.http4j.TheHttpResponseBody {
	protected TheHttpResponseBody(HttpURLConnection connection) {
		super(connection);
	}

	public Bitmap bitmap() {
		try {
			return DkBitmaps.load(connection.getInputStream());
		}
		catch (Exception e) {
			DkLogcats.error(DkLogcats.class, e);
			return null;
		}
		finally {
			connection.disconnect();
		}
	}
}