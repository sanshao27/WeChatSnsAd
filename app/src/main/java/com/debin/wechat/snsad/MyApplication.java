package com.debin.wechat.snsad;

import android.app.Application;
import com.debin.wechat.snsad.util.PrefUtil;

public class MyApplication extends Application {

    private static PrefUtil prefUtil;

    @Override
    public void onCreate() {
        super.onCreate();
        prefUtil = new PrefUtil(this);
    }

    public static PrefUtil getPrefUtil() {
        return prefUtil;
    }
}
