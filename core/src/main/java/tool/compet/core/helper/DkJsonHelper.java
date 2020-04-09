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

package tool.compet.core.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tool.compet.core.util.DkLogs;

public class DkJsonHelper {
	private static DkJsonHelper INS;
	private final Gson GSON;

	private DkJsonHelper() {
		GSON = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss")
			.excludeFieldsWithoutExposeAnnotation()
			.setPrettyPrinting()
			.create();
	}

	public static synchronized DkJsonHelper getIns() {
		if (INS == null) {
			synchronized (DkJsonHelper.class) {
				if (INS == null) {
					INS = new DkJsonHelper();
				}
			}
		}
		return INS;
	}

	public <T> T json2obj(String json, Class<T> classOfT) {
		try {
			return GSON.fromJson(json, classOfT);
		}
		catch (Exception e) {
			DkLogs.logex(this, e);
		}
		return null;
	}

	public String obj2json(Object obj) {
		return GSON.toJson(obj);
	}
}
