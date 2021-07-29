/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;

import java.util.Locale;

import tool.compet.topic.DkTopicOwner;
import tool.compet.core4j.BuildConfig;
import tool.compet.core4j.DkExecutorService;
import tool.compet.reflection4j.DkReflectionFinder;

// Single application (lite version compare with multidex app).
public class DkSingleApp extends Application implements DkApp {
	protected static Context appContext;
	protected ViewModelStore viewModelStore;

	@Override
	public void onCreate() {
		// We sync debug/release flag for Java and App
		BuildConfig.DEBUG = tool.compet.BuildConfig.DEBUG;

		super.onCreate();
		appContext = this;

		// Also setup executor service with default config even though it force the app =))
		// Of course we can review and change it in future, for now is busy :)
		DkExecutorService.install();

		// Init reflection finder for `compet` and `app`, this also upsert prefix-search-packages
		DkReflectionFinder.install(
			tool.compet.BuildConfig.class.getPackage().getName(),
			this.getClass().getPackage().getName()
		);
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
		return DkConfig.appLocale(appContext);
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
