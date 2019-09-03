package com.ftrend.zgp.utils.task;

import com.ftrend.zgp.utils.http.HttpCallBack;
import com.ftrend.zgp.utils.http.RestSubscribe;

import java.util.Locale;

/**
 * 服务器连接状态检测线程
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/3
 */
public class ServerWatcherThread extends Thread {

    public void run() {
        final boolean[] pinging = {false};
        while (true) {
            if (!pinging[0]) {
                RestSubscribe.getInstance().ping(new HttpCallBack<String>() {
                    @Override
                    public void onStart() {
                        System.out.println("ping started...");
                        pinging[0] = true;
                    }

                    @Override
                    public void onSuccess(String body) {
                        // TODO: 2019/9/3 广播：联机状态
                        System.out.println(body);
                        pinging[0] = false;
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {

                    }

                    @Override
                    public void onHttpError(int errorCode, String errorMsg) {
                        // TODO: 2019/9/3 广播：单机状态
                        System.out.println(String.format(Locale.getDefault(), "%d - %s", errorCode, errorMsg));
                        pinging[0] = false;
                    }

                    @Override
                    public void onFinish() {
                        System.out.println("ping finished...");
                    }
                });
            }
            try {
                Thread.sleep(1000 * 30);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
