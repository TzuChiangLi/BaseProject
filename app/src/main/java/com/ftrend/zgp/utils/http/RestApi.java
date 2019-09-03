package com.ftrend.zgp.utils.http;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * 后台服务http请求接口
 *
 * @author liziqiang@ftrend.cn
 */
public interface RestApi {

    @GET("common/ping")
    Observable<String> ping();

    /**
     * 客户端登录
     * @param request
     * @return
     */
    @POST("auth/login")
    Observable<RestResponse<Map<String, Object>>> clientLogin(@Body RestRequest<Map<String, Object>> request);

    /**
     * 2 获取指定机器号可登录专柜列表
     * @param request
     * @return
     */
    @POST("update/pos/dep")
    Observable<RestResponse<Map<String, Object>>> updatePosDep(@Body RestRequest<Map<String, Object>> request);

    /**
     * 3 获取指定机器号可登录用户列表
     * @param request
     * @return
     */
    @POST("update/pos/user")
    Observable<RestResponse<Map<String, Object>>> updatePosUser(@Body RestRequest<Map<String, Object>> request);

    /**
     * 4 获取指定机器号系统参数列表
     * @param request
     * @return
     */
    @POST("update/pos/sysparams")
    Observable<RestResponse<Map<String, Object>>> updatePosSysParams(@Body RestRequest<Map<String, Object>> request);

    /**
     * 5 获取指定专柜的商品类别列表
     * @param request
     * @return
     */
    @POST("update/dep/cls")
    Observable<RestResponse<Map<String, Object>>> updateDepCls(@Body RestRequest<Map<String, Object>> request);

    /**
     * 6 获取指定专柜的商品列表
     * @param request
     * @return
     */
    @POST("update/dep/product")
    Observable<RestResponse<Map<String, Object>>> updateDepProduct(@Body RestRequest<Map<String, Object>> request);

    /**
     * 7 获取指定专柜的支付方式列表
     * @param request
     * @return
     */
    @POST("update/dep/payinfo")
    Observable<RestResponse<Map<String, Object>>> updateDepPayInfo(@Body RestRequest<Map<String, Object>> request);

    // TODO: 2019/9/2 8 按机器号查询已上传实时流水（所有未交班流水号列表）
//    @POST("update/pos/ls")
//    Observable<RestResponse<Map<String, Object>>> updatePosLs(@Body RestRequest<Map<String, Object>> request);

}
