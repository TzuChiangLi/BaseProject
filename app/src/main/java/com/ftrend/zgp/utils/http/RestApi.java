package com.ftrend.zgp.utils.http;

import java.util.Map;

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
    Observable<RestResponse<Map<String, Object>>> clientLogin(@Body RestRequest<Map<String, Object>> request);

    /**
     * 修改用户登录密码
     *
     * @param request
     * @return
     */
    @POST("auth/changePwd")
    Observable<RestResponse<Map<String, Object>>> userChangePwd(@Body RestRequest<Map<String, Object>> request);

    /**
     * 设备注册
     *
     * @param request
     * @return
     */
    @POST("auth/devReg")
    Observable<RestResponse<Map<String, Object>>> devReg(@Body RestRequest<Map<String, Object>> request);

    /**
     * 1 获取指定机器号的数据更新标志
     *
     * @param request
     * @return
     */
    @POST("update/check/pos")
    Observable<RestResponse<Map<String, Object>>> checkPosUpdate(@Body RestRequest<Map<String, Object>> request);

    /**
     * 2 获取指定机器号可登录专柜列表
     *
     * @param request
     * @return
     */
    @POST("update/pos/dep")
    Observable<RestResponse<Map<String, Object>>> updatePosDep(@Body RestRequest<Map<String, Object>> request);

    /**
     * 3 获取指定机器号可登录用户列表
     *
     * @param request
     * @return
     */
    @POST("update/pos/user")
    Observable<RestResponse<Map<String, Object>>> updatePosUser(@Body RestRequest<Map<String, Object>> request);

    /**
     * 4 获取指定机器号系统参数列表
     *
     * @param request
     * @return
     */
    @POST("update/pos/sysparams")
    Observable<RestResponse<Map<String, Object>>> updatePosSysParams(@Body RestRequest<Map<String, Object>> request);

    /**
     * 5 获取指定专柜的商品类别列表
     *
     * @param request
     * @return
     */
    @POST("update/dep/cls")
    Observable<RestResponse<Map<String, Object>>> updateDepCls(@Body RestRequest<Map<String, Object>> request);

    /**
     * 6 获取指定专柜的商品列表
     *
     * @param request
     * @return
     */
    @POST("update/dep/product")
    Observable<RestResponse<Map<String, Object>>> updateDepProduct(@Body RestRequest<Map<String, Object>> request);

    /**
     * 7 获取指定专柜的支付方式列表
     *
     * @param request
     * @return
     */
    @POST("update/dep/payinfo")
    Observable<RestResponse<Map<String, Object>>> updateDepPayInfo(@Body RestRequest<Map<String, Object>> request);

    /**
     * 8 按机器号查询已上传实时流水（所有未交班流水号列表）
     *
     * @param request
     * @return
     */
    @POST("update/ls/list")
    Observable<RestResponse<Map<String, Object>>> queryPosLsList(@Body RestRequest<Map<String, Object>> request);

    /**
     * 9 按机器号查询指定流水（未交班流水）
     *
     * @param request
     * @return
     */
    @POST("update/ls/download")
    Observable<RestResponse<Map<String, Object>>> downloadPosLs(@Body RestRequest<Map<String, Object>> request);

    /**
     * 交班
     *
     * @param request
     * @return
     */
    @POST("trade/end")
    Observable<RestResponse<Map<String, Object>>> posEnd(@Body RestRequest<Map<String, Object>> request);

    /**
     * 查询会员信息
     *
     * @param request
     * @return
     */
    @POST("trade/vip/info")
    Observable<RestResponse<Map<String, Object>>> queryVipInfo(@Body RestRequest<Map<String, Object>> request);

    /**
     * 上传交易流水
     *
     * @param request
     * @return
     */
    @POST("upload/trade")
    Observable<RestResponse<Map<String, Object>>> uploadTrade(@Body RestRequest<Map<String, Object>> request);

    /**
     * 上传APP配置参数
     *
     * @param request
     * @return
     */
    @POST("upload/params")
    Observable<RestResponse<Map<String, Object>>> uploadAppParams(@Body RestRequest<Map<String, Object>> request);

    /**
     * 上传APP配置参数
     *
     * @param request
     * @return
     */
    @POST("update/params")
    Observable<RestResponse<Map<String, Object>>> queryAppParams(@Body RestRequest<Map<String, Object>> request);

}
