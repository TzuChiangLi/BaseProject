package com.ftrend.zgp.utils.task;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.ftrend.zgp.App;
import com.ftrend.zgp.utils.ZgParams;
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

    // 是否正在执行ping命令，防止因请求超时造成重复调用（如果ping间隔大于超时时间设置，此参数没有意义）
    private boolean isPinging = false;
    // 本地消息广播
    private LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(App.getContext());

    /**
     * 广播消息：切换到联机模式
     */
    private void broadcastOnline() {
        if (ZgParams.isIsOnline()) {
            return;//不重复发送消息
        }
        ZgParams.setIsOnline(true);
        Intent intent = new Intent(ZgParams.MSG_ONLINE);
        localBroadcastManager.sendBroadcastSync(intent);
    }

    /**
     * 广播消息：切换到单机模式
     */
    private void broadcastOffline() {
        if (!ZgParams.isIsOnline()) {
            return;//不重复发送消息
        }
        ZgParams.setIsOnline(false);
        Intent intent = new Intent(ZgParams.MSG_OFFLINE);
        localBroadcastManager.sendBroadcastSync(intent);
    }

    public void run() {
        super.run();

        while (!isInterrupted()) {
            if (!isPinging) {
                String posCode = ZgParams.getPosCode();
                String userCode = ZgParams.getCurrentUser().getUserCode();//CurrentUser不会为null
                RestSubscribe.getInstance().ping(posCode, userCode, new HttpCallBack<String>() {
                    @Override
                    public void onStart() {
                        System.out.println("ping started...");
                        isPinging = true;
                    }

                    @Override
                    public void onSuccess(String body) {
                        System.out.println(body);
                        // 广播：联机状态
                        broadcastOnline();
                        isPinging = false;
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {
                    }

                    @Override
                    public void onHttpError(int errorCode, String errorMsg) {
                        System.out.println(String.format(Locale.getDefault(), "%d - %s", errorCode, errorMsg));
                        // 广播：单机状态
                        broadcastOffline();
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
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
