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

package tool.compet.appbundle.menu;

import android.content.Context;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import tool.compet.core.math.DkMaths;
import tool.compet.core.util.DkLogs;

/**
 * 本クラス、メニューを解析して得られた結果をキャッシュします。
 */
public class DkMenuInflater<T extends DkMenuItemModel> {
	private static DkMenuInflater INSTANCE;

	private DkMenuResourceParser mParser;
	private SparseArray<List<T>> mCache;
	private Callable<T> mModelCreator;

	private DkMenuInflater() {
		mParser = new DkMenuResourceParser();
		mCache = new SparseArray<>(64);
	}

	public static DkMenuInflater getIns() {
		return INSTANCE != null ? INSTANCE : (INSTANCE = new DkMenuInflater());
	}

	@SuppressWarnings("unchecked")
	public Callable<T> getModelCreator() {
		if (mModelCreator == null) {
			mModelCreator = () -> (T) new DkMenuItemModel();
		}
		return mModelCreator;
	}

	public DkMenuInflater<T> setModelCreator(Callable<T> modelCreator) {
		mModelCreator = modelCreator;
		return this;
	}

	public List<T> inflate(Context context, int menuRes) {
		List<T> models = mCache.get(menuRes);
		if (models != null) {
			return models;
		}
		try {
			models = new ArrayList<>();
			T model;
			Callable<T> modelCreator = getModelCreator();

			for (String[] tag : mParser.parse(context, menuRes)) {
				model = modelCreator.call();

				for (int i = 0, w = 0, N = tag.length >> 1; i < N; ++i, w = i << 1) {
					String attrName = tag[w];
					String attrValue = tag[w + 1];
					boolean isResourceValue = false;

					if (attrValue.startsWith("@")) {
						isResourceValue = true;
						attrValue = attrValue.substring(1, attrValue.length());
					}

					if ("id".equals(attrName)) {
						if (isResourceValue) {
							model.setId(DkMaths.parseInt(attrValue));
						}
						else {
							DkLogs.complain(this, "id attribute value must be resource value");
						}
					}
					else if ("icon".equals(attrName)) {
						if (isResourceValue) {
							model.setIconTitleRes(DkMaths.parseInt(attrValue));
						}
						else {
							DkLogs.complain(this, "icon attribute value must be resource value");
						}
					}
					else if ("title".equals(attrName)) {
						if (isResourceValue) {
							model.setTitle(context.getString(DkMaths.parseInt(attrValue)));
						}
						else {
							model.setTitle(attrValue);
						}
					}
					else if ("dk_preference_key".equals(attrName)) {
						if (isResourceValue) {
							model.setSettingPrefKey(DkMaths.parseInt(attrValue));
						}
						else {
							DkLogs.complain(this, "dk_preference_key attribute value must be resource value");
						}
					}
					else if ("dk_preference_tag_value".equals(attrName)) {
						model.setSettingPrefTagValue(attrValue);
					}
					else if ("dk_icon_status".equals(attrName)) {
						if (isResourceValue) {
							model.setIconStatusRes(DkMaths.parseInt(attrValue));
						}
						else {
							DkLogs.complain(this, "dk_icon_status attribute value must be resource value");
						}
					}
				}

				models.add(model);
			}

			// cache result for this menu resource
			mCache.put(menuRes, models);
		}
		catch (Exception e) {
			DkLogs.logex(this, e);
		}

		return models;
	}
}
