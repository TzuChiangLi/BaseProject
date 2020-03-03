package com.ftrend.zgp.utils.task;

import android.util.Log;

import com.ftrend.zgp.model.UserLog;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.RestBodyMap;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * 日志上传线程
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/6
 */
public class LogUploadThread extends Thread {
    private final String TAG = "LogUploadThread";

    //是否正在上传数据，防止重复调用
    private boolean isUploading = false;

    @Override
    public void run() {
        super.run();
        String posCode = ZgParams.getPosCode();
        while (!isInterrupted()) {
            List<UserLog> list = SQLite.select().from(UserLog.class)
                    .limit(10)
                    .queryList();
            if (list.size() == 0 || !ZgParams.isIsOnline()) {
                //没有需要上传的数据或者单机模式，等待一段时间
                Log.e(TAG, "没有需要上传的日志或者单机模式，10秒钟后继续");
                try {
                    Thread.sleep(1000 * 10);
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }
            for (final UserLog log : list) {
                if (isInterrupted()) {
                    break;//线程终止，停止上传
                }
                if (log == null) {
                    continue;
                }
                isUploading = true;
                RestSubscribe.getInstance().uploadLog(posCode, log, new RestCallback(new RestResultHandler() {
                    @Override
                    public void onSuccess(RestBodyMap body) {
                        log.delete();
                        setUploaded();
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMsg) {
                        Log.e(TAG, "日志上传失败: " + errorCode + " - " + errorMsg);
                        setUploaded();//上传失败不做处理，下次再上传
                    }
                }));
                while (isUploading) {
                    //等待上传完成
                    yield();
                    if (isInterrupted()) {
                        break;//线程终止，停止等待
                        //此时退出可能导致流水重复上传（未标记已上传），后台服务有对应的处理机制
                    }
                }

            }
        }
    }


    /**
     * 设置当前流水上传完毕
     */
    private void setUploaded() {
        isUploading = false;
    }

}
