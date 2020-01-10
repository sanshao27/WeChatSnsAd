package com.debin.wechat.snsad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.debin.wechat.snsad.util.LogUtil;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WeChatAdHook {

    private static final String TAG = "WeChatAd";
    private static Map<Integer, Integer> AD_LINEARLAYOUT_MAP;
    private Context context = null;

    public static WeChatAdHook getInstance() {
        return WeChatHookUtils.instance;
    }

    @SuppressLint("UseSparseArrays")
    private WeChatAdHook() {
        AD_LINEARLAYOUT_MAP = new HashMap<>();
        //在此添加适配的微信版本
        //key：微信版本号，value：朋友圈广告条目右上角广告标识LinearLayout的id
        AD_LINEARLAYOUT_MAP.put(1363, 0x7F111B0F);
        AD_LINEARLAYOUT_MAP.put(1545, 0x7F121E84);
    }

    private static class WeChatHookUtils {
        private static final WeChatAdHook instance = new WeChatAdHook();
    }

    public void hideWeChatSnsTimeLineUIAD(XC_LoadPackage.LoadPackageParam lpParam) {
        getClassLoader(lpParam);
    }

    private void getClassLoader(final XC_LoadPackage.LoadPackageParam lpParam) {
        try {
            String className = "com.tencent.tinker.loader.app.TinkerApplication";
            Class clazz = lpParam.classLoader.loadClass(className);
            if (clazz != null) {
                XposedHelpers.findAndHookMethod(clazz,
                        "attachBaseContext", Context.class, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                Context context = (Context) param.args[0];

                                int versionCode = getVersionCode(context);
                                if (versionCode == -1) {
                                    LogUtil.e(TAG, "微信版本获取异常");
                                    return;
                                }

                                if (!AD_LINEARLAYOUT_MAP.containsKey(versionCode)) {
                                    LogUtil.e(TAG, "不支持此版本微信");
                                    return;
                                }

                                hideAD(context.getClassLoader(), AD_LINEARLAYOUT_MAP.get(versionCode));
                            }
                        });
            } else {
                LogUtil.e(TAG, "not found class : " + className);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "getClassLoader error: " + e.getLocalizedMessage());
        }
    }

    private int getVersionCode(Context context) {
        int versionCode = -1;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);// 获取包的信息
            if (packageInfo != null) {
                versionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            // 没有找到包名的时候会走此异常
            e.printStackTrace();
        }
        return versionCode;
    }

    private void hideAD(ClassLoader classLoader, final int adLinearLayoutId) {
        try {
            String className = "com.tencent.mm.plugin.sns.ui.a.c";
            final Class clazz = classLoader.loadClass(className);
            if (clazz != null) {
                Constructor[] constructors = clazz.getDeclaredConstructors();
                for (Constructor constructor : constructors) {
                    if (constructor.getParameterTypes().length == 6) {
                        XposedHelpers.findAndHookConstructor(clazz, constructor.getParameterTypes()[0],
                                constructor.getParameterTypes()[1], constructor.getParameterTypes()[2],
                                constructor.getParameterTypes()[3], constructor.getParameterTypes()[4],
                                constructor.getParameterTypes()[5], new XC_MethodHook() {
                                    @Override
                                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                        context = (Activity) param.args[0];
                                    }
                                });
                        break;
                    }
                }

                XposedHelpers.findAndHookMethod(clazz, "getView", int.class, View.class, ViewGroup.class,
                        new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                View view = (View) param.getResult();
                                View adLinearLayout = view.findViewById(adLinearLayoutId);
                                if (adLinearLayout.getVisibility() == View.VISIBLE) {
                                    LogUtil.e(TAG, "position: " + param.args[0] + " is ad");
                                    param.setResult(new View(context));
                                }
                            }
                        });
            } else {
                LogUtil.e(TAG, "class " + className + " not found");
            }
        } catch (Throwable e) {
            LogUtil.e(TAG, "hideAD err:" + Log.getStackTraceString(e));
        }
    }
}
