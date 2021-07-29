/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.compassview;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DkCompassInfo {
	@Expose
	@SerializedName("key")
	public String key; // for eg,. degrees

	@Expose
	@SerializedName("value")
	public String value; // for eg,. 180

	@Expose
	@SerializedName("list")
	public List<DkCompassInfo> children = new ArrayList<>();

	public DkCompassInfo() {
	}

	public DkCompassInfo(String key) {
		this.key = key;
	}

	public DkCompassInfo(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public DkCompassInfo addChild(DkCompassInfo child) {
		if (child != null) {
			children.add(child);
		}
		return this;
	}
}
