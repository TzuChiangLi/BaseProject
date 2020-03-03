package com.ftrend.zgp.utils.http;

/**
 * @author liziqiang@ftrend.cn
 * HTTP请求回调，根据后续需求增减。Presenter继承此回调，然后在http请求方法的参数中加入此回调即可。
 */
public interface HttpCallBack<T> {
    /**
     * 请求开始，刷新UI
     */
    void onStart();

    /**
     * 请求成功
     *
     * @param body 结果
     */
    void onSuccess(T body);

    /**
     * 请求失败
     *
     * @param errorCode 错误代码
     * @param errorMsg  错误消息
     */
    void onFailed(String errorCode, String errorMsg);

    /**
     * HTTP错误
     *
     * @param errorCode 错误代码
     * @param errorMsg  错误消息
     */
    void onHttpError(int errorCode, String errorMsg);

    /**
     * 请求结束
     */
    void onFinish();
}
