/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.core.datetime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * This class, provides common basic operations for datetime.
 */
public class DkDateTimes {
    public static long getCurrentTime() {
        return new Date().getTime();
    }

    public static int[] getCurrentTimeDetail() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hh = cal.get(Calendar.HOUR_OF_DAY);
        int mm = cal.get(Calendar.MINUTE);
        int ss = cal.get(Calendar.SECOND);

        return new int[] {year, month, day, hh, mm, ss};
    }

    public static String formatCurrentTime() {
        return formatTime(getCurrentTime(), DkDateTimeConst.DATE_FORMAT_US, Locale.US);
    }

    /**
     * @param millis time from Date.getTime() in millis.
     * @return formatted datetime with normal format in US locale.
     */
    public static String formatTime(long millis) {
        return formatTime(millis, DkDateTimeConst.DATE_FORMAT_US, Locale.US);
    }

    public static String formatTime(long millis, String pattern, Locale locale) {
        Date date = new Date(millis);
        DateFormat formatter = new SimpleDateFormat(pattern, locale);
        return formatter.format(date);
    }
}
