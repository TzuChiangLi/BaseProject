package com.ftrend.zgp.utils.http;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
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
     * @param observable //     * @param callback   , HttpCallBack<T> callback
     * @param <T>
     */
    private <T> void detachAndSubscribe(Observable observable, final HttpCallBack<T> callback) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RestObserver(callback));
    }

    private void detachAndSubscribeText(Observable observable, final HttpCallBack<String> callback) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new TextObserver(callback));
    }

    /**
     * 后台服务心跳检测
     * @param callback
     */
    public void ping(final HttpCallBack<String> callback) {
        detachAndSubscribeText(api.ping(), callback);
    }

    /**
     * 修改用户登录密码
     *
     * @param userCode 用户编号
     * @param oldPwd   旧密码
     * @param newPwd   新密码
     * @param callback
     */
    public void userChangePwd(final String userCode, final String oldPwd, final String newPwd,
                              final RestCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("userCode", userCode);
        params.put("oldPwd", oldPwd);
        params.put("newPwd", newPwd);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.userChangePwd(request), callback);
    }

    /**
     * 设备注册
     *
     * @param posCode  机器号
     * @param regCode  注册号
     * @param devSn    设备识别码（设备SN）
     * @param devStyle 设备型号
     * @param callback
     */
    public void devReg(final String posCode, final String regCode, final String devSn,
                       final String devStyle, final RestCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("posCode", posCode);
        params.put("regCode", regCode);
        params.put("devSn", devSn);
        params.put("devStyle", devStyle);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.devReg(request), callback);
    }

    /**
     * 客户端登录
     * @param posCode 机器号
     * @param callback
     */
    public void clientLogin(final String posCode, final String regCode, final RestCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("account", posCode);
        params.put("password", regCode);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.clientLogin(request), callback);
    }

    /**
     * 1 获取指定机器号的数据更新标志
     *
     * @param posCode 机器号
     * @param callback
     */
    public void checkPosUpdate(final String posCode, final RestCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("posCode", posCode);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.checkPosUpdate(request), callback);
    }

    /**
     * 2 获取指定机器号可登录专柜列表
     * @param posCode 机器号
     * @param callback
     */
    public void updatePosDep(final String posCode, final RestCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("posCode", posCode);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updatePosDep(request), callback);
    }

    /**
     * 3 获取指定机器号可登录用户列表
     * @param posCode 机器号
     * @param callback
     */
    public void updatePosUser(final String posCode, final RestCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("posCode", posCode);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updatePosUser(request), callback);
    }

    /**
     * 4 获取指定机器号系统参数列表
     * @param posCode 机器号
     * @param callback
     */
    public void updatePosSysParams(final String posCode, final RestCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("posCode", posCode);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updatePosSysParams(request), callback);
    }

    /**
     * 5 获取指定专柜的商品类别列表
     * @param depCode 专柜编码
     * @param callback
     */
    public void updateDepCls(final String depCode, final RestCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("depCode", depCode);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updateDepCls(request), callback);
    }

    /**
     * 6 获取指定专柜的商品列表
     * @param depCode 专柜编码
     * @param callback
     */
    public void updateDepProduct(final String depCode, final RestCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("depCode", depCode);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updateDepProduct(request), callback);
    }

    /**
     * 7 获取指定专柜的支付方式列表
     * @param depCode 专柜编码
     * @param callback
     */
    public void updateDepPayInfo(final String depCode, final RestCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("depCode", depCode);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updateDepPayInfo(request), callback);
    }

    /**
     * 交班
     *
     * @param posCode  机器号
     * @param callback
     */
    public void posEnd(final String posCode, final RestCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("posCode", posCode);
        RestRequest<Map<String, Object>> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.posEnd(request), callback);
    }

}
