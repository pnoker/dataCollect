package com.dact.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mn:ss");
		Date date = new Date();
		return sdf.format(date);
	}
}
