/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tool.compet.core.log.DkLogs;

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
            DkLogs.error(this, e);
        }
        return null;
    }

    public String obj2json(Object obj) {
        return GSON.toJson(obj);
    }
}
