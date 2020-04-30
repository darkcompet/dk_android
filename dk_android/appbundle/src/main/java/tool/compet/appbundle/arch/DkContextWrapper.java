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

package tool.compet.appbundle.arch;

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

	public static DkContextWrapper wrap(Context context, String lang) {
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
