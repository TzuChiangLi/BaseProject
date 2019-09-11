package com.ftrend.zgp.utils;

import com.ftrend.zgp.model.Handover;
import com.ftrend.zgp.model.HandoverPay;
import com.ftrend.zgp.model.Handover_Table;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradePay;
import com.ftrend.zgp.model.TradePay_Table;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.TradeProd_Table;
import com.ftrend.zgp.model.Trade_Table;
import com.ftrend.zgp.utils.log.LogUtil;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import static com.raizlabs.android.dbflow.sql.language.Method.count;
import static com.raizlabs.android.dbflow.sql.language.Method.sum;

/**
 * @author liziqiang@ftrend.cn
 */
public class HandoverHelper {
    // 交易类型：T-销售
    public static final String TRADE_FLAG_SALE = "T";
    // 交易类型：R-退货
    public static final String TRADE_FLAG_RETURN = "R";

    // 交易状态：0-未结
    public static final String TRADE_STATUS_NOTPAY = "0";
    // 交易状态：1-挂起
    public static final String TRADE_STATUS_HANGUP = "1";
    // 交易状态：2-已结
    public static final String TRADE_STATUS_PAID = "2";
    // 交易状态：3-取消
    public static final String TRADE_STATUS_CANCELLED = "3";
    // 操作记录：0-未删除
    public static final String DELFLAG_EXIST = "0";
    // 操作记录：1-已删除
    public static final String DELFLAG_DELETE = "1";
    // 交单状态：1-已完成
    public static final String HANDOVER_STATUS_FINISH = "1";
    // 交单状态：0-未完成
    public static final String HANDOVER_STATUS_UNFINISH = "0";
    // 支付方式：1-现金
    public static final String HANDOVER_PAY_MONEY = "1";
    // 支付方式：2-支付宝
    public static final String HANDOVER_PAY_ALIPAY = "2";
    // 支付方式：3-微信支付
    public static final String HANDOVER_PAY_WECHAT = "3";
    // 支付方式：4-储值卡
    public static final String HANDOVER_PAY_CARD = "4";
    // 交易筛选：All-全部
    public static final String TRADE_ALL = "ALL";


    /**
     * 交班记录
     */
    public static Handover handover = null;
    /**
     * 交班记录(支付方式统计)
     */
    public static HandoverPay handoverPay = null;


    public static void saveHandover(Handover handover) {
        //整理剩余数据
        handover.setHandoverNo(newHandoverNo());
        handover.setLsNoMin(getMinLsNo());
        handover.setLsNoMax(getMaxLsNo());
        handover.setCancelCount(getCountByStatus(TRADE_STATUS_CANCELLED));
        handover.setCancelTotal(getTotalByStatus(TRADE_STATUS_CANCELLED));
        handover.setHangupCount(getCountByStatus(TRADE_STATUS_HANGUP));
        handover.setHangupTotal(getTotalByStatus(TRADE_STATUS_HANGUP));
        handover.setDelCount(getCountByDelFlag(DELFLAG_DELETE));
        handover.setDelTotal(getTotalByDelFlag(DELFLAG_DELETE));
        handover.setHandoverTime(LogUtil.getDateTime());
        handover.insert();

        saveHandoverPay(handover.getHandoverNo());

    }

