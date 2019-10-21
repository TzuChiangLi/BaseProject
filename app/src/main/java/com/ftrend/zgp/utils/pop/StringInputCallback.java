package com.ftrend.zgp.utils.pop;

/**
 * 输入回调
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/21
 */
public interface StringInputCallback {
    /**
     * 用户输入完毕
     */
    void onOk(String value);

    /**
     * 用户取消输入
     */
    void onCancel();

    /**
     * 校验输入信息是否有效
     *
     * @param value
     * @return 错误提示信息，为空时表明信息校验通过
     */
    String validate(String value);
}
