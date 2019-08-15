package com.ftrend.zgp.utils.http;

/**
 * @author LZQ
 * @content HTTP请求回调，根据后续需求增减
 */
public interface HttpCallBack<T> {
    /**
     * 请求成功
     * @param t 泛型
     * @param params
     */
    void onSuccess(T t, int... params);

    /**
     * 请求开始
     */
    void onStart();

    /**
     * 请求失败
     */
    void onFailed();

    /**
     * 请求返回的错误值
     */
    void onError();

    /**
     * 请求结束
     */
    void onFinish();
}
