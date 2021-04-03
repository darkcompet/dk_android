/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.appbundle.architecture;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

import tool.compet.core.config.DkConfig;

public class DkContextWrapper extends ContextWrapper {
	private DkContextWrapper(Context base) {
		super(base);
	}

	/**
	 * Wraps (embed) locale into given context. By do it, caller can change language of the app.
	 *
	 * @param context Current context
	 * @param lang    Next lnaguage
	 * @return Localed context
	 */
	public static DkContextWrapper wrapLocale(Context context, String lang) {
		Configuration config = context.getResources().getConfiguration();

		Locale sysLocale;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			sysLocale = config.getLocales().get(0);
		}
		else {
			sysLocale = config.locale;
		}

		DkConfig.device.locale = sysLocale;

		if (!("").equals(lang) && !sysLocale.getLanguage().equals(lang)) {
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
		return new DkContextWrapper(context);
	}
}
