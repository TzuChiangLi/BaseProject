package com.ftrend.zgp.utils;

import android.support.annotation.NonNull;

import com.ftrend.zgp.model.Handover;
import com.ftrend.zgp.model.HandoverPay;
import com.ftrend.zgp.model.Handover_Table;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradePay;
import com.ftrend.zgp.model.TradePay_Table;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.TradeProd_Table;
import com.ftrend.zgp.model.Trade_Table;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.model.User_Table;
import com.ftrend.zgp.utils.db.ZgpDb;
import com.ftrend.zgp.utils.log.LogUtil;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 当前是否可以交班
     *
     * @return 1 - 可以交班，0 - 没有可交班的流水， -1 - 单机状态不可交班
     */
    public static int canHandover() {
        if (ZgParams.isIsOnline()) {
            long tradeCount = SQLite.select(count()).from(Trade.class)
                    .where(Trade_Table.status.eq(TradeHelper.TRADE_STATUS_PAID)).count();
            if (tradeCount > 0) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }

    /**
     * 保存到交班数据库中
     *
     * @param handover
     */
    public static void saveHandover(Handover handover) {
        //整理剩余数据
        handover.setHandoverNo(newHandoverNo());
        handover.setLsNoMin(getMinLsNo(handover.getCashier()));
        handover.setLsNoMax(getMaxLsNo(handover.getCashier()));
        handover.setCancelCount(getCountByStatus(TradeHelper.TRADE_STATUS_CANCELLED));
        handover.setCancelTotal(getTotalByStatus(TradeHelper.TRADE_STATUS_CANCELLED));
        handover.setHangupCount(getCountByStatus(TradeHelper.TRADE_STATUS_HANGUP));
        handover.setHangupTotal(getTotalByStatus(TradeHelper.TRADE_STATUS_HANGUP));
        handover.setDelCount(getCountByDelFlag(DELFLAG_DELETE));
        handover.setDelTotal(getTotalByDelFlag(DELFLAG_DELETE));
        handover.setHandoverTime(LogUtil.getDateTime());
        handover.insert();

        saveHandoverPay(handover.getHandoverNo());

    }

    /**
     * 保存支付统计记录
     *
     * @param handoverNo
     */
    private static void saveHandoverPay(final String handoverNo) {
        Transaction transaction = FlowManager.getDatabase(ZgpDb.class).beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                HandoverPay handoverPay;
                String[] tradeFlag = {TRADE_FLAG_SALE, TRADE_FLAG_RETURN};
                String[] payType = {HANDOVER_PAY_MONEY, HANDOVER_PAY_ALIPAY, HANDOVER_PAY_WECHAT, HANDOVER_PAY_CARD};
                for (String s : payType) {
                    for (String s1 : tradeFlag) {
                        handoverPay = new HandoverPay();
                        handoverPay.setHandoverNo(handoverNo);
                        handoverPay.setTradeFlag(s1);
                        handoverPay.setPayType(s);
                        handoverPay.setSaleTotal(s1.equals(tradeFlag[0]) ? getTotalByPayTypeAndTradeFlag(s, s1) : 0.00);
                        handoverPay.setSaleCount(s1.equals(tradeFlag[0]) ? getCountByPayTypeAndTradeFlag(s, s1) : 0);
                        handoverPay.setRtnTotal(s1.equals(tradeFlag[0]) ? 0.00 : getTotalByPayTypeAndTradeFlag(s, s1));
                        handoverPay.setRtnCount(s1.equals(tradeFlag[0]) ? 0 : getTotalByPayTypeAndTradeFlag(s, s1));
                        handoverPay.insert();
                    }
                }
            }
        }).success(new Transaction.Success() {
            @Override
            public void onSuccess(@NonNull Transaction transaction) {
            }
        }).error(new Transaction.Error() {
            @Override
            public void onError(@NonNull Transaction transaction, @NonNull Throwable error) {
            }
        }).build();
        transaction.execute();
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
     * 获取流水表内的用户名
     *
     * @return 交易表的用户名
     */
    public static List<String> getUserCode() {
        List<Trade> temp = SQLite.select(Trade_Table.cashier).distinct().from(Trade.class).queryList();
        if (temp != null) {
            List<String> userCode = new ArrayList<>();
            for (Trade t : temp) {
                userCode.add(t.getCashier());
            }
            return userCode;
        }
        return null;
    }

    /**
     * 获取流水表内的用户名
     *
     * @return 交易表的用户名
     */
    public static List<String> getLsNoByUserCode(String userCode) {
        List<Trade> temp = SQLite.select(Trade_Table.lsNo).distinct().from(Trade.class)
                .where(Trade_Table.cashier.eq(userCode)).queryList();
        if (temp != null) {
            List<String> lsNo = new ArrayList<>();
            for (Trade t : temp) {
                lsNo.add(t.getLsNo());
            }
            return lsNo;
        }
        return null;
    }

    /**
     * @param userCode 用户账号
     * @return 最大流水单号
     */
    public static String getMaxLsNo(String userCode) {
        FlowCursor csr;
        String lsNo = "";
        csr = SQLite.select(Method.max(Trade_Table.lsNo)).distinct().from(Trade.class)
                .where(Trade_Table.cashier.eq(userCode))
                .query();
        if (csr.moveToFirst()) {
            do {
                lsNo = csr.getStringOrDefault(0);
            } while (csr.moveToNext());
        }
        LogUtil.d("----maxLsNo:" + lsNo);
        return lsNo;
    }

    /**
     * @param userCode 用户账号
     * @return 最小流水单号
     */
    public static String getMinLsNo(String userCode) {
        FlowCursor csr;
        String lsNo = "";
        csr = SQLite.select(Method.min(Trade_Table.lsNo)).distinct().from(Trade.class)
                .where(Trade_Table.cashier.eq(userCode))
                .query();
        if (csr.moveToFirst()) {
            do {
                lsNo = csr.getStringOrDefault(0);
            } while (csr.moveToNext());
        }
        LogUtil.d("----minLsNo:" + lsNo);
        return lsNo;
    }


    /**
     * 获取行清金额
     *
     * @param status 状态
     * @return 次数
     */
    public static long getCountByStatus(String status) {
        long count;
        count = SQLite.select(count()).from(Trade.class).where(Trade_Table.status.eq(status))
                .count();
        return count;
    }

    /**
     * 获取行清次数
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
    public static double getTotalByTradeFlag(String type, String userCode) {
        //该专柜下、该收银员的已结流水
        double total = 0.00;
        FlowCursor csr = null;
        switch (type) {
            case TRADE_FLAG_SALE:
            case TRADE_FLAG_RETURN:
                csr = SQLite.select(sum(Trade_Table.total)).from(Trade.class)
                        .where(Trade_Table.tradeFlag.eq(type))
                        .and(Trade_Table.cashier.eq(userCode))
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
                        .and(Trade_Table.cashier.eq(userCode))
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
    public static long getCountByTradeFlag(String type, String userCode) {
        //该专柜下、该收银员的已结流水
        long count = 0;
        FlowCursor csr = null;
        switch (type) {
            case TRADE_FLAG_SALE:
            case TRADE_FLAG_RETURN:
                count = SQLite.select(count()).from(Trade.class)
                        .where(Trade_Table.tradeFlag.eq(type))
                        .and(Trade_Table.cashier.eq(userCode))
                        .and(Trade_Table.status.eq("2")).count();
                break;
            default:
                long countT, countR;
                countT = SQLite.select(count()).from(Trade.class)
                        .where(Trade_Table.tradeFlag.eq("T"))
                        .and(Trade_Table.cashier.eq(userCode))
                        .and(Trade_Table.status.eq("2")).count();
                countR = SQLite.select(count()).from(Trade.class)
                        .where(Trade_Table.tradeFlag.eq("R"))
                        .and(Trade_Table.cashier.eq(userCode))
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
        double total;
        FlowCursor csr;
        double amount = 0.00, change = 0.00;
        if (type.equals(TRADE_ALL)) {
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

    public static double getTotalByPayType(String type, List<String> lsNoList) {
        double total;
        FlowCursor csr;
        double amount = 0.00, change = 0.00;
        if (type.equals(TRADE_ALL)) {
            for (int i = 0; i < lsNoList.size(); i++) {
                csr = SQLite.select(sum(TradePay_Table.amount)).from(TradePay.class)
                        .where(TradeProd_Table.lsNo.eq(lsNoList.get(i)))
                        .query();
                if (csr.moveToFirst()) {
                    do {
                        amount += csr.getDoubleOrDefault(0);
                    } while (csr.moveToNext());
                }
                csr.close();
                csr = SQLite.select(sum(TradePay_Table.change)).from(TradePay.class)
                        .where(TradeProd_Table.lsNo.eq(lsNoList.get(i)))
                        .query();
                if (csr.moveToFirst()) {
                    do {
                        change += csr.getDoubleOrDefault(0);
                    } while (csr.moveToNext());
                }
                csr.close();
            }
            total = amount - change;
        } else {
            for (int i = 0; i < lsNoList.size(); i++) {
                csr = SQLite.select(sum(TradePay_Table.amount)).from(TradePay.class)
                        .where(TradePay_Table.appPayType.eq(type))
                        .and(TradeProd_Table.lsNo.eq(lsNoList.get(i)))
                        .query();
                if (csr.moveToFirst()) {
                    do {
                        amount += csr.getDoubleOrDefault(0);
                    } while (csr.moveToNext());
                }
                csr.close();
                csr = SQLite.select(sum(TradePay_Table.change)).from(TradePay.class)
                        .where(TradePay_Table.appPayType.eq(type))
                        .and(TradeProd_Table.lsNo.eq(lsNoList.get(i)))
                        .query();
                if (csr.moveToFirst()) {
                    do {
                        change += csr.getDoubleOrDefault(0);
                    } while (csr.moveToNext());
                }
                csr.close();
            }

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
        long count;
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

    public static long getCountByPayType(String type, List<String> lsNoList) {
        long count = 0;
        for (int i = 0; i < lsNoList.size(); i++) {
            if (type.equals(TRADE_ALL)) {
                count += SQLite.select(count()).from(TradePay.class)
                        .where(TradePay_Table.lsNo.eq(lsNoList.get(i)))
                        .count();
            } else {
                count += SQLite.select(count()).from(TradePay.class).where(TradePay_Table.appPayType.eq(type))
                        .and(TradePay_Table.lsNo.eq(lsNoList.get(i)))
                        .count();
            }
        }
        return count;
    }


    /**
     * @param appPayType
     * @param tradeFlag
     * @return
     */
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


    /**
     * 根据userCode获得用户名
     *
     * @param userCode 用户账号
     * @return 用户名
     */
    public static String convertUserCodeToUserName(String userCode) {
        String userName = "";
        FlowCursor csr = SQLite.select(User_Table.userName).from(User.class)
                .where(User_Table.userCode.eq(userCode)).query();
        if (csr.moveToFirst()) {
            do {
                userName = csr.getStringOrDefault(0);
            } while (csr.moveToNext());
        }
        return userName;
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
