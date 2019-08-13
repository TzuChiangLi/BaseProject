package com.ftrend.zgp.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * NetUtil网络请求工具类，配置连接，封装请求方法(未完成)
 *
 * @author LZQ
 */
public class NetUtil {
    private static NetUtil INSTANCE;
    private Retrofit mRetrofit;
    private static final int TIMEOUT = 60;

    public NetUtil() {
        initRetrofit();
    }

    private void initRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 设置超时
        builder.connectTimeout(TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(TIMEOUT, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        mRetrofit = new Retrofit.Builder()
                // 设置解析转换工厂，用自己定义的
                .client(client)
                .build();
    }

    public static NetUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetUtil();
        }
        return INSTANCE;
    }


    /**
     * 自定义Gson解析数据类
     */
    public static class ResponseConvert extends Converter.Factory {
        public static ResponseConvert create() {
            return new ResponseConvert();
        }
    }

}
