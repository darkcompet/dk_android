/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class DkFormHelper {
	/**
	 * Build array adapter with default item list stype `android.R.layout.simple_list_item_1`.
	 */
	public static <T> ArrayAdapter<T> arrayAdapter(Context context, T[] data) {
		return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, data);
	}

	/**
	 * Build array adapter with default item list style `android.R.layout.simple_list_item_1`.
	 * @param arrayRes Resource id of array, for eg,. R.array.language_list
	 */
	public static <T> ArrayAdapter<CharSequence> arrayAdapter(Context context, int arrayRes) {
		return ArrayAdapter.createFromResource(context, arrayRes, android.R.layout.simple_list_item_1);
	}

	/**
	 * Build array adapter with an item list style.
	 * @param arrayRes Resource id of array, for eg,. R.array.language_list
	 * @param itemLayoutRes Resource id of list item, for eg,. android.R.layout.simple_spinner_item
	 */
	public static <T> ArrayAdapter<CharSequence> arrayAdapter(Context context, int arrayRes, int itemLayoutRes) {
		return ArrayAdapter.createFromResource(context, arrayRes, itemLayoutRes);
	}

	/**
	 * Build array adapter with an item list style.
	 * @param itemLayoutRes Resource id of list item, for eg,. android.R.layout.simple_spinner_item
	 */
	public static <T> ArrayAdapter<T> arrayAdapter(Context context, int itemLayoutRes, T[] data) {
		return new ArrayAdapter<>(context, itemLayoutRes, data);
	}

	/**
	 * Build array adapter with an item list style.
	 * @param itemLayoutRes Resource id of list item, for eg,. android.R.layout.simple_spinner_item
	 */
	public static <T> ArrayAdapter<T> arrayAdapter(Context context, int itemLayoutRes, List<T> data) {
		return new ArrayAdapter<>(context, itemLayoutRes, data);
	}
}
