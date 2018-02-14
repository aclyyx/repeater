package com.aclyyx.repeater.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by aclyyx on 2018/2/14.
 */

public class SharedPreferencesUtil {

    private static  SharedPreferencesUtil util;
    private SharedPreferences sp;

    private SharedPreferencesUtil(Context cxt) {
        sp = cxt.getSharedPreferences("yunbox", Context.MODE_PRIVATE);
    }

    public static SharedPreferencesUtil newInstance(Context cxt) {
        if (util == null) {
            util = new SharedPreferencesUtil(cxt);
        }
        return util;
    }

    public boolean getBooleanValue(String name, boolean defaultValue) {
        return sp.getBoolean(name, defaultValue);
    }
    public void setBooleanValue(String name, boolean value) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(name, value);
        ed.commit();
    }

    public int getIntegerValue(String name, int defaultValue) {
        return sp.getInt(name, defaultValue);
    }
    public void setIntegerValue(String name, int value) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(name, value);
        ed.commit();
    }

    public String getStringValue(String name, String defaultValue) {
        return sp.getString(name, defaultValue);
    }
    public void setStringValue(String name, String value) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(name, value);
        ed.commit();
    }
}
