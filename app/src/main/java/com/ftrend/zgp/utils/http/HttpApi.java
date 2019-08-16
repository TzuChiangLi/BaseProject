package com.ftrend.zgp.utils.http;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * http请求接口,本类仅举例，后续根据具体的功能实现不同的api接口
 *
 * @author LZQ
 */
public interface HttpApi {
    //https://www.wanandroid.com//hotkey/json


    /**
     * 举例说明
     *
     * @return
     */
    @GET("/hotkey/json")
    Observable<KeyWord> example();


    @GET("{service}")
    Observable<ResBean<KeyWord>> get(@Path("service") String service);
}
