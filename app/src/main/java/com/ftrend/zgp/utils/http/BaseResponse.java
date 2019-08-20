package com.ftrend.zgp.utils.http;

/**
 * Response响应报文
 *
 * @author LZQ
 * * =============================RESPONSE====================================
 * {
 * "head": {
 * "requestNo": "",  //请求编号，和对应的request报文一致
 * "createTime": "",  //响应时间
 * "retFlag": "",  //请求执行结果码，0000代表成功
 * "retMsg": ""  //错误消息，成功时为空或者默认值
 * },
 * "body": {
 * ...
 * }
 * }
 * ==========================================================================
 */
public class BaseResponse<T> {
    /**
     * 响应头
     */
    private ResHead head;
    /**
     * 响应数据，不固定
     */
    private T body;

    public class ResHead {
        private String requestNo;
        private String createTime;
        private String retFlag;
        private String retMsg;
    }




    public ResHead getHead() {
        return head;
    }

    public void setHead(ResHead head) {
        this.head = head;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
