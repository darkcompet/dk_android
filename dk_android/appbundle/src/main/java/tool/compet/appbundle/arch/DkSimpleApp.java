package tool.compet.appbundle.arch;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;

import tool.compet.core.helper.DkExecutorService;
import tool.compet.core.reflection.DkReflectionFinder;
import tool.compet.core.storage.DkPreferenceStorage;

public class DkSimpleApp extends Application implements DkApp {
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
