/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.core;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Process;
import android.provider.MediaStore.Images.Media;
import android.provider.Settings;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.view.WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

/**
 * Utility class, provides common basic operations for app.
 */
public class DkUtils extends tool.compet.core4j.DkUtils {
	public static int getResourceId(String resName, Class<?> clazz) {
		try {
			return (int) clazz.getDeclaredField(resName).get(null);
		}
		catch (Exception e) {
			DkLogcats.error(DkLogcats.class, e);
			return -1;
		}
	}

	public static List<String> asset2lines(Context context, String fileName, boolean trim) {
		List<String> lines = new ArrayList<>();

		try {
			String line;
			BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
			while ((line = br.readLine()) != null) {
				lines.add(trim ? line.trim() : line);
			}
			br.close();
		}
		catch (Exception e) {
			DkLogcats.error(DkLogcats.class, e);
		}

		return lines;
	}

	public static String asset2string(Context context, String fileName) {
		try {
			return stream2string(context.getAssets().open(fileName));
		}
		catch (IOException e) {
			DkLogcats.error(DkLogcats.class, e);
			return "";
		}
	}

	public static String stream2string(InputStream is) {
		String line;
		String ls = DkConst.LS;
		StringBuilder sb = new StringBuilder(256);

		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			while ((line = br.readLine()) != null) {
				sb.append(line).append(ls);
			}
		}
		catch (Exception e) {
			DkLogcats.error(DkLogcats.class, e);
		}

