package com.ftrend.zgp.utils.pay;

import com.ftrend.zgp.model.DepPayInfo;
import com.ftrend.zgp.model.DepPayInfo_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * PayType
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/17
 */
public class PayType {
    //0	现金
    public final static String PAYTYPE_CASH = "0";
    //6	购物券
    public final static String PAYTYPE_COUPON = "6";
    //7	IC卡
    public final static String PAYTYPE_ICCARD = "7";
    //8	储值卡
    public final static String PAYTYPE_PREPAID = "8";
    //9	长款
    public final static String PAYTYPE_TREASURE = "9";
    //A	银联卡
    public final static String PAYTYPE_UNIONPAY = "A";
    //支付方式一共有‘1’..'5' ,'B'..'K'是可以自定义

    /**
     * 把APP端识别的支付类型转换成后台支持的支付类型
     *
     * @param depCode    专柜代码
     * @param appPayType APP支付类型
     * @return
     */
    public static String appPayTypeToPayType(String depCode, String appPayType) {
        //支付方式所有专柜通用，不再做区分
        return SQLite.select(DepPayInfo_Table.payTypeCode).from(DepPayInfo.class)
                .where(DepPayInfo_Table.depCode.eq("000"))
                .and(DepPayInfo_Table.appPayType.eq(appPayType))
                .querySingle().getPayTypeCode();
    }

    /**
     * 收钱吧支付方式转换成APP支付类型
     *
     * @param payway 收钱吧支付方式
     * @return
     */
    public static String sqbPaywayToAppPayType(String payway) {
        return "SQB_" + payway;
    }
}
