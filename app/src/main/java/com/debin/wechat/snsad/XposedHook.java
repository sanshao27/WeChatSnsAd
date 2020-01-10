package com.debin.wechat.snsad;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.debin.wechat.snsad.util.LogUtil;
import com.debin.wechat.snsad.util.XPrefUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedHook implements IXposedHookLoadPackage {
    private static String TAG = XposedHook.class.getSimpleName();

    private Context context;
    private final Set<Integer> adSet = new HashSet<>();

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpParam) throws Throwable {
        if (!XPrefUtils.removeWeChatSnsAdEnable()) {
            return;
        }

        if ("com.tencent.mm".equals(lpParam.packageName)) {
            WeChatAdHook.getInstance().hideWeChatSnsTimeLineUIAD(lpParam);
        }
    }

    @Deprecated
    private void hookA(final XC_LoadPackage.LoadPackageParam lpParam) {
        try {
            String className = "com.tencent.mm.plugin.sns.ui.a.a";
            final Class clazz = lpParam.classLoader.loadClass(className);
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
                                        context = (Context) param.args[0];
                                        adSet.clear();
                                    }
                                });
                        break;
                    }
                }

                XposedHelpers.findAndHookMethod(clazz, "getView", int.class, View.class, ViewGroup.class,
                        new XC_MethodReplacement() {
                            @Override
                            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                                if (adSet.contains(param.args[0])) {
                                    return new View(context);
                                }
                                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject,
                                        param.args);
                            }
                        });
            } else {
                LogUtil.e(TAG, "class " + className + " not found");
            }

            className = "com.tencent.mm.plugin.sns.ui.a.a$1";
            final Class dataChangeClazz = lpParam.classLoader.loadClass(className);
            if (dataChangeClazz != null) {
                XposedHelpers.findAndHookMethod(dataChangeClazz, "cll", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        adSet.clear();
                    }
                });
            } else {
                LogUtil.e(TAG, "class " + className + " not found");
            }
        } catch (Throwable e) {
            LogUtil.e(TAG, "hookA err:" + Log.getStackTraceString(e));
        }
    }

    @Deprecated
    private void hookC(final XC_LoadPackage.LoadPackageParam lpParam) {
        try {
            final String className = "com.tencent.mm.plugin.sns.ui.a.b.a";
            final String avClassName = "com.tencent.mm.plugin.sns.ui.av";
            final Class clazz = lpParam.classLoader.loadClass(className);
            final Class avClazz = lpParam.classLoader.loadClass(avClassName);
            if (avClazz == null) {
                LogUtil.e(TAG, "class " + className + " not found");
                return;
            }

            final Field isAdField = XposedHelpers.findFieldIfExists(avClazz, "qkM");
            if (clazz != null) {
                XposedHelpers.findAndHookMethod(clazz, "Cw", int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Object result = param.getResult();
                        if (result == null) {
                            return;
                        }
                        boolean isAd = (boolean) isAdField.get(result);
                        if (isAd) {
                            int position = (int) param.args[0];
                            adSet.add(position);
                            LogUtil.e(TAG, "position: " + position + " is ad");
                        }
                    }
                });
            } else {
                LogUtil.e(TAG, "class " + className + " not found");
            }
        } catch (Throwable e) {
            LogUtil.e(TAG, "hookA err:" + Log.getStackTraceString(e));
        }
    }
}
