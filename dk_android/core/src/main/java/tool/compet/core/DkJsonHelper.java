/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DkJsonHelper {
	private static DkJsonHelper INS;
	private final Gson gson;

	private DkJsonHelper() {
		gson = new GsonBuilder()
			.setDateFormat(DkDateTimeConst.DATETIME)
			.excludeFieldsWithoutExposeAnnotation()
			.setPrettyPrinting()
			.create();
	}

	public static DkJsonHelper getIns() {
		return INS != null ? INS : (INS = new DkJsonHelper());
	}

	public <T> T json2obj(String json, Class<T> classOfT) {
		try {
			return gson.fromJson(json, classOfT);
		}
		catch (Exception e) {
			DkLogs.error(this, e);
		}
		return null;
	}

	public String obj2json(Object obj) {
		return gson.toJson(obj);
	}
}
