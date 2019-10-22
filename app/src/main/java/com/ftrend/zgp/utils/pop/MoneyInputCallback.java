package com.ftrend.zgp.utils.pop;

/**
 * 输入面板回调
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/21
 */
public interface MoneyInputCallback {
    /**
     * 用户输入完毕
     */
    void onOk(double value);

    /**
     * 用户取消输入
     */
    void onCancel();

    /**
     * 校验输入数据是否有效
     *
     * @param value
     * @return 错误提示信息，为空时表明数据校验通过
     */
    String validate(double value);
}
