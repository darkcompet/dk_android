/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.compassview;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DkInfo {
    @Expose
    @SerializedName("key")
    public String key; // for eg,. degrees

    @Expose
    @SerializedName("value")
    public String value; // for eg,. 180

    @Expose
    @SerializedName("list")
    public List<DkInfo> children = new ArrayList<>();

    public DkInfo() {
    }

    public DkInfo(String key) {
        this.key = key;
    }

    public DkInfo(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public DkInfo addChild(DkInfo child) {
        if (child != null) {
            children.add(child);
        }
        return this;
    }
}
