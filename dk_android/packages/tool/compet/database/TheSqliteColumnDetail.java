/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

public class TheSqliteColumnDetail {
	private final String name;
	private final String type;
	private boolean nullable = true;
	private boolean hasDefaultValue;
	private String defaultValue;

	TheSqliteColumnDetail(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public TheSqliteColumnDetail nullable(boolean nullable) {
		this.nullable = nullable;
		return this;
	}

	public TheSqliteColumnDetail defaultValue(String value) {
		this.hasDefaultValue = true;
		this.defaultValue = value;
		return this;
	}

	public String build() {
		String def = MyGrammarHelper.wrapName(name) + " " + type;
		if (!nullable) {
			def += " not null";
		}
		if (hasDefaultValue) {
			def += " \"" + defaultValue + "\"";
		}
		return def.trim();
	}
}
