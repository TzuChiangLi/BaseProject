package com.ftrend.zgp.utils.http;

import com.ftrend.zgp.utils.http.base_test.User;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * http请求接口,本类仅举例，后续根据具体的功能实现不同的api接口
 *
 * @author LZQ
 */
public interface HttpApi {

    @GET("hello")
    Observable<BaseResponse<User>> getResponse();


}
//笔记备注：此类不要再用泛型了，因为是具体的接口直接传入具体的实体类
//实际使用的时候，一个