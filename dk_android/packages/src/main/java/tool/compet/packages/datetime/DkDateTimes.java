/*
 * Copyright (c) 2017-2021 DarkCompet. All rights reserved.
 */

package tool.compet.packages.datetime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * This class, provides common basic operations for datetime.
 */
public class DkDateTimes {
	/**
	 * Returns the number of milliseconds (now) since January 1, 1970, 00:00:00 GMT.
	 */
	public static long nowInMillis() {
		return new Date().getTime();
	}

	/**
	 * Returns array of `year, month, day, hh, mm, ss` from calendar.
	 * For eg,. [2021, 03, 31, 14, 30, 59]
	 */
	public static int[] nowInArr() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hh = cal.get(Calendar.HOUR_OF_DAY);
		int mm = cal.get(Calendar.MINUTE);
		int ss = cal.get(Calendar.SECOND);

		return new int[] {year, month, day, hh, mm, ss};
	}

	/**
	 * Format current time (now) with default format `Y-m-d H:i:s`.
	 */
	public static String format() {
		return format(nowInMillis());
	}

	public static String format(String pattern) {
		return format(nowInMillis(), pattern, Locale.US);
	}

	public static String format(long millis) {
		return format(millis, "yyyy-MM-dd HH:mm:ss", Locale.US);
	}

	public static String format(long millis, String pattern) {
		return format(millis, pattern, Locale.US);
	}

	/**
	 * @param millis Time from Date.getTime() in millis.
	 * @param pattern Pattern of datetime, for eg,. `yyyy-MM-dd HH:mm:ss`
	 * @return formatted datetime with format `Y-m-d H:i:s` in US locale.
	 */
	public static String format(long millis, String pattern, Locale locale) {
		Date date = new Date(millis);
		DateFormat formatter = new SimpleDateFormat(pattern, locale);
		return formatter.format(date);
	}
}
