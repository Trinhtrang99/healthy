package com.example.healthy.untils;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedUtils {

    private static final String SHARED_NAME = "HEALTHY";

    public static void saveString(Context context, String key, String value) {
        getShared(context)
                .edit()
                .putString(key, value)
                .apply();
    }

    public static String getString(Context context, String key, String defaultValue) {
        return getShared(context).getString(key, defaultValue);
    }

    public static void saveLong(Context context, String key, Long value) {
        getShared(context)
                .edit()
                .putLong(key, value)
                .apply();
    }

    public static Long getLong(Context context, String key, Long defaultValue) {
        return getShared(context).getLong(key, defaultValue);
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        getShared(context)
                .edit()
                .putBoolean(key, value)
                .apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getShared(context).getBoolean(key, defaultValue);
    }

    public static void saveInt(Context context, String key, int value) {
        getShared(context)
                .edit()
                .putInt(key, value)
                .apply();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return getShared(context).getInt(key, defaultValue);
    }

    private static SharedPreferences getShared(Context context) {
        return context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
    }

    public static void clearLocalData(Context context) {
        getShared(context).edit().clear().apply();
    }

    public static void removeKey(Context context, String key) {
        getShared(context).edit().remove(key).apply();
    }

    public static boolean isContain(Context context, String key) {
        return getShared(context).contains(key);
    }

    public static void getRemove(String key, Context context) {
        getShared(context).edit().remove(key).commit();
    }

}

