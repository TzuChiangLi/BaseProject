package com.ftrend.zgp.utils.http;

import com.ftrend.zgp.utils.http.BaseResponse.ResHead;

/**
 * @author LZQ
 * @content HTTP请求回调，根据后续需求增减。Presenter继承此回调，然后在http请求方法的参数中加入此回调即可。
 */
public interface HttpCallBack<T> {
    /**
     * 请求开始
     */
    void onStart();

    /**
     * 请求成功
     */
    void onSuccess(T body, ResHead head);


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
