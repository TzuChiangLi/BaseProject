package com.ftrend.zgp.utils.http;

import com.ftrend.zgp.utils.http.base_test.Login;
import com.ftrend.zgp.utils.http.base_test.User;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * http请求接口,本类仅举例，后续根据具体的功能实现不同的api接口
 *
 * @author liziqiang@ftrend.cn
 */
public interface HttpApi {
    /**
     * Get测试
     *
     * @return
     */
    @GET("hello")
    Observable<BaseResponse<User>> getResponse();

    /**
     * Post测试
     *
     * @param username
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("user/login")
    Observable<Login> login(@Field("username") String username, @Field("password") String password);


}
//笔记备注：此类不用泛型，因为是具体的接口直接传入具体的实体类