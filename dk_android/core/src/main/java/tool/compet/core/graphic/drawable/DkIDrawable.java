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

package tool.compet.core.graphic.drawable;

import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * All custom drawables (starts with Dk) should implement this interface.
 */
public interface DkIDrawable {
	// from api 21+
	void setHotspot(float x, float y);

	// from api 21+
	void setHotspotBounds(int left, int top, int right, int bottom);

	// from api 21+
	void getHotspotBounds(@NonNull Rect outRect);

	// from api 21+
	void applyTheme(Resources.Theme t);

	// from api 21+
	void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme)
		throws XmlPullParserException, IOException;

	// from api 21+
	boolean canApplyTheme();
}
