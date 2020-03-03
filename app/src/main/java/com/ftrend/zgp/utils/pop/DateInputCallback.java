package com.ftrend.zgp.utils.pop;

import java.util.Date;

/**
 * 日期输入回调
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin
 * @since 2019/11/13
 */
public interface DateInputCallback {
    /**
     * 用户输入完毕
     */
    void onOk(Date value);

    /**
     * 用户取消输入
     */
    void onCancel();
}
