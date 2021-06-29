/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.graphics;

import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Drawable compat.
 */
public interface DkDrawable {
	// from api 21+
	void setHotspot(float x, float y);

	// from api 21+
	void setHotspotBounds(int left, int top, int right, int bottom);

	// from api 21+
	void getHotspotBounds(@NonNull Rect outRect);

	// from api 21+
	void applyTheme(Resources.Theme t);

	// from api 21+
	void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException;

	// from api 21+
	boolean canApplyTheme();
}