    private static void saveHandoverPay(String handoverNo) {
        HandoverPay handoverPay = null;
        String[] tradeFlag = {TRADE_FLAG_SALE, TRADE_FLAG_RETURN};
        String[] payType = {HANDOVER_PAY_MONEY, HANDOVER_PAY_ALIPAY, HANDOVER_PAY_WECHAT, HANDOVER_PAY_CARD};
        for (int i = 0; i < payType.length; i++) {
            for (int j = 0; j < tradeFlag.length; j++) {
                handoverPay = new HandoverPay();
                handoverPay.setHandoverNo(handoverNo);
                handoverPay.setTradeFlag(tradeFlag[j]);
                handoverPay.setPayType(payType[i]);
                handoverPay.setSaleTotal(tradeFlag[j].equals(tradeFlag[0]) ? getTotalByPayTypeAndTradeFlag(payType[i], tradeFlag[j]) : 0.00);
                handoverPay.setSaleCount(tradeFlag[j].equals(tradeFlag[0]) ? getCountByPayTypeAndTradeFlag(payType[i], tradeFlag[j]) : 0);
                handoverPay.setRtnTotal(tradeFlag[j].equals(tradeFlag[0]) ? 0.00 : getTotalByPayTypeAndTradeFlag(payType[i], tradeFlag[j]));
                handoverPay.setRtnCount(tradeFlag[j].equals(tradeFlag[0]) ? 0 : getTotalByPayTypeAndTradeFlag(payType[i], tradeFlag[j]));
                handoverPay.insert();
            }
        }
    }

    /**
     * 生成新的交单流水号
     *
     * @return 交单流水号
     */
    private static String newHandoverNo() {
        //初始流水号
        final String DEF_LS_NO = ZgParams.getPosCode() + "00001";

        FlowCursor cursor = SQLite.select(Method.max(Handover_Table.handoverNo)).from(Handover.class).query();
        if (cursor == null) {
            return DEF_LS_NO;
        }
        cursor.moveToNext();
        if (cursor.getCount() == 0 || cursor.isNull(0)) {
            return DEF_LS_NO;
        }

        String handoverNo = cursor.getStringOrDefault(0);
        int max = Integer.valueOf(handoverNo.substring(3));
        int current = max == 99999 ? 1 : max + 1;
        return ZgParams.getPosCode() + String.format("%05d", current);
    }


    /**
     * @return 最大流水单号
     */
    public static String getMaxLsNo() {
        FlowCursor csr = null;
        String lsNo = "";
        csr = SQLite.select(Method.max(Trade_Table.lsNo)).distinct().from(Trade.class).query();
        if (csr.moveToFirst()) {
            do {
                lsNo = csr.getStringOrDefault(0);
            } while (csr.moveToNext());
        }
        return lsNo;
    }

    /**
     * @return 最小流水单号
     */
    public static String getMinLsNo() {
        FlowCursor csr = null;
        String lsNo = "";
        csr = SQLite.select(Method.min(Trade_Table.lsNo)).distinct().from(Trade.class).query();
        if (csr.moveToFirst()) {
            do {
                lsNo = csr.getStringOrDefault(0);
            } while (csr.moveToNext());
        }
        return lsNo;
    }


    /**
     * 获取行清金额
     *
     * @param status 状态
     * @return 次数
     */
    public static long getCountByStatus(String status) {
        long count = 0;
        count = SQLite.select(count()).from(Trade.class).where(Trade_Table.status.eq(status))
                .count();
        return count;
    }

    /**
     * 获取行清金额
     *
     * @param delFlag
     * @return
     */
    public static long getCountByDelFlag(String delFlag) {
        long count = 0;
        count = SQLite.select(count()).from(TradeProd.class).where(TradeProd_Table.delFlag.eq(delFlag))
                .count();
        return count;
    }


    /**
     * 获取行清金额
     *
     * @param delFlag
     * @return
     */
    public static double getTotalByDelFlag(String delFlag) {
        double total = 0.00;
        FlowCursor csr = null;
        csr = SQLite.select(sum(TradeProd_Table.total)).from(TradeProd.class).where(TradeProd_Table.delFlag.eq(delFlag))
                .query();
        if (csr.moveToFirst()) {
            do {
                total = csr.getDoubleOrDefault(0);
            } while (csr.moveToNext());
        }
        csr.close();
        return total;
    }


    /**
     * 根据交易状态获取金额
     *
     * @param status 取消、挂单
     * @return
     */
    public static double getTotalByStatus(String status) {
        double total = 0.00;
        FlowCursor csr = null;
        csr = SQLite.select(sum(Trade_Table.total)).from(Trade.class).where(Trade_Table.status.eq(status)).query();
        if (csr.moveToFirst()) {
            do {
                total = csr.getDoubleOrDefault(0);
            } while (csr.moveToNext());
        }
        csr.close();
        return total;
    }


