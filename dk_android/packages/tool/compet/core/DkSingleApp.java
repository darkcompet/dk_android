/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;

import java.util.Locale;

import tool.compet.core.topic.DkTopicOwner;

// Single application (lite version compares with multidex app).
public class DkSingleApp extends Application implements DkApp {
	protected static Context appContext;
	protected ViewModelStore viewModelStore;

	@Override
	public void onCreate() {
		super.onCreate();
		appContext = getApplicationContext();

//		PreferenceManager.setDefaultValues(this, R.xml.pref_settings, true);
//		PreferenceManager.setDefaultValues(this, R.xml.pref_home, true);

//		if (LeakCanary.isInAnalyzerProcess(this)) {
//		This process is dedicated to LeakCanary for heap analysis.
//		You should not init your app in this process.
//			return;
//		}
//		LeakCanary.install(this);

//		Thread.setDefaultUncaughtExceptionHandler((thread, e) -> {
//			DKLogs.logex(this, e);
//			DKFileLogger.get().log("uncaught exception: %s", e.getMessage());
//			DKAppContract.quit(-1);
//		});
//
//		Picasso.with().load().into();
	}

	// This makes the app become view model store owner
	@NonNull
	@Override
	public ViewModelStore getViewModelStore() {
		if (viewModelStore == null) {
			viewModelStore = new ViewModelStore();
		}
		return viewModelStore;
	}

	/**
	 * Obtain the topic controller and Make this view becomes an owner of the topic.
	 * When all owners of the topic were destroyed, topic and its material will be cleared.
	 */
	public DkTopicOwner joinTopic(String topicId) {
		return new DkTopicOwner(topicId, this).registerClient(this);
	}

	/**
	 * Just obtain the topic controller.
	 */
	public DkTopicOwner viewTopic(String topicId) {
		return new DkTopicOwner(topicId, this);
	}

	/**
	 * Should NOT use this app context to inflate a view since it maybe not support attributes for View.
	 */
	public static Context context() {
		return appContext;
	}

	/**
	 * App locale based on current context.
	 * If you have changed (wrapped) localed-context, then new locale was made.
	 */
	public static Locale locale() {
		return DkConfig.appLocale(context());
	}

	/**
	 * App language based on current context.
	 * If you have changed (wrapped) localed-context, then new language was made.
	 */
	public static String lang() {
		return locale().getLanguage();
	}

	/**
	 * Quit current app with a status.
	 */
	public static void quit(int status) {
		System.exit(status);
	}
}
