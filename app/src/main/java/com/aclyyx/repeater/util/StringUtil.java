package com.aclyyx.repeater.util;

import java.text.DecimalFormat;

/**
 * Created by aclyyx on 2018/2/14.
 */

public class StringUtil {

    private static StringUtil util;
    private DecimalFormat format00;

    private StringUtil() {
        format00 = new DecimalFormat("00");
    }

    public static StringUtil newInstance() {
        if (util == null) util = new StringUtil();
        return util;
    }

    public String timeSec2mmss(int msec) {
        int sec = msec/1000;
        return format00.format(sec/60) + ":" + format00.format(sec%60);
    }
}
