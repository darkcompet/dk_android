/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.database;

import java.util.ArrayList;
import java.util.List;

public class TheSqliteColumnBuilder {
	private final List<TheSqliteColumnDetail> colDetails = new ArrayList<>();

	public TheSqliteColumnDetail id(String colName) {
		return id(colName, false);
	}

	public TheSqliteColumnDetail id(String colName, boolean autoincrement) {
		String type = "integer primary key";
		if (autoincrement) {
			type += " autoincrement";
		}
		return addColumn(colName, type);
	}

	public TheSqliteColumnDetail integer(String colName) {
		return addColumn(colName, "integer");
	}

	public TheSqliteColumnDetail string(String colName) {
		return addColumn(colName, "text");
	}

	public TheSqliteColumnDetail real(String colName) {
		return addColumn(colName, "real");
	}

	public TheSqliteColumnDetail blob(String colName) {
		return addColumn(colName, "blob");
	}

	private TheSqliteColumnDetail addColumn(String colName, String colType) {
		TheSqliteColumnDetail colDetail = new TheSqliteColumnDetail(colName, colType);
		colDetails.add(colDetail);
		return colDetail;
	}

	public List<String> compile() {
		List<String> colDefs = new ArrayList<>();
		for (TheSqliteColumnDetail detail : colDetails) {
			String colDef = detail.build();

			if (colDef != null && colDef.length() > 0) {
				colDefs.add(colDef);
			}
		}
		return colDefs;
	}
}
