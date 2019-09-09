package com.ftrend.zgp.utils.http;

import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.log.LogUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * NetUtil网络请求工具类，配置连接，封装请求方法(未完成)
 *
 * @author liziqiang@ftrend.cn
 */
public class HttpUtil {
    private static HttpUtil INSTANCE;
    private static Retrofit mRetrofit;
    private static final int CONNECT_TIMEOUT = 10;
    private static final int READ_TIMEOUT = 10;
    private static final int WRITE_TIMEOUT = 10;
    private static final String BASE_URL = String.format("http://%s/pos/", ZgParams.getServerUrl());

    private HttpUtil() {
        initRetrofit();
    }

    /**
     * 初始化Retrofit
     */
    private static void initRetrofit() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        mRetrofit = new Retrofit.Builder()
                // 设置解析转换工厂，用自己定义的
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(initClient())
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
        return mRetrofit.create(service);
    }

    /**
     * 构建client
     *
     * @return OkHttpClient
     */
    private static OkHttpClient initClient() {
        // TODO: 2019/9/2 暂时屏蔽https证书
/*        InputStream inputStream = null;
        try {
            //证书文件名需要改
            inputStream = Utils.getApp().getAssets().open("srca.cer");
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
        }
        X509TrustManager trustManager = initTrustManager(inputStream);*/
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //设置自签名证书（暂时不让他生效）
//        builder.sslSocketFactory(initSSLSocketFactory(trustManager), trustManager);
        // 设置超时
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
//        builder.addInterceptor(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                //关键在下面这句话，获取返回的response
//                Response response = chain.proceed(chain.request());
//                return response;
//            }
//        });
        builder.addInterceptor(new RestInterceptor());//拦截器，自动注入token
        OkHttpClient client = builder.build();
        return client;
    }


    /**
     * 构建SSlSocketFactory
     *
     * @param trustManager
     * @return
     */
    private static SSLSocketFactory initSSLSocketFactory(X509TrustManager trustManager) {
        SSLSocketFactory sslSocketFactory = null;
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            //使用构建出的trustManger初始化SSLContext对象
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            //获得sslSocketFactory对象
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
        }

        return sslSocketFactory;
    }


    private static X509TrustManager initTrustManager(InputStream certificate) {
        X509TrustManager trustManager = null;
        try {
            trustManager = trustManagerForCertificates(certificate);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
        }
        return trustManager;
    }

    /**
     * 获去信任自签证书的trustManager
     *
     * @param in 自签证书输入流
     * @return 信任自签证书的trustManager
     * @throws GeneralSecurityException
     */
    private static X509TrustManager trustManagerForCertificates(InputStream in)
            throws GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        //通过证书工厂得到自签证书对象集合
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }
        //为证书设置一个keyStore
        char[] password = "password".toCharArray(); // Any password will work.
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        //将证书放入keystore中
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }
        // Use it to build an X509 trust manager.
        //使用包含自签证书信息的keyStore去构建一个X509TrustManager
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }

    private static KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = null; // By convention, 'null' creates an empty key store.
            keyStore.load(null, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
