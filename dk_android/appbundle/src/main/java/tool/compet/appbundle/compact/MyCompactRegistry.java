/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.compact;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import tool.compet.core.DkReflections;
import tool.compet.core.DkStrings;
import tool.compet.core.reflection.DkReflectionFinder;

@SuppressWarnings("unchecked")
class MyCompactRegistry {
	/**
	 * Start from all compact-annotated fields inside the View. Collect all compact-annotated fields
	 * which be specified in each type of field. After at all, init them and inject to correspond field.
	 * <p></p>
	 * Note that, this method must be called after #super.onCreate() inside subclass of View.
	 */
	static <L extends DkCompactLogic, D> void init(DkCompactView view, FragmentActivity host, @Nullable Bundle savedInstanceState) {
		Class viewClass = view.getClass();
		List<Field> viewLogics = DkReflectionFinder.getIns().findFields(viewClass, MyInjectLogic.class);
		List<Field> viewDatas = DkReflectionFinder.getIns().findFields(viewClass, MyInjectData.class);

		if (viewLogics.size() == 0 && viewDatas.size() == 0) {
			return; // Ignore since no field to process
		}
		if (viewLogics.size() != 1 || viewDatas.size() != 1) {
			throw new RuntimeException("Must declare only 1 ViewLogic and 1 ViewData inside View");
		}

		final Field viewLogicField = viewLogics.get(0);
		final Field viewDataField = viewDatas.get(0);

		Type[] viewGenericTypes = DkReflections.getAllGenericOfSuperClass(viewClass);

		// Only handle when have 2 generic arguments given (logic and data)
		if (viewGenericTypes != null && viewGenericTypes.length >= 2) {
			// Obtain logic and data
			Class<L> logicClass = (Class<L>) viewGenericTypes[0];
			Class<D> dataClass = (Class<D>) viewGenericTypes[1];

			if (! DkCompactLogic.class.isAssignableFrom(logicClass)) {
				throw new RuntimeException(DkStrings.format("ViewLogic `%s` must be subclass of `DkCompactViewLogic`", logicClass.toString()));
			}

			// Logic is ViewModelOwner, so it get aware of its destroy completely
			final L logic = new ViewModelProvider(view).get(logicClass.getName(), logicClass);
			final boolean isInit = (logic.model == null);
			if (isInit) {
				logic.model = instantiate(dataClass);
			}

			// Set Logic and Data fields inside View
			setFieldValue(viewLogicField, view, logic);
			setFieldValue(viewDataField, view, logic.model);

			// Attach view as soon as possible
			logic.view = view;

			// Tell Logic init state
			if (isInit) {
				logic.onInit(host, savedInstanceState);
			}
		}
	}

	private static <T> T instantiate(Class<T> type) {
		try {
			return type.newInstance();
		}
		catch (Exception e) {
			throw new RuntimeException(DkStrings.format("Could not instantiate for class %s, error: %s", type.getName(), e.getMessage()));
		}
	}

	private static void setFieldValue(Field field, Object target, Object value) {
		try {
			field.setAccessible(true);
			field.set(target, value);
		}
		catch (Exception e) {
			throw new RuntimeException(DkStrings.format("Could not set value for field %s inside class %s, error: %s", field.getName(), target.getClass().getName(), e.getMessage()));
		}
	}
}
