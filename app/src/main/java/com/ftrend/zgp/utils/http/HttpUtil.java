package com.ftrend.zgp.utils.http;

import com.ftrend.zgp.utils.LogUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
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
public class HttpUtil{
    private static HttpUtil INSTANCE;
    private static Retrofit mRetrofit;
    private static final int TIMEOUT = 60;
    private static final String baseURL = "https://www.wanandroid.com/";

    //https://www.wanandroid.com//hotkey/json
    public HttpUtil() {
        initRetrofit();
    }

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

    public static HttpUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpUtil();
        }
        return INSTANCE;
    }


    public static void get(String type) {
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
        HttpApi api = mRetrofit.create(HttpApi.class);
        api.get(type).subscribeOn(Schedulers.io())//事件发生时的线程
                .observeOn(AndroidSchedulers.mainThread())//事件发生后回调在的线程
                .subscribe(new Observer<KeyWord>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtil.d("----onSubcribe");
                    }

                    @Override
                    public void onNext(KeyWord keyWord) {
                        LogUtil.d("----onNext:" + keyWord.getData().get(0).getName());
                    }


                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d("----onError:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        LogUtil.d("----onComplete");
                    }
                });
    }


}
