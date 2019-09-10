package com.ftrend.zgp.utils.http;

import java.util.Map;

/**
 * 用于简化后台服务请求回调
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/3
 */
public interface RestResultHandler {
    /**
     * 请求成功
     *
     * @param body 后台服务返回的数据
     */
    void onSuccess(Map<String, Object> body);

    /**
     * 请求失败
     * @param errorCode 错误码
     * @param errorMsg 错误信息
     */
    void onFailed(String errorCode, String errorMsg);
}
