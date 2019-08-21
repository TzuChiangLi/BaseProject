package com.ftrend.zgp.utils.http;

/**
 * Request请求报文
 *
 * @author liziqiang@ftrend.cn
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

        public Head() {
        }

        public Head(String requestNo, String createTime, String token) {
            this.requestNo = requestNo;
            this.createTime = createTime;
            this.token = token;
        }

        public String getRequestNo() {
            return requestNo;
        }

        public void setRequestNo(String requestNo) {
            this.requestNo = requestNo;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public BaseRequest() {
    }

    public BaseRequest(Head head, T body) {
        this.head = head;
        this.body = body;
    }

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

}
