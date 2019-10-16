package com.ftrend.zgp.utils.pay;

import java.util.HashMap;
import java.util.Map;

/**
 * 收钱吧错误码定义
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/10/11
 */
public class SqbError {
    /**
     * 错误消息定义
     */
    private static Map<String, String> errorMap = null;

    /**
     * 初始化错误消息
     */
    private static void initErrors() {
        errorMap = new HashMap<>();
        errorMap.put("INVALID_BARCODE", "条码错误");
        errorMap.put("INSUFFICIENT_FUND", "账户金额不足");
        errorMap.put("EXPIRED_BARCODE", "过期的支付条码");
        errorMap.put("BUYER_OVER_DAILY_LIMIT", "付款人当日付款金额超过上限");
        errorMap.put("BUYER_OVER_TRANSACTION_LIMIT", "付款人单笔付款金额超过上限");
        errorMap.put("SELLER_OVER_DAILY_LIMIT", "收款账户当日收款金额超过上限");
        errorMap.put("TRADE_NOT_EXIST", "交易不存在");
        errorMap.put("TRADE_HAS_SUCCESS", "交易已被支付");
        errorMap.put("SELLER_BALANCE_NOT_ENOUGH", "卖家余额不足");
        errorMap.put("REFUND_AMT_NOT_EQUAL_TOTAL", "退款金额无效");
        errorMap.put("TRADE_FAILED", "交易失败");
        errorMap.put("UNEXPECTED_PROVIDER_ERROR", "不认识的支付通道");
        errorMap.put("TRADE_TIMEOUT", "交易超时自动撤单");
        errorMap.put("ACCOUNT_BALANCE_NOT_ENOUGH", "商户余额不足");
        errorMap.put("CLIENT_SN_CONFLICT", "client_sn在系统中已存在");
        errorMap.put("UPAY_ORDER_NOT_EXISTS", "订单不存在");
        errorMap.put("REFUNDABLE_AMOUNT_NOT_ENOUGH", "订单可退金额不足");
        errorMap.put("UPAY_TERMINAL_NOT_EXISTS", "终端号在交易系统中不存在");
        errorMap.put("UPAY_TERMINAL_STATUS_ABNORMAL", "终端未激活");
        errorMap.put("UPAY_CANCEL_ORDER_NOOP", "无效操作，订单已经是撤单状态了");
        errorMap.put("UPAY_CANCEL_INVALID_ORDER_STATE", "当前订单状态不可撤销");
        errorMap.put("UPAY_REFUND_ORDER_NOOP", "无效操作，本次退款退款已经完成了");
        errorMap.put("UPAY_REFUND_INVALID_ORDER_STATE", "当前订单状态不可退款");
        errorMap.put("UPAY_STORE_OVER_DAILY_LIMIT", "商户日收款额超过上限");
        errorMap.put("UPAY_TCP_ORDER_NOT_REFUNDABLE", "订单参与了活动并且无法撤销");
    }

    /**
     * 获取错误消息
     *
     * @param errCode 错误码
     * @return
     */
    public static String errMsg(String errCode) {
        if (errorMap == null) {
            initErrors();
        }
        if (errorMap.containsKey(errCode)) {
            return errorMap.get(errCode);
        } else {
            return "未知错误";
        }
    }


}
