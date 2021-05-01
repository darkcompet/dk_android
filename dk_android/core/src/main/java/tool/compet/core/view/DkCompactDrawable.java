/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core.view;

import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Back-compability version, this brings new features on api 21+ into older version.
 */
public abstract class DkCompactDrawable extends Drawable implements DkDrawable {
	// hotspot is pressed-touch-coordinator from user onto the callback
	protected float hotspotX;
	protected float hotspotY;

	@Override
	public void setHotspot(float x, float y) {
		// don't really need but just give to super a chance to handle
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			super.setHotspot(x, y);
		}

		hotspotX = x;
		hotspotY = y;
	}

	@Override
	public void setHotspotBounds(int left, int top, int right, int bottom) {
		// don't really need but just give to super a chance to handle
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			super.setHotspotBounds(left, top, right, bottom);
		}
	}

	@Override
	public void getHotspotBounds(@NonNull Rect outRect) {
		super.getHotspotBounds(outRect);
	}

	@Override
	public boolean canApplyTheme() {
		return super.canApplyTheme();
	}

	@Override
	public void applyTheme(@NonNull Resources.Theme t) {
		super.applyTheme(t);
	}

	@Override
	public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs)
		throws XmlPullParserException, IOException {
		super.inflate(r, parser, attrs);
	}

	@Override
	public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs,
		@Nullable Resources.Theme theme) throws XmlPullParserException, IOException {
		super.inflate(r, parser, attrs, theme);
	}
}
