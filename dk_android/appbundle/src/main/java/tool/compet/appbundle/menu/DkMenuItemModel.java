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

import tool.compet.core.util.DkStrings;

/**
 * 本クラス、DkMenuViewのアイテムのモデルです。デフォルトのアイテムが、設定用SharedPreference
 * と同期しますので、デフォルトのモデルは勿論、設定の同期をサポートしています。
 * 注意：settingPrefKeyのキーと対応した設定値がsettingPrefTagValueと同じであれば、
 * This model also contains a prefenrence key to help you map it with a setting preference entry.
 * You should provide a setting-preference-target-value to it knows value-matched or not.
 */
public class DkMenuItemModel {
	// id of tag inside menu xml
	private int id;

	// backward, forward menu
	private int parentMenuRes;
	private int childMenuRes;

	// default menu item attributes
	private int iconTitleRes;
	private String title;
	private String subTitle;
	private int iconStatusRes;

	// sync with default preference (setting preference)
	private int settingPrefKey;
	private String settingPrefTagValue;

	public int getParentMenuRes() {
		return parentMenuRes;
	}

	public boolean hasParentMenu() {
		return parentMenuRes > 0;
	}

	public void setParentMenuRes(int parentMenuRes) {
		this.parentMenuRes = parentMenuRes;
	}

	public boolean hasChildMenu() {
		return childMenuRes > 0;
	}

	public int getChildMenuRes() {
		return childMenuRes;
	}

	public void setChildMenuRes(int childMenuRes) {
		this.childMenuRes = childMenuRes;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean hasIconTitleRes() {
		return iconTitleRes > 0;
	}

	public int getIconTitleRes() {
		return iconTitleRes;
	}

	public void setIconTitleRes(int iconTitleRes) {
		this.iconTitleRes = iconTitleRes;
	}

	public boolean hasTitle() {
		return title != null;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean hasIconStatusRes() {
		return iconStatusRes > 0;
	}

	public int getIconStatusRes() {
		return iconStatusRes;
	}

	public void setIconStatusRes(int iconStatusRes) {
		this.iconStatusRes = iconStatusRes;
	}

	public boolean isSettingValueMatched() {
		return DkStrings.isEquals(settingPrefTagValue, "");
	}

	public boolean hasSettingPreference() {
		return settingPrefKey > 0;
	}

	public int getSettingPrefKey() {
		return settingPrefKey;
	}

	public void setSettingPrefKey(int settingPrefKey) {
		this.settingPrefKey = settingPrefKey;
	}

	public String getSettingPrefTagValue() {
		return settingPrefTagValue;
	}

	public void setSettingPrefTagValue(String settingPrefTagValue) {
		this.settingPrefTagValue = settingPrefTagValue;
	}
}
