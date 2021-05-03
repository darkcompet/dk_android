/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

import tool.compet.core.DkConst;
import tool.compet.core.DkConfig;

public class DkLocaleHelper {
	/**
	 * Change locale of given context.
	 *
	 * @param context Current context
	 * @param lang Next language to change.
	 * @return Localed context
	 */
	public static ContextWrapper wrapLocale(Context context, String lang) {
		final Locale systemLocale = DkConfig.appLocale(context);

		if (! DkConst.EMPTY_STRING.equals(lang) && ! systemLocale.getLanguage().equals(lang)) {
			Configuration config = context.getResources().getConfiguration();
			Locale locale = new Locale(lang);
			Locale.setDefault(locale);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				config.setLocale(locale);
			}
			else {
				config.locale = locale;
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				context = context.createConfigurationContext(config);
			}
			else {
				context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
			}
		}

		return new ContextWrapper(context);
	}
}
