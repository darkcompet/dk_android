/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.stream;

import tool.compet.stream4j.DkScheduler;

@SuppressWarnings("unchecked")
public class DkSchedulers extends tool.compet.stream4j.DkSchedulers {
	protected static DkScheduler uiScheduler;

	// Android ui thread scheduler
	public static <T> DkScheduler<T> ui() {
		if (uiScheduler == null) {
			synchronized (DkSchedulers.class) {
				if (uiScheduler == null) {
					uiScheduler = new MyUiScheduler<>();
				}
			}
		}
		return (DkScheduler<T>) uiScheduler;
	}
}
