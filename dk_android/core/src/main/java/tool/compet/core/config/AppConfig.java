/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.config;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.util.TypedValue;

import androidx.fragment.app.FragmentActivity;

import java.util.Locale;

import tool.compet.core.constant.DkConst;
import tool.compet.core.log.DkLogs;

/**
 * Config of current app.
 */
public class AppConfig {
    // You should manual initialize below fields via #AppConfig.obtainAttrs()
    public int colorLayout;
    public int colorPrimary;
    public int colorPrimaryDark;
    public int colorAccent;

    // Below fields also be automatically initialized when activity created
    // so don't use them until the first activity was created and #attachBaseContext() be called.
    public String lang = DkConst.LANG_ENGLISH;
    public String country = DkConst.COUNTRY_ENGLISH;
    public Locale locale = Locale.US;

    AppConfig() {
        lang = Locale.getDefault().getLanguage();
        country = Locale.getDefault().getCountry();
    }

    public void onConfigurationChanged(Activity host) {
    }

    /**
     * You can get version name from BuildConfig also.
     */
    public String getVersionName(Context context) {
        try {
            return getPackageInfo(context).versionName;
        }
        catch (Exception e) {
            DkLogs.error(DkLogs.class, e);
            return "1.0.0";
        }
    }

    /**
     * You can get version code from BuildConfig also.
     */
    public int getVersionCode(Context context) {
        try {
            return getPackageInfo(context).versionCode;
        }
        catch (Exception e) {
            DkLogs.error(DkLogs.class, e);
            return 0;
        }
    }

    public PackageInfo getPackageInfo(Context context) throws Exception {
        PackageManager manager = context.getPackageManager();
        return manager.getPackageInfo(context.getPackageName(), 0);
    }

    /**
     * @param attrs should be array of
     *              {
     *              R.attr.dk_color_layout_bkg,
     *              R.attr.colorPrimary,
     *              R.attr.colorPrimaryDark,
     *              R.attr.colorAccent
     *              }
     */
    @SuppressLint("ResourceType")
    public void obtainAttrs(FragmentActivity host, int[] attrs) {
        TypedValue tv = new TypedValue();
        TypedArray arr = host.obtainStyledAttributes(tv.data, attrs);

        this.colorLayout = arr.getColor(0, 0);
        this.colorPrimary = arr.getColor(1, 0);
        this.colorPrimaryDark = arr.getColor(2, 0);
        this.colorAccent = arr.getColor(3, 0);

        arr.recycle();
    }
}
