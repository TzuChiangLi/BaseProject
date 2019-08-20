package com.ftrend.zgp.example;

import com.ftrend.zgp.examplePost.Login;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * http请求接口,本类仅举例，后续根据具体的功能实现不同的api接口
 *
 * @author liziqiang@ftrend.cn
 */
public interface ExampleApi {
    /**
     * 举例说明GET的方法
     *
     * @return
     */
    @GET("/hotkey/json")
    Observable<KeyWord> example();


    /**
     * https://www.wanandroid.com/user/login
     */
    @POST("user/login")
    Observable<Login> login(String username, String password);
}
