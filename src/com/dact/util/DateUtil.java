package com.dact.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pnoker
 * @description 时间工具类
 */
public class DateUtil {
    private static SimpleDateFormat sdfComplete = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdfMinute = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");

    public static String getCompleteTime(Date date) {
        return sdfComplete.format(date);
    }

    public static String getMinuteTime(Date date) {
        return sdfMinute.format(date);
    }

    public static String getDayTime(Date date) {
        return sdfDay.format(date);
    }

}
