package com.ftrend.zgp.utils.log;

import android.os.Looper;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.ftrend.zgp.utils.msg.MessageUtil;

import java.util.Locale;

/**
 * 异常捕捉
 *
 * @author liziqiang@ftrend.cn
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    /**
     * 系统默认的UncaughtException处理类
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    /**
     * 单例实例
     */
    private static CrashHandler INSTANCE = null;

    private CrashHandler() {
    }

    /**
     * 获取UniException实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }

    public void init() {
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置默认的处理器为本处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(Thread t, final Throwable e) {
        //用户先处理
        if (!handleException(e) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(t, e);
        } else {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
                LogUtil.u(TAG, "异常捕获处理", "Error : " + ex.getMessage());
            }
            // 退出程序
            ActivityUtils.finishAllActivities();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    /**
     * @param e 异常
     * @return 如果已经处理：true，否则：false
     */
    private boolean handleException(final Throwable e) {
        if (e == null) {
            return false;
        }
        // 提示信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                MessageUtil.show("很抱歉程序出现异常，即将退出");
                Looper.loop();
            }
        }.start();
        //上传异常信息
        LogUtil.u(TAG, "捕获全局异常",
                String.format(Locale.CHINA, "%s", collectDeviceInfo(e)));
        return true;

    }


    /**
     * 收集设备参数信息
     *
     * @param e 异常
     */
    public String collectDeviceInfo(Throwable e) {
        //2020年1月17日14:36:13 可以加入更多信息
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.CHINA, "版本名称:%s\r\n", AppUtils.getAppVersionName()));
        sb.append(String.format(Locale.CHINA, "版本号:%s \r\n", String.valueOf(AppUtils.getAppVersionCode())));
        sb.append(String.format(Locale.CHINA, "异常原因:\r\n%s\r\n", e.getLocalizedMessage()));
        sb.append("出错位置:\r\n");
        for (int i = 0; i < e.getStackTrace().length; i++) {
            sb.append(String.format(Locale.CHINA, "%s\r\n", e.getStackTrace()[i]));
        }
        return sb.toString();
    }
}
