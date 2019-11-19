package com.ftrend.zgp.utils.http;

import android.util.Log;

import com.blankj.utilcode.util.StringUtils;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.common.EncryptUtill;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Protocol;
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

    // 当前使用的token，除个别接口以外，都需要通过此token来验证身份
    private static String token = "";
    // token过期时间
    private static Date tokenExpireTime = new Date();

    /**
     * 设置token信息
     *
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

    /**
     * 拦截网络请求，注入token信息
     *
     * @param chain
     * @return
     * @throws IOException
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        //注入公共header
        if (needToken(request.url())) {
            if (!checkToken()) {
                // token获取失败，直接返回401错误
                return new Response.Builder()
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .code(401)
                        .message("token获取失败")
                        .build();
            }
            requestBuilder
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "bearer " + token)
                    .build();
        }

        request = requestBuilder.build();
        Response response = chain.proceed(request);
        if (response.code() == 401) {//401 - token无效
            clearToken();
            //获取token后重新发送请求
            if (checkToken()) {
                response = chain.proceed(request);
            }
        }
        return response;
    }

    /**
     * 判断指定的url是否需要验证token
     *
     * @param url
     * @return
     */
    private boolean needToken(HttpUrl url) {
        String urlStr = url.toString();
        if (urlStr.contains("/pos/common/ping")) return false;
        if (urlStr.contains("/pos/auth/login")) return false;
        if (urlStr.contains("/pos/auth/devReg")) return false;
        return true;
    }

    /**
     * 判断token是否有效
     *
     * @return
     */
    private boolean tokenExpired() {
        //注意：服务端生成的token有效期为24小时，且每天清空。一般不会出现token过期的情况
        Date checkTime = new Date(new Date().getTime() + 60 * 1000 * 3);
        return StringUtils.isEmpty(token) || (tokenExpireTime.before(checkTime));
    }

    /**
     * 检查token是否有效，如果已失效则重新获取
     *
     * @return
     */
    private boolean checkToken() {
        if (!tokenExpired()) {
            return true;
        }
        final boolean[] failed = {false};
        RestSubscribe.getInstance().clientLogin(ZgParams.getPosCode(), EncryptUtill.md5(ZgParams.getDevSn()),
                new RestCallback(new RestResultHandler() {
                    @Override
                    public void onSuccess(RestBodyMap body) {
                        if (body.containsKey("token") && body.containsKey("expiration")) {
                            RestInterceptor.setToken(body.getString("token"),
                                    body.getString("expiration"));
                        }
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMsg) {
                        // 获取token失败时快速返回，避免等待超时
                        failed[0] = true;
                        Log.e(TAG, "获取token失败: " + errorCode + " - " + errorMsg);
                    }
                }));
        // 异步获取token，如果30秒未成功，则认为超时失败
        Date checkStart = new Date();
        while (tokenExpired() && !failed[0]) {
            if (new Date().after(new Date(checkStart.getTime() + 30 * 1000))) {
                failed[0] = true;
            }
        }
        return !failed[0];
    }
}
