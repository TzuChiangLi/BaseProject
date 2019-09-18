package com.ftrend.zgp.utils.http;

import java.util.Map;

/**
 * 后台服务接口请求回调
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/2
 */
public class RestCallback implements HttpCallBack<Map<String, Object>> {

    private RestResultHandler resultHandler = null;

    public RestCallback() {}

    public RestCallback(RestResultHandler handler) {
        this.resultHandler = handler;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onSuccess(Map<String, Object> body) {
        if (resultHandler != null) {
            resultHandler.onSuccess(body);
        }
    }

    @Override
    public void onFailed(String errorCode, String errorMsg) {
//        Log.d(TAG, "-----------------------------onFailed: " + errorMsg);
        if (resultHandler != null) {
            resultHandler.onFailed(errorCode, errorMsg);
        }
    }

    @Override
    public void onHttpError(int errorCode, String errorMsg) {
//        Log.d(TAG, "-----------------------------onHttpError: " + errorMsg);
        if (resultHandler != null) {
            resultHandler.onFailed(Integer.toString(errorCode), errorMsg);
        }
    }

    @Override
    public void onFinish() {
//        Log.d(TAG, "-----------------------------onFinish: ");
    }
}
