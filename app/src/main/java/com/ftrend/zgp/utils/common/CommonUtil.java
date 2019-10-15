package com.ftrend.zgp.utils.common;

import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //与正常页面跳转一样可传递序列化数据,在Launch页面内获得
        intent.putExtra("REBOOT", "reboot");
        startActivity(intent);
    }

    public static String todayToString() {
        return dateToString(new Date());
    }

    public static String dateToString(Date date) {
        return new SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(date);
    }
}
