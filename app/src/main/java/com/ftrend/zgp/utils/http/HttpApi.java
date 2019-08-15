package com.ftrend.zgp.utils.http;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * http请求接口
 *
 * @author LZQ
 */
public interface HttpApi {
        //https://www.wanandroid.com//hotkey/json
    @GET("/{type}/json")
    Observable<KeyWord> get(@Path("type") String type);
}
