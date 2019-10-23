package com.ftrend.zgp.utils;

import java.util.Map;

/**
 * 通用的操作结果回调：成功或失败
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/22
 */
public interface OperateCallback {
    /**
     * 操作成功
     *
     * @param data 返回的数据，可为空
     */
    void onSuccess(Map<String, Object> data);

    /**
     * 操作失败
     *
     * @param code 错误码
     * @param msg  错误消息
     */
    void onError(String code, String msg);
}
