/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.Field;
import java.util.List;

import tool.compet.core.DkLogs;
import tool.compet.core.DkStrings;
import tool.compet.core.DkUtils;
import tool.compet.core.reflection.DkReflectionFinder;
import tool.compet.core.reflection.DkReflections;

@SuppressWarnings("unchecked")
class MyCompactInjector {
	private final DkCompactView view;
	private final Class viewClass;

	MyCompactInjector(DkCompactView view) {
		this.view = view;
		this.viewClass = view.getClass();
	}

	/**
	 * Start from all compact-annotated fields inside the View. Collect all compact-annotated fields
	 * which be specified in each type of field. After at all, init them and inject to correspond field.
	 * <p></p>
	 * Note that, this method must be called after #super.onCreate() inside subclass of View.
	 */
	<VL extends DkCompactViewLogic> VL injectViewLogic() {
		MyCompactCache cache = new ViewModelProvider(view).get(MyCompactCache.class.getName(), MyCompactCache.class);

		List<Field> viewLogics = DkReflectionFinder.getIns().findFields(viewClass, MyInjectViewLogic.class);
		if (viewLogics.size() == 0) {
			return null;
		}
		if (viewLogics.size() > 1) {
			DkUtils.complainAt(this, "Can declare at most 1 ViewLogic inside View");
		}
		Field viewLogicField = viewLogics.get(0);

		// Lookup cache from store first
		if (cache.viewLogic != null) {
			setFieldValue(viewLogicField, view, cache.viewLogic);
			return (VL) cache.viewLogic;
		}

		// Not found cache in the store, init ViewLogic
		VL viewLogic = null;
		Class<VL> viewLogicClass = DkReflections.getGenericOfSuperClass(viewClass);
		if (viewLogicClass != null) {
			viewLogic = initAndSetViewLogic(viewLogicClass, view, viewLogicField);
			cache.viewLogic = viewLogic;
		}

		return viewLogic;
	}

	private <VL extends DkCompactViewLogic> VL initAndSetViewLogic(@NonNull Class<VL> viewLogicClass, DkCompactView view, Field viewLogicField) {
		if (! DkCompactViewLogic.class.isAssignableFrom(viewLogicClass)) {
			throw new RuntimeException("Invalid type of ViewLogic: " + viewLogicClass.toString());
		}

		// init ViewLogic
		VL viewLogic = instantiate(viewLogicClass);

		// attach View to ViewLogic
		viewLogic.attachView(view);

		// init ViewLogic field inside View
		setFieldValue(viewLogicField, view, viewLogic);

		return viewLogic;
	}

	private <T> T instantiate(Class<T> type) {
		try {
			return type.newInstance();
		}
		catch (Exception e) {
			DkLogs.error(this, e);
			throw new RuntimeException(DkStrings.format("Could not instantiate for class %s", type.getName()));
		}
	}

	private void setFieldValue(Field field, Object target, Object value) {
		try {
			field.setAccessible(true);
			field.set(target, value);
		}
		catch (IllegalAccessException e) {
			DkLogs.error(this, e);
			DkLogs.complain(this, "Could not set value for field %s inside class %s.", field.getName(), target.getClass().getName());
		}
	}
}