    /**
     * 交易金额
     * R---退货   T---销售   other----交易总金额
     *
     * @return 金额
     */
    public static double getTotalByTradeFlag(String type) {
        //该专柜下、该收银员的已结流水
        double total = 0.00;
        FlowCursor csr = null;
        switch (type) {
            case TRADE_FLAG_SALE:
            case TRADE_FLAG_RETURN:
                csr = SQLite.select(sum(Trade_Table.total)).from(Trade.class)
                        .where(Trade_Table.tradeFlag.eq(type))
                        .and(Trade_Table.status.eq("2")).query();
                if (csr.moveToFirst()) {
                    do {
                        total = csr.getDoubleOrDefault(0);
                    } while (csr.moveToNext());
                }
                csr.close();
                break;
            default:
                csr = SQLite.select(sum(Trade_Table.total)).from(Trade.class)
                        .where(Trade_Table.status.eq("2"))
                        .query();
                if (csr.moveToFirst()) {
                    do {
                        total = csr.getDoubleOrDefault(0);
                    } while (csr.moveToNext());
                }
                csr.close();
                break;
        }
        return total;
    }

    /**
     * 交易次数
     * R---退货   T---销售   other----交易总次数
     *
     * @return 次数
     */
    public static long getCountByTradeFlag(String type) {
        //该专柜下、该收银员的已结流水
        long count = 0;
        FlowCursor csr = null;
        switch (type) {
            case TRADE_FLAG_SALE:
            case TRADE_FLAG_RETURN:
                count = SQLite.select(count()).from(Trade.class)
                        .where(Trade_Table.tradeFlag.eq(type))
                        .and(Trade_Table.status.eq("2")).count();
                break;
            default:
                long countT = 0, countR = 0;
                countT = SQLite.select(count()).from(Trade.class)
                        .where(Trade_Table.tradeFlag.eq("T"))
                        .and(Trade_Table.status.eq("2")).count();
                countR = SQLite.select(count()).from(Trade.class)
                        .where(Trade_Table.tradeFlag.eq("R"))
                        .and(Trade_Table.status.eq("2")).count();
                count = countT + countR;
                break;
        }
        return count;
    }

    /**
     * 支付方式金额
     *
     * @return 金额
     */
    public static double getTotalByPayType(String type) {
        double total = 0.00;
        FlowCursor csr = null;
        double amount = 0.00, change = 0.00;
        if (type.equals("ALL")) {
            csr = SQLite.select(sum(TradePay_Table.amount)).from(TradePay.class)
                    .query();
            if (csr.moveToFirst()) {
                do {
                    amount = csr.getDoubleOrDefault(0);
                } while (csr.moveToNext());
            }
            csr.close();
            csr = SQLite.select(sum(TradePay_Table.change)).from(TradePay.class)
                    .query();
            if (csr.moveToFirst()) {
                do {
                    change = csr.getDoubleOrDefault(0);
                } while (csr.moveToNext());
            }
            csr.close();
            total = amount - change;
        } else {
            csr = SQLite.select(sum(TradePay_Table.amount)).from(TradePay.class)
                    .where(TradePay_Table.appPayType.eq(type)).query();
            if (csr.moveToFirst()) {
                do {
                    amount = csr.getDoubleOrDefault(0);
                } while (csr.moveToNext());
            }
            csr.close();
            csr = SQLite.select(sum(TradePay_Table.change)).from(TradePay.class)
                    .where(TradePay_Table.appPayType.eq(type)).query();
            if (csr.moveToFirst()) {
                do {
                    change = csr.getDoubleOrDefault(0);
                } while (csr.moveToNext());
            }
            csr.close();
            total = amount - change;
        }
        return total;
    }