		return sb.toString();
	}

	public static void restartApp(Context context, Class startActivity) {
		Intent startIntent = new Intent(context, startActivity);
		PendingIntent pendingIntent = PendingIntent.getActivity(context,
			Process.myPid(),
			startIntent,
			PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		if (alarm != null) {
			alarm.set(AlarmManager.RTC, System.currentTimeMillis() + 200, pendingIntent);
		}

		Runtime.getRuntime().exit(0);
	}

	public static void setFullScreen(Activity host, boolean fullScreen) {
		// alter at onAttach(): host.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

		int addFlag = fullScreen ? FLAG_FULLSCREEN : FLAG_FORCE_NOT_FULLSCREEN;
		int clearFlag = FLAG_FULLSCREEN + FLAG_FORCE_NOT_FULLSCREEN - addFlag;

		Window window = host.getWindow();
		if (window != null) {
			window.addFlags(addFlag);
			window.clearFlags(clearFlag);
		}
	}

	public static void hideStatusBar(Activity host) {
		// alter at onDetach(): host.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

		if (Build.VERSION.SDK_INT < 16) {
			host.getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);
		}
		else {
			View decorView = host.getWindow().getDecorView();
			// Hide the status bar.
			int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
			decorView.setSystemUiVisibility(uiOptions);
			// Remember that you should never show the action bar if the
			// status bar is hidden, so hide that too if necessary.
			ActionBar actionBar = host.getActionBar();

			if (actionBar != null) {
				actionBar.hide();
			}
		}
	}

	public static void transparentStatusBar(Activity host, boolean isTransparent, boolean fullscreen) {
		int defaultStatusBarColor = Color.TRANSPARENT;
		final Window window = host.getWindow();

		if (isTransparent) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { // 16+
				window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 21+
				defaultStatusBarColor = window.getStatusBarColor();
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				// FOR TRANSPARENT NAVIGATION BAR
				// window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
				window.setStatusBarColor(Color.TRANSPARENT);
			}
			else {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				}
			}
		}
		else {
			if (fullscreen) {
				View decorView = window.getDecorView();
				int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
				decorView.setSystemUiVisibility(uiOptions);
			}
			else {
				window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
					window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
					window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
					window.clearFlags(FLAG_FULLSCREEN);
					window.setStatusBarColor(defaultStatusBarColor);

				}
				else {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
						window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
					}
				}
			}
		}
	}

	/**
	 * Dim system bars like: status bar, navigation bar...
	 * Note that, once user touches some system bar, you need call this to dim again.
	 */
	public static void dimSystemBars(Activity host) {
		View decorView = host.getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;

		decorView.setSystemUiVisibility(uiOptions);
	}

	public static void showSystemBars(Activity host) {
		View decorView = host.getWindow().getDecorView();
		// Clears all flags
		decorView.setSystemUiVisibility(View.VISIBLE);
	}

	public static void sendEmail(Context context, String dstEmail, String subject, String message) {
		Uri uri = Uri.fromParts("mailto", dstEmail, null);
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, message);
		context.startActivity(Intent.createChooser(emailIntent, "Send email"));
	}

	public static int getPhotoOrientation(String photoPath) throws IOException {
		ExifInterface ei = new ExifInterface(photoPath);
		int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

		switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				return 90;
			case ExifInterface.ORIENTATION_ROTATE_180:
				return 180;
			case ExifInterface.ORIENTATION_ROTATE_270:
				return 270;
			case ExifInterface.ORIENTATION_NORMAL:
				return 0;
		}
		return 0;
	}

	public static int getPhotoOrientation(Context context, Uri photoUri) {
		int rotate = 0;
		try {
			context.getContentResolver().notifyChange(photoUri, null);
			File imageFile = new File(getPathFromUri(context, photoUri));
			ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
			switch (orientation) {
				case ExifInterface.ORIENTATION_NORMAL:
					rotate = 0;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotate = 270;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotate = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotate = 90;
					break;
			}
		}
		catch (Exception e) {
			DkLogcats.error(DkLogcats.class, e);
		}

		return rotate;
	}

	public static String getGalleryPhotoPath(Context context, @Nullable Intent data) {
		if (data == null) {
			return null;
		}
		return getPathFromUri(context, data.getData());
	}

	public static String getPathFromUri(Context context, @Nullable Uri uri) {
		if (uri == null) {
			return null;
		}

		String res = null;
		String[] proj = {Media.DATA};
		Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);

		if (cursor != null && cursor.moveToFirst()) {
			int column_index = cursor.getColumnIndexOrThrow(Media.DATA);
			res = cursor.getString(column_index);
			cursor.close();
		}

		return res;
	}

	public static void hideSoftKeyboard(Context context, @Nullable View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

		if (imm != null && view != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED);
	}

	public static void gotoGpsSetting(Context context) {
		context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}

	/**
	 * @return intent for app in local. If null, intent of app on play store will be returned.
	 */
	public static Intent getOpenIntentForAppInLocal(Context context, String packageName) {
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		if (intent == null) {
			intent = intentForOpenAppInPlayStore(packageName);
		}
		intent.setFlags(0);

		return intent;
	}

	public static Intent intentForOpenAppInPlayStore(String packageName) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=" + packageName));
		intent.setFlags(0);

		return intent;
	}

	public static Intent intentForRateApp(Context context) {
		Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;

		if (Build.VERSION.SDK_INT >= 21) {
			flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
		}
		else {
			flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
		}

		try {
			intent.addFlags(flags);

			return intent;
		}
		catch (Exception e) {
			DkLogcats.error(DkLogcats.class, e);
			return new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google" + ".com/store/apps/details?id=" + context.getPackageName()));
		}
	}

	public static Intent intentForOpenPlayStoreAppList(String developerName) {
		Uri uri = Uri.parse("market://search?q=pub:" + developerName);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
		}
		else {
			flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
		}

		try {
			return intent.addFlags(flags);
		}
		catch (Exception e) {
			DkLogcats.error(DkLogcats.class, e);
			return new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=pub:" + developerName));
		}
	}

	public static int getScreenRotation(Activity host) {
		int rotation = host.getWindowManager().getDefaultDisplay().getRotation();
		if (host.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			switch (rotation) {
				case Surface.ROTATION_0:
				case Surface.ROTATION_90:
					return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
				default:
					return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
			}
		}
		else {
			switch (rotation) {
				case Surface.ROTATION_0:
				case Surface.ROTATION_270:
					return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
				default:
					return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
			}
		}
		//		return getWindowManager().getDefaultDisplay().getRotation();
	}

	public static void setScreenOritation(Activity host, int hostInfoOrientation) {
		host.setRequestedOrientation(hostInfoOrientation);
	}

	/**
	 * Also add this line to Manifest.xml: android:configChanges="keyboardHidden|orientation|screenSize"
	 */
	public static void lockOrientation(Activity host) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			host.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
		}
	}

	public static boolean shareOn(Context context, String targetAppPkg, String title, String message) {
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(targetAppPkg);
		if (intent == null) {
			return false;
		}
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setPackage(targetAppPkg);

		shareIntent.putExtra(Intent.EXTRA_TITLE, title);
		shareIntent.putExtra(Intent.EXTRA_TEXT, message);

		context.startActivity(shareIntent);

		return true;
	}

	/**
	 * Caller must grant permission `DkConst.WRITE_EXTERNAL_STORAGE`.
	 */
	public static boolean share(Activity host, String message, Iterable<Bitmap> bitmaps) {
		if (! DkUtils.checkPermission(host, DkConst.WRITE_EXTERNAL_STORAGE)) {
			return false;
		}
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, message);

		ArrayList<Uri> uris = new ArrayList<>();

		for (Bitmap bitmap : bitmaps) {
			if (bitmap != null) {
				String bitmapPath = Media.insertImage(host.getContentResolver(), bitmap, "share", null);
				Uri uri = Uri.parse(bitmapPath);
				uris.add(uri);
			}
		}
		if (uris.size() > 0) {
			intent.setAction(Intent.ACTION_SEND_MULTIPLE);
			intent.setType("image/*");
			intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		}

		host.startActivity(Intent.createChooser(intent, "share"));

		return true;
	}

	public static Intent intentForUninstallPackage(Activity host, String packageName) {
		Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
		intent.setData(Uri.parse("package:" + packageName));
		intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);

		return intent;
	}

	/**
	 * @return TRUE iff it is api 21+ and battery is in save-mode. Otherwise FALSE.
	 */
	public static boolean isPowerSaveMode(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			return powerManager.isPowerSaveMode();
		}
		return false;
	}

	public static boolean checkPermission(Context context, String... permissions) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
			for (String permission : permissions) {
				if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
					return false;
				}
			}
		}
		return true;
	}
}
