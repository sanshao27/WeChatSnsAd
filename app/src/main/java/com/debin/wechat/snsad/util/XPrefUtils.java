package com.debin.wechat.snsad.util;


import com.debin.wechat.snsad.BuildConfig;
import de.robv.android.xposed.XSharedPreferences;

public class XPrefUtils {

    private static XSharedPreferences instance = null;

    private static XSharedPreferences getInstance() {
        if (instance == null) {
            instance = new XSharedPreferences(BuildConfig.APPLICATION_ID);
            instance.makeWorldReadable();
        } else {
            instance.reload();
        }
        return instance;
    }

    private XPrefUtils() {

    }

    public static boolean removeWeChatSnsAdEnable() {
        return getInstance().getBoolean(PrefUtil.REMOVE_WECHAT_SNSAD, true);
    }
}
