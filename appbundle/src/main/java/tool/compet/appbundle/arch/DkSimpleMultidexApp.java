/*
 * Copyright (c) 2019 DarkCompet. All rights reserved.
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

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;

import tool.compet.core.helper.DkExecutorService;
import tool.compet.core.reflection.DkReflectionFinder;
import tool.compet.core.storage.DkPreferenceStorage;

public class DkSimpleMultidexApp extends Application implements DkApp {
	protected static Context appContext;
	protected ViewModelStore viewModelStore;

	@Override
	public void onCreate() {
		super.onCreate();

		Context ctx = appContext = getApplicationContext();

		DkExecutorService.install();
		DkReflectionFinder.installWithCompetTool(ctx);
		DkPreferenceStorage.install(ctx);
	}

	/**
	 * Should not use app context to inflate a view since it maybe not support attributes for View.
	 */
	public static Context getContext() {
		return appContext;
	}

	@NonNull
	@Override
	public ViewModelStore getViewModelStore() {
		if (viewModelStore == null) {
			viewModelStore = new ViewModelStore();
		}
		return viewModelStore;
	}
}
