package com.ftrend.zgp.utils.http;

import java.net.SocketException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;

import retrofit2.HttpException;

/**
 * Observer基类，实现一些通用功能
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/3
 */
public abstract class AbstractObserver {

    protected String parseHttpError(Throwable e) {
        String errorMessage;
        if (e instanceof HttpException) {
            switch (((HttpException) e).code()) {
                //5XX是服务器错误
                case 504:
                    errorMessage = "网络异常，请检查您的网络状态";
                    break;
                case 503:
                    errorMessage = "当前服务不可用";
                    break;
                //4XX是请求错误
                case 404:
                    errorMessage = "请求的地址不存在";
                    break;
                case 403:
                    errorMessage = "服务器拒绝请求";
                    break;
                case 400:
                    errorMessage = "发送了错误的请求";
                    break;
                default:
                    errorMessage = "Error:" + e.getMessage();
                    break;
            }
        } else if (e instanceof SocketException) {
            errorMessage = "请求超时";
/*        } else if (e instanceof ConnectException) {//Always false
            errorMessage = "网络连接超时";*/
        } else if (e instanceof SSLHandshakeException) {
            errorMessage = "安全证书异常";
        } else if (e instanceof UnknownHostException) {
            errorMessage = "域名解析失败";
        } else {
            errorMessage = "Error：" + e.getMessage();
        }
        return errorMessage;
//    200 OK：客户端请求成功。
//
//　　400 Bad Request：客户端请求有语法错误，不能被服务器所理解。
//
//　　401 Unauthorized：请求未经授权，这个状态代码必须和WWW-Authenticate报头域一起使用。
//
//　　403 Forbidden：服务器收到请求，但是拒绝提供服务。
//
//　　404 Not Found：请求资源不存在，举个例子：输入了错误的URL。
//
//　　500 Internal Server Error：服务器发生不可预期的错误。
//
//　　503 Server Unavailable：服务器当前不能处理客户端的请求，一段时间后可能恢复正常，举个例子：HTTP/1.1 200 OK(CRLF)。
    }
}
