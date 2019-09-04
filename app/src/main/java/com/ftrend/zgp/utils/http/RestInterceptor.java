package com.ftrend.zgp.utils.http;

import android.util.Log;

import com.blankj.utilcode.util.StringUtils;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.EncryptUtill;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * RestInterceptor
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/2
 */
public class RestInterceptor implements Interceptor {

    private static String token = "";
    private static Date tokenExpireTime = new Date();

/*    public static void setToken(String token, Date expire) {
        RestInterceptor.token = token;
        RestInterceptor.tokenExpireTime = expire;
    }*/

    /**
     * 设置token信息
     * @param token
     * @param expire
     */
    private static void setToken(String token, String expire) {
        RestInterceptor.token = token;
        try {
            RestInterceptor.tokenExpireTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expire);
        } catch (Exception e) {
            Log.e(TAG, "token有效期解析失败", e);
            //默认有效期为24小时
            RestInterceptor.tokenExpireTime = new Date(new Date().getTime() + 1000 * 60 * 60 * 24);
        }
    }

    /**
     * 清除token信息
     */
    private void clearToken() {
        RestInterceptor.token = "";
        RestInterceptor.tokenExpireTime = new Date();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        //注入公共header
        if (needToken(request.url())) {
            checkToken();// TODO: 2019/9/3 如果token获取失败，这里直接返回401错误
            requestBuilder
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "bearer " + token)
                    .build();
        }

        request = requestBuilder.build();
        Response response = chain.proceed(request);
        if (response.code() == 401) {
            clearToken();
            //获取token后重新发送请求
            if (checkToken()) {
                response = chain.proceed(request);
            }
        }
        return response;
    }

    private boolean needToken(HttpUrl url) {
        String urlStr = url.toString();
        if (urlStr.contains("/pos/common/ping")) return false;
        if (urlStr.contains("/pos/auth/login")) return false;
        return true;
    }

    private boolean tokenExpired() {
        //注意：服务端生成的token有效期为24小时，且每天清空。一般不会出现token过期的情况
        Date checkTime = new Date(new Date().getTime() + 60 * 1000 * 3);
        return StringUtils.isEmpty(token) || (tokenExpireTime.before(checkTime));
    }

    private boolean checkToken() {
        if (!tokenExpired()) {
            return true;
        }
        RestSubscribe.getInstance().clientLogin(ZgParams.getPosCode(), EncryptUtill.md5(ZgParams.getDevSn()),
                new RestCallback(new RestResultHandler() {
                    @Override
                    public void onSuccess(Map<String, Object> body) {
                        if (body.containsKey("token") && body.containsKey("expiration")) {
                            RestInterceptor.setToken(body.get("token").toString(),
                                    body.get("expiration").toString());
                        }
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMsg) {

                    }
                }));
        Date checkStart = new Date();
        while (tokenExpired()) {
            if (new Date().after(new Date(checkStart.getTime() + 30 * 1000))) {
                return false;
            }
        }
        return true;
    }
}
