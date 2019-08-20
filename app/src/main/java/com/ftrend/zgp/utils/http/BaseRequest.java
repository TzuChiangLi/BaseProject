package com.ftrend.zgp.utils.http;

/**
 * Request请求报文
 *
 * @author LZQ
 * =================================POST====================================
 * {
 * "head": {
 * "requestNo": "",  //请求编号，唯一
 * "createTime": "",  //请求发起时间
 * "token": ""  //身份认证token，提前通过认证接口获取，一定时间内有效
 * },
 * "body": {
 * ...
 * }
 * }
 * =================================GET====================================
 * ?requestNo=&createTime=&access_token=
 */
public class BaseRequest<T> {
    /**
     * 请求头
     */
    private Head head;
    /**
     * 请求数据，格式不固定
     */
    private T body;

    private class Head {
        private String requestNo;
        private String createTime;
        private String token;
    }

}
