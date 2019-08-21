package com.ftrend.zgp.utils.http;

/**
 * Http管理类
 * 负责请求异常、错误的处理以及多个BaseURL的存储
 *
 * @author liziqiang@ftrend.cn
 */
public class HttpManager {

    public static final String TEST_URL = "https://www.wanandroid.com/";

    /**
     * 在响应报文中如果返回的是错误编码，就在此回调处理
     *
     * @param error
     * @return
     */
    public static String onErrorMessage(String error) {
        switch (error) {
            case "":
                return "";
            default:
                return "";
        }
    }

    /**
     * 处理Retrofit的onError回调
     *
     * @param error
     * @return
     */
    public static String onThrowableMessage(Throwable error) {
        return "";
    }


}
