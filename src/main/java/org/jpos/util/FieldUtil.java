package org.jpos.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FieldUtil {
	public static String formatDate(Date d, String pattern, TimeZone timeZone) {
		SimpleDateFormat df = (SimpleDateFormat) DateFormat
				.getDateTimeInstance();

		df.setTimeZone(timeZone);
		df.applyPattern(pattern);
		return df.format(d);
	}

	public static String formatDate(Date d, String pattern) {
		return formatDate(d, pattern, TimeZone.getDefault());
	}

	public static String getBit7() {
		return formatDate(new Date(), "MMddHHmmss");
	}

	public static String getBit11() {
		Date d = new Date();
		String result = formatDate(d, "ssSS");
		return result;
	}

	public static String getBit12() {
		return formatDate(new Date(), "HHmmss");
	}

	public static String getBit13() {
		return formatDate(new Date(), "MMdd");
	}

	public static String getBit37() {
		return formatDate(new Date(), "yyMMddHHmmss");
	}

	public static String getDate() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	public static String getValue(int i) {
		switch (i) {
		case 7:
			return getBit7();
		case 11:
			return getBit11();
		case 12:
			return getBit12();
		case 13:
			return getBit13();
		case 37:
			return getBit37();
		}
		return "bit." + i + " empty";
	}

	public static class Interval {
		public Date startDate = new Date();

		public long getValue() {
			return new Date().getTime() - this.startDate.getTime();
		}

	}
}