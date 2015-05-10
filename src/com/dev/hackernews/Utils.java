package com.dev.hackernews;

public class Utils {
	public static String findTimeDuration(long jsonTime) {
		long today = System.currentTimeMillis() / 1000L;
		jsonTime = today - jsonTime;
		String postedTime = "";
		if (jsonTime < 3600) {
			// for minutes
			long min = (long) Math.floor(jsonTime / 60);
			postedTime = min == 1 ? min + " minute ago " : min
					+ " minutes ago ";
		} else if (jsonTime < 86400) {
			// for hours
			long hour = (long) Math.floor(jsonTime / 3600);
			postedTime = hour == 1 ? hour + " hour ago " : hour + " hours ago ";
		} else {
			// for days
			long days = (long) Math.floor(jsonTime / 86400);
			postedTime = days == 1 ? days + " day ago " : days + " days ago ";
		}
		return postedTime;
	}
}
