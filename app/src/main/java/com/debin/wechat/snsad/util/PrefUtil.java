package com.debin.wechat.snsad.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import com.debin.wechat.snsad.BuildConfig;

public class PrefUtil {
    private static final String PREF_SUFFIX = "_preferences";
    public static final String REMOVE_WECHAT_SNSAD = "remove_wechat_snsad";

    private Context context;
    private SharedPreferences sp;

    public PrefUtil(Context ctx) {
        context = ctx;
        getSharedPreferences(BuildConfig.APPLICATION_ID + PREF_SUFFIX);
    }

    @SuppressWarnings({"deprecation", "ResultOfMethodCallIgnored"})
    @SuppressLint({"SetWorldReadable", "WorldReadableFiles"})
    private void getSharedPreferences(String prefFileName) {
        sp = context.getSharedPreferences(prefFileName, Context.MODE_WORLD_READABLE);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public boolean getBoolean(int keyId, boolean defaultValue) {
        return getBoolean(context.getString(keyId), defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).commit();
    }

    public void setBoolean(int keyId, boolean value) {
        setBoolean(context.getString(keyId), value);
    }

    public String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public String getString(int keyId, String defaultValue) {
        return getString(context.getString(keyId), defaultValue);
    }

    public void setString(String key, String value) {
        sp.edit().putString(key, value).commit();
    }

    public void setString(int keyId, String value) {
        setString(context.getString(keyId), value);
    }

    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public int getInt(int keyId, int defaultValue) {
        return getInt(context.getString(keyId), defaultValue);
    }

    public void setInt(String key, int value) {
        sp.edit().putInt(key, value).commit();
    }

    public void setInt(int keyId, int value) {
        setInt(context.getString(keyId), value);
    }
}
