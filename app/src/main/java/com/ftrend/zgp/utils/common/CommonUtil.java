package com.ftrend.zgp.utils.common;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.ftrend.zgp.utils.log.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

/**
 * 全局通用方法定义
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/11
 */
public class CommonUtil {

    /**
     * 重启当前APP
     *
     * @param context - getBaseContext()，或者App.getContext()
     */
    public static void rebootApp(Context context) {
        ActivityUtils.finishAllActivities();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //与正常页面跳转一样可传递序列化数据,在Launch页面内获得
        intent.putExtra("REBOOT", "reboot");
        startActivity(intent);
        try {
            Activity activity = findActivity(context);
            if (activity != null) {
                activity.finish();
            }
        } catch (Exception e) {
            LogUtil.e("rebootApp：" + e.getMessage());
        }
    }

    @Nullable
    public static Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            ContextWrapper wrapper = (ContextWrapper) context;
            return findActivity(wrapper.getBaseContext());
        } else {
            return null;
        }
    }

    /**
     * 判断当前应用是否是debug状态
     */
    public static boolean debugMode(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成新的UUID
     *
     * @return
     */
    public static String newUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 日期格式化：yyyyMMdd
     *
     * @param date
     * @return
     */
    public static String dateToYyyyMmDd(Date date) {
        return new SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(date);
    }

    /**
     * 金额格式化
     *
     * @param amount
     * @return
     */
    public static String moneyToString(double amount) {
        String result = String.format(Locale.CHINA, "%.2f", amount);
        if (result.endsWith(".00")) {
            result = result.substring(0, result.length() - 3);
        }
        return result;
    }
}
