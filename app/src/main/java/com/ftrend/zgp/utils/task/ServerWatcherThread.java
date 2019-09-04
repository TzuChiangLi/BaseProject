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

    //是否正在执行ping命令，防止因请求超时造成重复调用（如果ping间隔大于超时时间设置，此参数没有意义）
    private boolean isPinging = false;

    public void run() {
        super.run();

        while (!isInterrupted()) {
            if (!isPinging) {
                RestSubscribe.getInstance().ping(new HttpCallBack<String>() {
                    @Override
                    public void onStart() {
                        System.out.println("ping started...");
                        isPinging = true;
                    }

                    @Override
                    public void onSuccess(String body) {
                        // TODO: 2019/9/3 广播：联机状态
                        System.out.println(body);
                        isPinging = false;
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {

                    }

                    @Override
                    public void onHttpError(int errorCode, String errorMsg) {
                        // TODO: 2019/9/3 广播：单机状态
                        System.out.println(String.format(Locale.getDefault(), "%d - %s", errorCode, errorMsg));
                        isPinging = false;
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
