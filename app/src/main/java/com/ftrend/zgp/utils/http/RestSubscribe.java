package com.ftrend.zgp.utils.http;

import com.ftrend.zgp.model.AppParams;
import com.ftrend.zgp.model.SqbPayOrder;
import com.ftrend.zgp.model.SqbPayResult;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradePay;
import com.ftrend.zgp.model.TradeProd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 后台服务请求工具类，实现通用封装，简化调用
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

    public static void resetInstance() {
        INSTANCE = null;
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
     *
     * @param callback
     */
    public void ping(final String posCode, final String userCode, final HttpCallBack<String> callback) {
        detachAndSubscribeText(api.ping(posCode, userCode), callback);
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
        RestBodyMap params = new RestBodyMap();
        params.put("userCode", userCode);
        params.put("oldPwd", oldPwd);
        params.put("newPwd", newPwd);
        RestRequest<RestBodyMap> request = new RestRequest<>();
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
        RestBodyMap params = new RestBodyMap();
        params.put("posCode", posCode);
        params.put("regCode", regCode);
        params.put("devSn", devSn);
        params.put("devStyle", devStyle);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.devReg(request), callback);
    }

    /**
     * 客户端登录
     *
     * @param posCode  机器号
     * @param callback
     */
    public void clientLogin(final String posCode, final String regCode, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("account", posCode);
        params.put("password", regCode);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.clientLogin(request), callback);
    }

    /**
     * 1 获取指定机器号的数据更新标志
     *
     * @param posCode  机器号
     * @param callback
     */
    public void checkPosUpdate(final String posCode, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("posCode", posCode);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.checkPosUpdate(request), callback);
    }

    /**
     * 2 获取指定机器号可登录专柜列表
     *
     * @param posCode  机器号
     * @param callback
     */
    public void updatePosDep(final String posCode, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("posCode", posCode);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updatePosDep(request), callback);
    }

    /**
     * 3 获取指定机器号可登录用户列表
     *
     * @param posCode  机器号
     * @param callback
     */
    public void updatePosUser(final String posCode, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("posCode", posCode);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updatePosUser(request), callback);
    }

    /**
     * 4 获取指定机器号系统参数列表
     *
     * @param posCode  机器号
     * @param callback
     */
    public void updatePosSysParams(final String posCode, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("posCode", posCode);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updatePosSysParams(request), callback);
    }

    /**
     * 5 获取指定专柜的商品类别列表
     *
     * @param depCode  专柜编码
     * @param callback
     */
    public void updateDepCls(final String depCode, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("depCode", depCode);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updateDepCls(request), callback);
    }

    /**
     * 6 获取指定专柜的商品列表
     *
     * @param depCode  专柜编码
     * @param callback
     */
    public void updateDepProduct(final String depCode, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("depCode", depCode);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updateDepProduct(request), callback);
    }

    /**
     * 7 获取指定专柜的支付方式列表
     *
     * @param depCode  专柜编码
     * @param callback
     */
    public void updateDepPayInfo(final String depCode, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("depCode", depCode);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.updateDepPayInfo(request), callback);
    }

    /**
     * 8 按机器号查询已上传实时流水（所有未交班流水号列表）
     *
     * @param posCode  机器号
     * @param callback
     */
    public void queryPosLsList(final String posCode, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("posCode", posCode);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.queryPosLsList(request), callback);
    }

    /**
     * 9 按机器号查询指定流水（未交班流水）
     *
     * @param posCode  机器号
     * @param lsNo     流水号
     * @param callback
     */
    public void downloadPosLs(final String posCode, final String lsNo, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("posCode", posCode);
        params.put("lsNo", lsNo);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.downloadPosLs(request), callback);
    }

    /**
     * 交班
     *
     * @param posCode  机器号
     * @param callback
     */
    public void posEnd(final String posCode, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("posCode", posCode);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.posEnd(request), callback);
    }

    /**
     * 查询会员信息
     *
     * @param code     查询参数：会员卡号、会员编号、手机号，3项任传其一
     * @param type     卡片类型：1-IC卡，2-磁卡。用于服务端进行卡号处理。code不是卡号时传空字符串
     * @param callback
     */
    public void queryVipInfo(final String code, final String type, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("code", code);
        params.put("type", type);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.queryVipInfo(request), callback);
    }

    public void queryVipInfo(final String code, final RestCallback callback) {
        queryVipInfo(code, "", callback);
    }

    /**
     * 查询退货流水
     *
     * @param code     格式：yyyyMMdd+lsNo
     * @param callback
     */
    public void queryRefundLs(final String code, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("code", code);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.queryRefundLs(request), callback);
    }

    /**
     * 会员卡信息查询请求
     *
     * @param cardCode 卡号
     * @param cardType 卡类型：1-IC卡，2-磁卡
     * @param callback
     */
    public void payCardInfoRequest(final String cardCode, final String cardType, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("cardCode", cardCode);
        params.put("cardType", cardType);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.payCardInfoRequest(request), callback);
    }

    /**
     * 会员卡信息查询结果
     * 注意保存返回参数中的卡号，此卡号与数据库中的卡号一致
     *
     * @param dataSign 请求标识，由payCardInfoRequest返回
     * @param callback
     */
    public void payCardInfo(final String dataSign, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("dataSign", dataSign);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.payCardInfo(request), callback);
    }

    /**
     * 会员卡支付请求（只支持磁卡）
     * 使用完全相同的参数，多次发起支付请求，不会重复扣款
     *
     * @param posCode  机器号
     * @param lsNo     流水号
     * @param date     日期
     * @param cashier  收款员姓名
     * @param cardCode 卡号（读卡器读到的卡号）
     * @param money    支付金额
     * @param callback
     */
    public void payCardRequest(final String posCode, final String lsNo, final String date,
                               final String cashier, final String cardCode, final double money,
                               final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("posCode", posCode);
        params.put("lsNo", lsNo);
        params.put("date", date);
        params.put("cashier", cashier);
        params.put("cardCode", cardCode);
        params.put("money", money);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.payCardRequest(request), callback);
    }

    /**
     * 会员卡支付结果
     * 注意保存返回参数中的卡号，此卡号与数据库中的卡号一致
     *
     * @param dataSign
     * @param callback
     */
    public void payCard(final String dataSign, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("dataSign", dataSign);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.payCard(request), callback);
    }

    /**
     * 会员卡支付密码验证
     *
     * @param cardCode 卡号（这里的卡号是payCardInfo返回的卡号）
     * @param pwd      密码（用户输入的密码）
     * @param callback
     */
    public void vipCardPwdValidate(final String cardCode, final String pwd, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("cardCode", cardCode);
        params.put("pwd", pwd);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.vipCardPwdValidate(request), callback);
    }

    /**
     * 上传交易流水
     *
     * @param posCode  机器号
     * @param trade    交易流水信息
     * @param prodList 商品信息
     * @param pay      支付信息
     * @param callback
     */
    public void uploadTrade(final String posCode,
                            Trade trade, List<TradeProd> prodList, TradePay pay,
                            final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("posCode", posCode);
        params.put("trade", trade);
        params.put("prod", prodList);
        params.put("pay", pay);

        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.uploadTrade(request), callback);
    }

    /**
     * 上传APP配置参数
     *
     * @param posCode  机器号
     * @param list     APP配置参数列表
     * @param callback
     */
    public void uploadAppParams(final String posCode, List<AppParams> list,
                                final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("posCode", posCode);
        Map<String, String> appParams = new HashMap<>();
        for (AppParams p : list) {
            appParams.put(p.getParamName(), p.getParamValue());
        }
        params.put("params", appParams);

        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.uploadAppParams(request), callback);
    }

    /**
     * 查询APP配置参数
     *
     * @param posCode  机器号
     * @param callback
     */
    public void queryAppParams(final String posCode, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("posCode", posCode);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.queryAppParams(request), callback);
    }

    /**
     * 上传收钱吧交易记录
     *
     * @param order
     * @param result
     * @param callback
     */
    public void uploadSqb(SqbPayOrder order, SqbPayResult result, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("order", order);
        params.put("result", result);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.uploadSqb(request), callback);
    }

    /**
     * 查询总交易报表
     *
     * @param depCode   专柜编号
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @param callback
     */
    public void queryTradeReport(String depCode, String beginDate, String endDate, final RestCallback callback) {
        RestBodyMap params = new RestBodyMap();
        params.put("depCode", depCode);
        params.put("beginDate", beginDate);
        params.put("endDate", endDate);
        RestRequest<RestBodyMap> request = new RestRequest<>();
        request.setBody(params);
        detachAndSubscribe(api.queryTradeReport(request), callback);
    }
}