    /**
     * 支付方式次数
     *
     * @return 次数
     */
    public static long getCountByPayType(String type) {
        long count = 0;
        FlowCursor csr = null;
        if (type.equals(TRADE_ALL)) {
            count = SQLite.select(count()).from(TradePay.class)
                    .count();
        } else {
            count = SQLite.select(count()).from(TradePay.class).where(TradePay_Table.appPayType.eq(type))
                    .count();
        }
        return count;
    }


    public static double getTotalByPayTypeAndTradeFlag(String appPayType, String tradeFlag) {
        double total = 0.00;
        double amount = 0.00, change = 0.00;
        FlowCursor csr = null;
        switch (tradeFlag) {
            case TRADE_FLAG_RETURN:
                csr = SQLite.select(sum(TradePay_Table.amount), sum(TradePay_Table.change)).from(TradePay.class)
                        .where(TradePay_Table.amount.lessThanOrEq(0.0))
                        .and(TradePay_Table.appPayType.eq(appPayType))
                        .query();
                if (csr.moveToFirst()) {
                    do {
                        amount = csr.getDoubleOrDefault(0);
                        change = csr.getDoubleOrDefault(1);
                    } while (csr.moveToNext());
                }
                csr.close();
                total = amount - change;
                break;
            case TRADE_FLAG_SALE:
                csr = SQLite.select(sum(TradePay_Table.amount), sum(TradePay_Table.change)).from(TradePay.class)
                        .where(TradePay_Table.amount.lessThanOrEq(0.0))
                        .and(TradePay_Table.appPayType.eq(appPayType))
                        .query();
                if (csr.moveToFirst()) {
                    do {
                        amount = csr.getDoubleOrDefault(0);
                        change = csr.getDoubleOrDefault(1);
                    } while (csr.moveToNext());
                }
                csr.close();
                total = amount - change;
                break;
            default:
                break;
        }
        return total;
    }


    /**
     * @param appPayType 支付方式
     * @param tradeFlag  销售退货
     * @return
     */
    public static long getCountByPayTypeAndTradeFlag(String appPayType, String tradeFlag) {
        long count = 0;
        switch (tradeFlag) {
            case TRADE_FLAG_RETURN://退货
                count = SQLite.select(count()).from(TradePay.class)
                        .where(TradePay_Table.amount.lessThanOrEq(0.0))
                        .and(TradePay_Table.appPayType.eq(appPayType))
                        .count();
                break;
            case TRADE_FLAG_SALE://销售
                count = SQLite.select(count()).from(TradePay.class)
                        .where(TradePay_Table.amount.greaterThanOrEq(0.0))
                        .and(TradePay_Table.appPayType.eq(appPayType))
                        .count();
                break;
            default:
                break;
        }
        return count;
    }


//
//    /**
//     * 通过app内的支付方式查询下发的代码
//     *
//     * @param appPayTypeCode App内的支付方式代码
//     * @return 下发数据内的支付方式
//     */
//    public static String appToPayCode(String appPayTypeCode) {
//        LogUtil.d(appPayTypeCode);
//        if (appPayTypeCode.equals(TRADE_ALL)) {
//            return appPayTypeCode;
//        }
//        LogUtil.d(appPayTypeCode);
//        return SQLite.select(DepPayInfo_Table.payTypeCode).from(DepPayInfo.class)
//                .where(DepPayInfo_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
//                .and(DepPayInfo_Table.appPayType.eq(appPayTypeCode))
//                .querySingle().getPayTypeCode();
//    }


    /**
     * 通过下发的支付方式查询App对应的代码
     *
     * @param payTypeCode 下发的支付方式代码
     * @return app内的支付方式代码
     */
    public static String payToAppCode(String payTypeCode) {
//        return SQLite.select(DepPayInfo_Table.appPayType).from(DepPayInfo.class)
//                .where(DepPayInfo_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
//                .and(DepPayInfo_Table.appPayType.eq(payTypeCode))
//                .querySingle().getPayTypeCode();
        return null;
    }


    public static Handover getHandover() {
        return handover;
    }

    public static HandoverPay getHandoverPay() {
        return handoverPay;
    }
}
