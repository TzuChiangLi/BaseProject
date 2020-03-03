package com.ftrend.zgp.utils.pop;

/**
 * 优惠输入框回调
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/29
 */
public interface DscInputCallback {
    /**
     * 用户输入完毕
     */
    boolean onOk(int dscRate, double dscMoney);

    /**
     * 实时按比例计算优惠
     *
     * @param dscRate 折扣比例
     * @return 实际优惠金额
     */
    double onDscByRate(double dscRate);

    /**
     * 实时按金额计算优惠
     *
     * @param dscTotal 优惠金额
     * @return 实际优惠金额
     */
    double onDscByTotal(double dscTotal);

    /**
     * 用户取消输入
     */
    void onCancel();
}
