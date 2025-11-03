package com.sosd.insightnews.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    // format
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static boolean outOfDate(Date x, Date latest) {
        return x.getTime() < latest.getTime();
    }

    public static Date parse(String timeStr) {
        try {
            return df.parse(timeStr);
        } catch (Exception e) {
            return null;
        }
    }

    public static String format(Date date) {
        return df.format(date);
    }
}
