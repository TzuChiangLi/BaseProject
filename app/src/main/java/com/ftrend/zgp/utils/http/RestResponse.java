package com.ftrend.zgp.utils.http;

/**
 * Response响应报文
 *
 * @author liziqiang@ftrend.cn
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
public class RestResponse<T> {
    /**
     * 响应头
     */
    private ResHead head;
    /**
     * 响应数据，不固定
     */
    private T body;

    public static class ResHead {
        private String requestNo;
        private String createTime;
        private String retFlag;
        private String retMsg;

        public ResHead() {
        }

        public ResHead(String requestNo, String createTime, String retFlag, String retMsg) {
            this.requestNo = requestNo;
            this.createTime = createTime;
            this.retFlag = retFlag;
            this.retMsg = retMsg;
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

        public String getRetFlag() {
            return retFlag;
        }

        public void setRetFlag(String retFlag) {
            this.retFlag = retFlag;
        }

        public String getRetMsg() {
            return retMsg;
        }

        public void setRetMsg(String retMsg) {
            this.retMsg = retMsg;
        }
    }

    public RestResponse() {
    }

    public RestResponse(ResHead head, T body) {
        this.head = head;
        this.body = body;
    }

    /**
     * 判断是否执行成功
     * @return
     */
    public boolean succeed() {
        return "0000".equals(head.retFlag);
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
