package com.ftrend.zgp.utils.http;

import com.ftrend.zgp.utils.LogUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * NetUtil网络请求工具类，配置连接，封装请求方法(未完成)
 *
 * @author LZQ
 */
public class HttpUtil {
    private static HttpUtil INSTANCE;
    private static Retrofit mRetrofit;
    private static final int TIMEOUT = 60;
    private static final String baseURL = "https://www.wanandroid.com/";

    public HttpUtil() {
        initRetrofit();
    }

    /**
     * 初始化Retrofit
     */
    private static void initRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 设置超时
        builder.connectTimeout(TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(TIMEOUT, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        mRetrofit = new Retrofit.Builder()
                // 设置解析转换工厂，用自己定义的
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }

    /**
     * @return 返回请求工具类的单例
     */
    public static HttpUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpUtil();
        }
        return INSTANCE;
    }

    /**
     * 获取对应的Serviceapi
     *
     * @param service Service 的 class
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> service) {
        LogUtil.d("----"+mRetrofit.baseUrl());
        return mRetrofit.create(service);
    }






}
