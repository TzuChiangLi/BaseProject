package com.ftrend.zgp.utils.log;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.ftrend.zgp.model.UserLog;
import com.ftrend.zgp.model.UserLog_Table;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.db.TransHelper;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 打印日志工具类
 *
 * @author liziqiang@ftrend.cn
 */
public class LogUtil {
    private final static String TAG = "Ftrend";

    /**
     * 非数据库字段：保存当前所在模块
     */
    private static String currentModule = "";

    /**
     * 日志保存目录
     */
    private static String logPathStr = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DATA/" + AppUtils.getAppPackageName() + "/LogUtil/";
    /**
     * 允许保存Error错误到本地日志
     */
    private static boolean saveError = true;

    /**
     * 允许在控制台打印log
     */
    private static boolean showLog = true;

    /**
     * 日志最大大小
     */
    private static final long MAX_SIZE = 1024 * 1024 * 10;

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
        if (!TextUtils.isEmpty(msg)) {
            if (showLog) {
                Log.e(TAG, msg);
            }
            if (saveError) {
                FileWriter writer;
                try {
                    init();
                    writer = new FileWriter(new File(logPathStr + "log.txt"), true);
                    BufferedWriter bufWriter = new BufferedWriter(writer);
                    bufWriter.write(String.format("----------------------------------------------------------------------------------------------%s%s%s%s%s",
                            "\n异常原因：",
                            msg, "\n发生时间：",
                            getDateTime().toString(),
                            "\n"));
                    bufWriter.newLine();
                    bufWriter.close();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "为避免发生死循环错误: " + e.getMessage());
                }
            }
        }
    }

    public static void d(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            if (showLog) {
                Log.d(TAG, msg);
            }
        }
    }

    public static void i(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            if (showLog) {
                Log.i(TAG, msg);
            }
        }
    }

    /**
     * @param function 功能代码（中文）
     * @param content  操作内容
     */
    public static void u(final String function, final String content) {
        u(currentModule, function, content);
    }

    public static void u(final String module, final String function, final String content) {
        UserLog userLog = new UserLog();
        //当前用户名
        userLog.setUserCode(ZgParams.getCurrentUser().getUserCode());
        //专柜
        userLog.setDepCode(ZgParams.getCurrentDep().getDepCode());
        //类名
        userLog.setModule(module);
        //方法名
        userLog.setFunction(function);
        //内容，写卡内容，读卡结果
        userLog.setContent(content);
        //发生时间
        userLog.setOccurTime(new Date());
        userLog.save();
    }

    /**
     * 清理记录
     */
    public static void c() {
        TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                clear(databaseWrapper);
                return false;
            }
        });
    }

    private static void clear(DatabaseWrapper databaseWrapper) {
        File file = new File(logPathStr + "log.txt");
        try {
            if (file.length() > MAX_SIZE) {
                file.delete();
            }
            file.createNewFile();
        } catch (Exception e) {
            LogUtil.d(e.getMessage());
        }
        Date now = new Date();
        SQLite.delete().from(UserLog.class)
                .where(UserLog_Table.occurTime.lessThan(new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000L)))
                .execute(databaseWrapper);
    }

    /**
     * 捕捉异常信息
     *
     * @param throwable 异常
     * @return 异常信息
     */
    public static String getStackTraceInfo(final Throwable throwable) {
        PrintWriter pw = null;
        Writer writer = new StringWriter();
        try {
            pw = new PrintWriter(writer);
            throwable.printStackTrace(pw);
        } catch (Exception e) {
            return "";
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        return writer.toString();
    }

    public static Date getDateTime() {
        Date date = new Date();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        date = ts;

        return date;
    }

    public static Timestamp getTimestamp() {
        Date date = new Date();
        return new Timestamp(System.currentTimeMillis());
    }

    public static void setSaveError(boolean saveError) {
        LogUtil.saveError = saveError;
    }

    public static void setShowLog(boolean showLog) {
        LogUtil.showLog = showLog;
    }

    public static String getCurrentModule() {
        return currentModule;
    }

    public static void setCurrentModule(String currentModule) {
        LogUtil.currentModule = currentModule;
    }
}