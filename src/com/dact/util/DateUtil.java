package com.dact.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	/**
	 * @return 2016-10-21 08:33:53
	 */
	public String getCompleteTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return sdf.format(date);
	}

	/**
	 * @return 2016-10-21 08:36
	 */
	public String getMinuteTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date();
		return sdf.format(date);
	}

	/**
	 * @return 2016-10-21
	 */
	public String getDayTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return sdf.format(date);
	}

	/**
	 * @return 1477010427811
	 */
	public long getTimeNum() {
		Date date = new Date();
		return date.getTime();
	}

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		System.out.println(sdf.format(date));
		System.out.println(date.getTime());
	}
}
