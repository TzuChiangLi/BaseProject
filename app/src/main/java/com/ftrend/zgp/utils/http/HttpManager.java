package com.ftrend.zgp.utils.http;

/**
 * Http管理类
 * 负责请求异常、错误的处理以及多个BaseURL的存储
 * 所有的错误代码需要与后台统一
 *
 * @author liziqiang@ftrend.cn
 */
public class HttpManager {

    public static final String TEST_URL = "https://www.wanandroid.com/";

    /**
     * 在响应报文中如果返回的是错误编码，就在此回调处理
     *
     * @param error 错误信息文本
     * @return 返回错误的结果
     */
    public static String onErrorMessage(String error) {
//        switch (error) {
//            case "":
//                return "";
//            default:
//                return "";
//        }
        return "";
    }

    /**
     * 处理Retrofit的onError回调
     *
     * @param error 错误响应
     * @return 根据错误响应来确定错误文本的显示
     */
    public static String onThrowableMessage(Throwable error) {
        return "";
    }


}
