package com.ftrend.zgp.utils.http;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * RestSubscribe
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/2
 */
public class RestSubscribe {

    private RestApi api;
    private static RestSubscribe INSTANCE;

    /**
     * @return 返回请求工具类的单例
     */
    public static RestSubscribe getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RestSubscribe();
        }
        return INSTANCE;
    }

    public RestSubscribe() {
        api = HttpUtil.getInstance().create(RestApi.class);
    }

    /**
     * 订阅
     *
     * @param observable //     * @param callBack   , HttpCallBack<T> callBack
     * @param <T>
     */
    private <T> void detachAndSubscribe(Observable observable, final HttpCallBack<T> callBack) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RestObserver(callBack));
    }

    private void detachAndSubscribeText(Observable observable, final HttpCallBack<String> callBack) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new TextObserver(callBack));
    }

    /**
     * 后台服务心跳检测
     * @param callBack
     */
    public void ping(final HttpCallBack<String> callBack) {
        detachAndSubscribeText(api.ping(), callBack);
    }

    /**
     * 客户端登录
     * @param posCode
     * @param callBack
     */
    public void clientLogin(final String posCode, final String regCode, final RestCallback callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("account", posCode);
        params.put("password", regCode);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.clientLogin(request), callBack);
    }

    /**
     * 2 获取指定机器号可登录专柜列表
     * @param posCode
     * @param callBack
     */
    public void updatePosDep(final String posCode, final RestCallback callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("posCode", posCode);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updatePosDep(request), callBack);
    }

    /**
     * 3 获取指定机器号可登录用户列表
     * @param posCode
     * @param callBack
     */
    public void updatePosUser(final String posCode, final RestCallback callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("posCode", posCode);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updatePosUser(request), callBack);
    }

    /**
     * 4 获取指定机器号系统参数列表
     * @param posCode
     * @param callBack
     */
    public void updatePosSysParams(final String posCode, final RestCallback callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("posCode", posCode);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updatePosSysParams(request), callBack);
    }

    /**
     * 5 获取指定专柜的商品类别列表
     * @param depCode
     * @param callBack
     */
    public void updateDepCls(final String depCode, final RestCallback callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("depCode", depCode);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updateDepCls(request), callBack);
    }

    /**
     * 6 获取指定专柜的商品列表
     * @param depCode
     * @param callBack
     */
    public void updateDepProduct(final String depCode, final RestCallback callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("depCode", depCode);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updateDepProduct(request), callBack);
    }

    /**
     * 7 获取指定专柜的支付方式列表
     * @param depCode
     * @param callBack
     */
    public void updateDepPayInfo(final String depCode, final RestCallback callBack) {
        Map<String, Object> params = new HashMap<>();
        params.put("depCode", depCode);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updateDepPayInfo(request), callBack);
    }

}
