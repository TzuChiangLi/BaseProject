package com.ftrend.zgp.utils.log;

import android.os.Environment;
import android.util.Log;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 打印日志工具类
 *
 * @author liziqiang@ftrend.cn
 */
public class LogUtil {
    static String logPathStr = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DATA/" + AppUtils.getAppPackageName() + "/LogUtil/";
    /**
     * 允许保存Error错误到本地日志
     */
    private static boolean saveError = true;

    /**
     * 允许在控制台打印log
     */
    private static boolean showLog = true;

    /**
     * 初始化并创建本地记录文件
     */
    public static void init() {
        File logPath = new File(logPathStr);
        if (!logPath.exists()) {
            logPath.mkdirs();
        }
        File logFile = new File(logPathStr + "log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                LogUtil.e(e.getMessage());
            }
        }
    }


    /**
     * 在try-catch中使用以便保存到本地
     *
     * @param msg
     */
    public static void e(String msg) {
        if (showLog) {
            Log.e(ActivityUtils.getTopActivity().getLocalClassName(), msg);
        }
        if (saveError) {
            FileWriter writer = null;
            try {
                init();
                writer = new FileWriter(new File(logPathStr + "log.txt"), true);
                BufferedWriter bufWriter = new BufferedWriter(writer);
                bufWriter.write(String.format("----------------------------------------------------------------------------------------------%s%s%s%s%s",
                        "\n异常原因：",
                        msg, "\n发生时间：",
                        new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss").format(new Date()),
                        "\n"));
                bufWriter.newLine();
                bufWriter.close();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(ActivityUtils.getTopActivity().getLocalClassName(), "为避免发生死循环错误: " + e.getMessage());
            }
        }
    }

    public static void d(String msg) {
        if (showLog) {
            Log.d(ActivityUtils.getTopActivity().getLocalClassName(), msg);
        } else {
            return;
        }
    }

    public static void i(String msg) {
        if (showLog) {
            Log.i(ActivityUtils.getTopActivity().getLocalClassName(), msg);
        } else {
            return;
        }
    }

    public static void setSaveError(boolean saveError) {
        LogUtil.saveError = saveError;
    }

    public static void setShowLog(boolean showLog) {
        LogUtil.showLog = showLog;
    }
}
