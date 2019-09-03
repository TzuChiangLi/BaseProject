package com.ftrend.zgp.utils.http;

import java.util.Map;

/**
 * RestResultHandler
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/3
 */
public interface RestResultHandler {

    void onSuccess(Map<String, Object> body);

    void onFailed(String errorCode, String errorMsg);
}
