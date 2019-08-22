package com.ftrend.zgp.utils.http;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.HttpException;

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
     * @param e 错误信息文本
     * @return 返回错误的结果
     */
    public static String onErrorMessage(String e) {
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
     * @param e 错误响应
     * @return 根据错误响应来确定错误文本的显示
     */
    public static String onThrowableMessage(Throwable e) {
        if (e instanceof HttpException) {
            switch (((HttpException) e).code()) {
                case 504:
                    return "网络异常，请检查您的网络状态";
                case 404:
                    return "请求的地址不存在";
                default:
                    return "Error:" + e.getMessage();
            }
        } else if (e instanceof SocketException) {
            return "请求超时";
        } else if (e instanceof ConnectException) {
            return "网络连接超时";
        } else if (e instanceof SSLHandshakeException) {
            return "安全证书异常";
        } else if (e instanceof UnknownHostException) {
            return "域名解析失败";
        } else {
            return "Error：" + e.getMessage();
        }

    }
}