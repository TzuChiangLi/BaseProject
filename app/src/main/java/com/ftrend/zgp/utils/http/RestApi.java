package com.ftrend.zgp.utils.http;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 后台服务http请求接口
 *
 * @author liziqiang@ftrend.cn
 */
public interface RestApi {

    @GET("common/ping")
    Observable<String> ping(@Query("posCode") String posCode, @Query("userCode") String userCode);

    /**
     * 客户端登录
     *
     * @param request
     * @return
     */
    @POST("auth/login")
    Observable<RestResponse<RestBodyMap>> clientLogin(@Body RestRequest<RestBodyMap> request);

    /**
     * 修改用户登录密码
     *
     * @param request
     * @return
     */
    @POST("auth/changePwd")
    Observable<RestResponse<RestBodyMap>> userChangePwd(@Body RestRequest<RestBodyMap> request);

    /**
     * 设备注册
     *
     * @param request
     * @return
     */
    @POST("auth/devReg")
    Observable<RestResponse<RestBodyMap>> devReg(@Body RestRequest<RestBodyMap> request);

    /**
     * 1 获取指定机器号的数据更新标志
     *
     * @param request
     * @return
     */
    @POST("update/check/pos")
    Observable<RestResponse<RestBodyMap>> checkPosUpdate(@Body RestRequest<RestBodyMap> request);

    /**
     * 2 获取指定机器号可登录专柜列表
     *
     * @param request
     * @return
     */
    @POST("update/pos/dep")
    Observable<RestResponse<RestBodyMap>> updatePosDep(@Body RestRequest<RestBodyMap> request);

    /**
     * 3 获取指定机器号可登录用户列表
     *
     * @param request
     * @return
     */
    @POST("update/pos/user")
    Observable<RestResponse<RestBodyMap>> updatePosUser(@Body RestRequest<RestBodyMap> request);

    /**
     * 4 获取指定机器号系统参数列表
     *
     * @param request
     * @return
     */
    @POST("update/pos/sysparams")
    Observable<RestResponse<RestBodyMap>> updatePosSysParams(@Body RestRequest<RestBodyMap> request);

    /**
     * 5 获取指定专柜的商品类别列表
     *
     * @param request
     * @return
     */
    @POST("update/dep/cls")
    Observable<RestResponse<RestBodyMap>> updateDepCls(@Body RestRequest<RestBodyMap> request);

    /**
     * 6 获取指定专柜的商品列表
     *
     * @param request
     * @return
     */
    @POST("update/dep/product")
    Observable<RestResponse<RestBodyMap>> updateDepProduct(@Body RestRequest<RestBodyMap> request);

    /**
     * 7 获取指定专柜的支付方式列表
     *
     * @param request
     * @return
     */
    @POST("update/dep/payinfo")
    Observable<RestResponse<RestBodyMap>> updateDepPayInfo(@Body RestRequest<RestBodyMap> request);

    /**
     * 8 按机器号查询已上传实时流水（所有未交班流水号列表）
     *
     * @param request
     * @return
     */
    @POST("update/ls/list")
    Observable<RestResponse<RestBodyMap>> queryPosLsList(@Body RestRequest<RestBodyMap> request);

    /**
     * 9 按机器号查询指定流水（未交班流水）
     *
     * @param request
     * @return
     */
    @POST("update/ls/download")
    Observable<RestResponse<RestBodyMap>> downloadPosLs(@Body RestRequest<RestBodyMap> request);

    /**
     * 交班
     *
     * @param request
     * @return
     */
    @POST("trade/end")
    Observable<RestResponse<RestBodyMap>> posEnd(@Body RestRequest<RestBodyMap> request);

    /**
     * 查询会员信息
     *
     * @param request
     * @return
     */
    @POST("trade/vip/info")
    Observable<RestResponse<RestBodyMap>> queryVipInfo(@Body RestRequest<RestBodyMap> request);

    /**
     * 查询退货流水
     *
     * @param request
     * @return
     */
    @POST("trade/refund/ls")
    Observable<RestResponse<RestBodyMap>> queryRefundLs(@Body RestRequest<RestBodyMap> request);

    /**
     * 会员卡信息查询请求
     *
     * @param request
     * @return
     */
    @POST("trade/card/info/request")
    Observable<RestResponse<RestBodyMap>> payCardInfoRequest(@Body RestRequest<RestBodyMap> request);

    /**
     * 会员卡信息查询结果
     *
     * @param request
     * @return
     */
    @POST("trade/card/info")
    Observable<RestResponse<RestBodyMap>> payCardInfo(@Body RestRequest<RestBodyMap> request);

    /**
     * 会员卡支付请求（只支持磁卡）
     *
     * @param request
     * @return
     */
    @POST("trade/card/pay/request")
    Observable<RestResponse<RestBodyMap>> payCardRequest(@Body RestRequest<RestBodyMap> request);

    /**
     * 会员卡支付结果
     *
     * @param request
     * @return
     */
    @POST("trade/card/pay/result")
    Observable<RestResponse<RestBodyMap>> payCard(@Body RestRequest<RestBodyMap> request);

    /**
     * 会员卡支付密码验证
     *
     * @param request
     * @return
     */
    @POST("trade/card/pay/pwd")
    Observable<RestResponse<RestBodyMap>> vipCardPwdValidate(@Body RestRequest<RestBodyMap> request);

    /**
     * 计算积分金额
     *
     * @param request
     * @return
     */
    @POST("trade/vipTotal")
    Observable<RestResponse<RestBodyMap>> calcVipTotal(@Body RestRequest<RestBodyMap> request);

    /**
     * 计算实时积分请求
     *
     * @param request
     * @return
     */
    @POST("trade/score/request")
    Observable<RestResponse<RestBodyMap>> vipScoreRequest(@Body RestRequest<RestBodyMap> request);

    /**
     * 计算实时积分结果
     *
     * @param request
     * @return
     */
    @POST("trade/score/result")
    Observable<RestResponse<RestBodyMap>> vipScore(@Body RestRequest<RestBodyMap> request);

    /**
     * 上传交易流水
     *
     * @param request
     * @return
     */
    @POST("upload/trade")
    Observable<RestResponse<RestBodyMap>> uploadTrade(@Body RestRequest<RestBodyMap> request);

    /**
     * 上传APP配置参数
     *
     * @param request
     * @return
     */
    @POST("upload/params")
    Observable<RestResponse<RestBodyMap>> uploadAppParams(@Body RestRequest<RestBodyMap> request);

    /**
     * 上传日志
     *
     * @param request
     * @return
     */
    @POST("upload/log")
    Observable<RestResponse<RestBodyMap>> uploadLog(@Body RestRequest<RestBodyMap> request);

    /**
     * 查询APP配置参数
     *
     * @param request
     * @return
     */
    @POST("update/params")
    Observable<RestResponse<RestBodyMap>> queryAppParams(@Body RestRequest<RestBodyMap> request);

    /**
     * 上传收钱吧交易记录
     *
     * @param request
     * @return
     */
    @POST("upload/sqb")
    Observable<RestResponse<RestBodyMap>> uploadSqb(@Body RestRequest<RestBodyMap> request);

    /**
     * 查询总交易报表
     *
     * @param request
     * @return
     */
    @POST("report/trade")
    Observable<RestResponse<RestBodyMap>> queryTradeReport(@Body RestRequest<RestBodyMap> request);


}
