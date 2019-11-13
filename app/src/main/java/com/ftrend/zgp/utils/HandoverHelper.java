package com.ftrend.zgp.utils;

import android.support.annotation.NonNull;

import com.ftrend.zgp.model.AppParams;
import com.ftrend.zgp.model.AppParams_Table;
import com.ftrend.zgp.model.DepPayInfo;
import com.ftrend.zgp.model.DepPayInfo_Table;
import com.ftrend.zgp.model.Handover;
import com.ftrend.zgp.model.HandoverPay;
import com.ftrend.zgp.model.HandoverRecord;
import com.ftrend.zgp.model.HandoverSum;
import com.ftrend.zgp.model.Handover_Table;
import com.ftrend.zgp.model.SqbPayOrder;
import com.ftrend.zgp.model.SqbPayResult;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradePay;
import com.ftrend.zgp.model.TradePay_Table;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.TradeProd_Table;
import com.ftrend.zgp.model.Trade_Table;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.model.User_Table;
import com.ftrend.zgp.utils.db.TransHelper;
import com.ftrend.zgp.utils.http.RestCallback;
import com.ftrend.zgp.utils.http.RestResultHandler;
import com.ftrend.zgp.utils.http.RestSubscribe;
import com.ftrend.zgp.utils.pay.PayType;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.property.Property;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.raizlabs.android.dbflow.sql.language.Method.count;
import static com.raizlabs.android.dbflow.sql.language.Method.min;

/**
 * @author liziqiang@ftrend.cn
 */
public class HandoverHelper {
    // 交班状态：1-已完成
    public static final String HANDOVER_STATUS_FINISH = "1";
    // 交班状态：0-未完成
    public static final String HANDOVER_STATUS_UNFINISH = "0";

    // 强制交班天数
    private static final int MUST_HANDOVER_DAYS = 3;
    // 提示交班天数
    private static final int TIP_HANDOVER_DAYS = 2;

    /**
     * 交班记录
     */
    private static Handover handover = null;
    /**
     * 交班记录(流水统计)
     */
    private static List<HandoverSum> sumList = null;
    /**
     * 交班记录(支付方式统计)
     */
    private static List<HandoverPay> payList = null;

    /**
     * 处理未完成的交班记录（只处理最新的一条），在交班初始化前完成
     */
    public static void dealWithNotFinished(@NonNull OperateCallback callback) {
        // TODO: 2019/10/23 目前交班后流水号从1开始，不适合做以下处理
       /* // 查询上次交班信息
        Handover handover = SQLite.select().from(Handover.class)
                .orderBy(Handover_Table.id, false)
                .limit(1)
                .querySingle();
        if (handover == null) {
            callback.onSuccess(null);
            return;
        }
        if (HANDOVER_STATUS_FINISH.equals(handover.getStatus())) {
            // 删除已交班流水
            deleteLs(handover.getLsNoMin(), handover.getLsNoMax());
            callback.onSuccess(null);
            return;
        }
        // TODO: 2019/10/22 查询后台交班状态
        // 1. 已完成，更新交班状态并删除对应的流水
        // 2. 未完成，设置该交班记录已失败（或者删除？）*/
    }

    /**
     * 交班初始化
     *
     * @return
     */
    public static boolean initHandover() {
        handover = new Handover();
        handover.setHandoverNo(newHandoverNo());
        handover.setHandoverTime(new Date());
        handover.setDepCode(ZgParams.getCurrentDep().getDepCode());
        handover.setLsNoMin(getMinLsNo());
        handover.setLsNoMax(getMaxLsNo());
        handover.setStatus(HANDOVER_STATUS_UNFINISH);

        sumList = new ArrayList<>();
        payList = new ArrayList<>();

        return true;
    }

    /**
     * 查询当前最小流水号
     *
     * @return
     */
    private static String getMinLsNo() {
        Trade trade = SQLite.select().from(Trade.class)
                .orderBy(Trade_Table.lsNo, true)
                .limit(1)
                .querySingle();
        return trade == null ? "" : trade.getLsNo();
    }

    /**
     * 查询当前最大流水号
     *
     * @return
     */
    private static String getMaxLsNo() {
        Trade trade = SQLite.select().from(Trade.class)
                .orderBy(Trade_Table.lsNo, false)
                .limit(1)
                .querySingle();
        return trade == null ? "" : trade.getLsNo();
    }

    /**
     * 汇总交班数据（此方法统计全部已结流水信息。调用此方法前，请确认已交班流水全部删除）
     *
     * @return
     */
    public static boolean handoverSum(boolean isReport) {
        List<String> userList;
        if (isReport) {
            userList = new ArrayList<>();
            userList.add(ZgParams.getCurrentUser().getUserCode());
        } else {
            userList = getUserList();
        }
        sumList.clear();
        payList.clear();
        for (String userCode : userList) {
            String userName = getUserName(userCode);
            // 按用户汇总交易流水
            HandoverSum sum = new HandoverSum();
            sum.setHandoverNo(handover.getHandoverNo());
            // 收款员
            sum.setCashier(userCode);
            sum.setCashierName(userName);
            // 按交易类型统计
            sumByTradeFlag(sum);
            // 按交易状态统计
            sumByTradeStatus(sum);
            // 行清统计
            sumByProdStatus(sum);
            //
            sumList.add(sum);

            // 按用户总支付流水
            List<HandoverPay> userPayList = new ArrayList<>();
            sumPayTypeList(userPayList, userCode);
            for (HandoverPay pay : userPayList) {
                // 补充相关信息
                pay.setHandoverNo(handover.getHandoverNo());
                // 收款员
                pay.setCashier(userCode);
                pay.setCashierName(userName);
            }
            payList.addAll(userPayList);
        }

        return true;
    }

    /**
     * 按交易类型统计
     *
     * @param sum 统计结果输出
     */
    private static void sumByTradeFlag(HandoverSum sum) {
        Property colSaleCount = Method.sum(
                SQLite.caseWhen(Trade_Table.tradeFlag.eq(TradeHelper.TRADE_FLAG_SALE))
                        .then(1)._else(0).end())
                .as("saleCount");
        Property colSaleTotal = Method.sum(
                SQLite.caseWhen(Trade_Table.tradeFlag.eq(TradeHelper.TRADE_FLAG_SALE))
                        .then(Trade_Table.total)._else(0).end())
                .as("saleTotal");
        Property colRtnCount = Method.sum(
                SQLite.caseWhen(Trade_Table.tradeFlag.eq(TradeHelper.TRADE_FLAG_REFUND))
                        .then(1)._else(0).end())
                .as("rtnCount");
        Property colRtnTotal = Method.sum(
                SQLite.caseWhen(Trade_Table.tradeFlag.eq(TradeHelper.TRADE_FLAG_REFUND))
                        .then(Trade_Table.total)._else(0).end())
                .as("rtnTotal");
        FlowCursor csr = SQLite.select(colSaleCount, colSaleTotal, colRtnCount, colRtnTotal)
                .from(Trade.class)
                .where(Trade_Table.status.eq(TradeHelper.TRADE_STATUS_PAID))
                .and(Trade_Table.cashier.eq(sum.getCashier()))
                .groupBy(Trade_Table.cashier)
                .query();
        if (csr != null) {
            if (csr.moveToNext()) {
                // 销售笔数
                sum.setSaleCount(csr.getDoubleOrDefault("saleCount"));
                // 销售金额
                sum.setSaleTotal(csr.getDoubleOrDefault("saleTotal"));
                // 退货笔数
                sum.setRtnCount(csr.getDoubleOrDefault("rtnCount"));
                // 退货金额
                sum.setRtnTotal(csr.getDoubleOrDefault("rtnTotal"));
            }
            csr.close();
        }
    }

    /**
     * 按交易状态统计
     *
     * @param sum 统计结果输出
     */
    private static void sumByTradeStatus(HandoverSum sum) {
        Property colHangupCount = Method.sum(
                SQLite.caseWhen(Trade_Table.status.eq(TradeHelper.TRADE_STATUS_HANGUP))
                        .then(1)._else(0).end())
                .as("hangupCount");
        Property colHangupTotal = Method.sum(
                SQLite.caseWhen(Trade_Table.status.eq(TradeHelper.TRADE_STATUS_HANGUP))
                        .then(Trade_Table.total)._else(0).end())
                .as("hangupTotal");
        Property colCancelCount = Method.sum(
                SQLite.caseWhen(Trade_Table.status.eq(TradeHelper.TRADE_STATUS_CANCELLED))
                        .then(1)._else(0).end())
                .as("cancelCount");
        Property colCancelTotal = Method.sum(
                SQLite.caseWhen(Trade_Table.status.eq(TradeHelper.TRADE_STATUS_CANCELLED))
                        .then(Trade_Table.total)._else(0).end())
                .as("cancelTotal");
        FlowCursor csr = SQLite.select(colHangupCount, colHangupTotal, colCancelCount, colCancelTotal)
                .from(Trade.class)
                .where(Trade_Table.cashier.eq(sum.getCashier()))
                .groupBy(Trade_Table.cashier)
                .query();
        if (csr != null) {
            if (csr.moveToNext()) {
                // 挂单笔数
                sum.setHangupCount(csr.getDoubleOrDefault("hangupCount"));
                // 挂单金额
                sum.setHangupTotal(csr.getDoubleOrDefault("hangupTotal"));
                // 取消笔数
                sum.setCancelCount(csr.getDoubleOrDefault("cancelCount"));
                // 取消金额
                sum.setCancelTotal(csr.getDoubleOrDefault("cancelTotal"));
            }
            csr.close();
        }
    }

    /**
     * 按商品状态统计（行清）
     *
     * @param sum 统计结果输出
     */
    private static void sumByProdStatus(HandoverSum sum) {
        Property colDeleteCount = Method.count(TradeProd_Table.id.withTable()).as("deleteCount");
        Property colDeleteTotal = Method.sum(TradeProd_Table.total.withTable()).as("deleteTotal");
        FlowCursor csr = SQLite.select(colDeleteCount, colDeleteTotal)
                .from(Trade.class)
                .join(TradeProd.class, Join.JoinType.INNER)
                .on(TradeProd_Table.lsNo.withTable().eq(Trade_Table.lsNo.withTable()),
                        Trade_Table.cashier.withTable().eq(sum.getCashier()),
                        Trade_Table.status.withTable().eq(TradeHelper.TRADE_STATUS_PAID),
                        TradeProd_Table.delFlag.withTable().eq(TradeHelper.DELFLAG_YES))
                .query();
        if (csr != null) {
            if (csr.moveToNext()) {
                // 行清次数
                sum.setDelCount(csr.getDoubleOrDefault("deleteCount"));
                // 行清金额
                sum.setDelTotal(csr.getDoubleOrDefault("deleteTotal"));
            }
            csr.close();
        }
    }

    /**
     * 按支付方式统计
     *
     * @param userCode
     * @return
     */
    private static void sumPayTypeList(List<HandoverPay> payList, final String userCode) {
        Property colSaleCount = Method.sum(
                SQLite.caseWhen(Trade_Table.tradeFlag.eq(TradeHelper.TRADE_FLAG_SALE))
                        .then(1)._else(0).end())
                .as("saleCount");
        Property colSaleTotal = Method.sum(
                SQLite.caseWhen(Trade_Table.tradeFlag.eq(TradeHelper.TRADE_FLAG_SALE))
                        .then(TradePay_Table.amount.minus(TradePay_Table.change))._else(0).end())
                .as("saleTotal");
        Property colRtnCount = Method.sum(
                SQLite.caseWhen(Trade_Table.tradeFlag.eq(TradeHelper.TRADE_FLAG_REFUND))
                        .then(1)._else(0).end())
                .as("rtnCount");
        Property colRtnTotal = Method.sum(
                SQLite.caseWhen(Trade_Table.tradeFlag.eq(TradeHelper.TRADE_FLAG_REFUND))
                        .then(TradePay_Table.amount.minus(TradePay_Table.change))._else(0).end())
                .as("rtnTotal");
        FlowCursor csr = SQLite.select(TradePay_Table.payTypeCode.withTable(),
                DepPayInfo_Table.payTypeName.withTable(),
                colSaleCount, colSaleTotal, colRtnCount, colRtnTotal)
                .from(TradePay.class)
                .join(Trade.class, Join.JoinType.INNER)
                .on(TradePay_Table.lsNo.withTable().eq(Trade_Table.lsNo.withTable()),
                        Trade_Table.cashier.withTable().eq(userCode))
                .join(DepPayInfo.class, Join.JoinType.INNER)
                //payTypeCode可能重复出现，这里要用appPayType关联
                .on(Trade_Table.depCode.withTable().eq(DepPayInfo_Table.depCode.withTable()),
                        TradePay_Table.appPayType.withTable().eq(DepPayInfo_Table.appPayType.withTable()))
                .groupBy(TradePay_Table.payTypeCode.withTable())
                .query();
        if (csr != null) {
            while (csr.moveToNext()) {
                HandoverPay pay = new HandoverPay();
                //这里取字段名，如果使用TradePay_Table.payTypeCode.getCursorKey()，得到的名称为`payTypeCode`，可能是bug
                pay.setPayType(csr.getStringOrDefault(TradePay_Table.payTypeCode.getNameAlias().nameRaw()));
                pay.setPayTypeName(csr.getStringOrDefault(DepPayInfo_Table.payTypeName.getNameAlias().nameRaw()));
                pay.setSaleCount(csr.getDoubleOrDefault("saleCount"));
                pay.setSaleTotal(csr.getDoubleOrDefault("saleTotal"));
                pay.setRtnCount(csr.getDoubleOrDefault("rtnCount"));
                pay.setRtnTotal(csr.getDoubleOrDefault("rtnTotal"));
                payList.add(pay);
            }
            csr.close();
        }
    }

    /**
     * 删除指定范围的交易流水信息
     *
     * @param from 起始流水号
     * @param to   结束流水号
     */
    public static void deleteLs(final String from, final String to) {
        TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                SQLite.delete(Trade.class)
                        .where(Trade_Table.lsNo.greaterThanOrEq(from))
                        .and(Trade_Table.lsNo.lessThanOrEq(to))
                        .execute(databaseWrapper);
                SQLite.delete(TradeProd.class)
                        .where(TradeProd_Table.lsNo.greaterThanOrEq(from))
                        .and(TradeProd_Table.lsNo.lessThanOrEq(to))
                        .execute(databaseWrapper);
                SQLite.delete(TradePay.class)
                        .where(TradePay_Table.lsNo.greaterThanOrEq(from))
                        .and(TradePay_Table.lsNo.lessThanOrEq(to))
                        .execute(databaseWrapper);
                return true;
            }
        });
    }

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
     * 当前是否必须交班（收银、取单、退货等功能进入前调用此方法）
     *
     * @return 0 - 必须交班，-1 - 不提示交班，大于0 - 提示已经这么多天没有交班了
     */
    public static int mustHandover() {
        int result;
        FlowCursor cursor = SQLite.select(min(Trade_Table.tradeTime))
                .from(Trade.class)
                .where(Trade_Table.status.eq(TradeHelper.TRADE_STATUS_PAID))
                .query();
        if (cursor == null || cursor.getCount() == 0) {
            result = -1;
        } else {
            cursor.moveToNext();
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date minDate = format.parse(cursor.getString(0));
                long days = (System.currentTimeMillis() - minDate.getTime()) / (1000 * 60 * 60 * 24);
                if (days < TIP_HANDOVER_DAYS) {
                    result = -1;
                } else if (days >= MUST_HANDOVER_DAYS) {
                    return 0;
                } else {
                    return (int) days;
                }
            } catch (Exception e) {
                result = -1;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return result;
    }

    /**
     * 保存交班信息（调用后台接口前，先保存交班记录）
     *
     * @return
     */
    public static boolean save() {
        final boolean[] result = {false};
        TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                if (!handover.save(databaseWrapper)) {
                    return false;
                }
                for (HandoverSum sum : sumList) {
                    if (!sum.save(databaseWrapper)) {
                        return false;
                    }
                }
                for (HandoverPay pay : payList) {
                    if (!pay.save(databaseWrapper)) {
                        return false;
                    }
                }
                result[0] = true;
                return true;
            }
        });
        return result[0];
    }

    /**
     * 交班完成（更新交班记录状态，清空流水表）
     */
    public static void finish() {
        TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                handover.setStatus(HANDOVER_STATUS_FINISH);
                if (!handover.save(databaseWrapper)) {
                    return false;
                }
                SQLite.delete(Trade.class).execute(databaseWrapper);
                SQLite.delete(TradeProd.class).execute(databaseWrapper);
                SQLite.delete(TradePay.class).execute(databaseWrapper);
                //同步删除收钱吧交易记录（已经和流水同步上传）
                SQLite.delete(SqbPayOrder.class).execute(databaseWrapper);
                SQLite.delete(SqbPayResult.class).execute(databaseWrapper);
                return true;
            }
        });
    }

    /**
     * 上传APP配置参数，失败不影响交班结果（只上传必要的参数）
     */
    public static void uploadAppParams() {
        List<AppParams> appParamsList = SQLite.select().from(AppParams.class)
                .where(AppParams_Table.paramName.in("printerConfig", "lastDep", "lastUser", "lastLsNo"))
                .queryList();
        RestSubscribe.getInstance().uploadAppParams(ZgParams.getPosCode(), appParamsList,
                new RestCallback(new RestResultHandler() {
                    @Override
                    public void onSuccess(Map<String, Object> body) {
                        // 无需处理上传结果
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMsg) {
                        // 无需处理上传结果
                    }
                }));
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
    @NonNull
    public static List<String> getUserList() {
        List<String> userCode = new ArrayList<>();
        List<Trade> temp = SQLite.select(Trade_Table.cashier).distinct().from(Trade.class)
                .where(Trade_Table.status.eq(TradeHelper.TRADE_STATUS_PAID))
                .queryList();
        for (Trade t : temp) {
            userCode.add(t.getCashier());
        }
        return userCode;
    }

    /**
     * 根据userCode获得用户名
     *
     * @param userCode 用户账号
     * @return 用户名
     */
    public static String getUserName(String userCode) {
        String userName = "";
        FlowCursor csr = SQLite.select(User_Table.userName).from(User.class)
                .where(User_Table.userCode.eq(userCode)).query();
        if (csr != null) {
            if (csr.moveToNext()) {
                userName = csr.getStringOrDefault(0);
            }
            csr.close();
        }
        return userName;
    }

    /**
     * 获取指定收款员的支付汇总
     *
     * @param userCode   收款员编号
     * @param payCash    现金汇总
     * @param payPrepaid 储值卡汇总
     * @param paySqb     收钱吧汇总
     */
    public static void getUserPayInfo(String userCode,
                                      HandoverPay payCash,
                                      HandoverPay payPrepaid,
                                      HandoverPay paySqb) {
        payCash.init();
        payPrepaid.init();
        paySqb.init();
        for (HandoverPay pay : payList) {
            if (!userCode.equals(pay.getCashier())) {
                continue;
            }
            if (PayType.PAYTYPE_CASH.equals(pay.getPayType())) {
                payCash.add(pay);
            } else if (PayType.PAYTYPE_PREPAID.equals(pay.getPayType())) {
                payPrepaid.add(pay);
            } else {
                paySqb.add(pay);
            }
        }
    }

    /**
     * 获取显示用的交班信息列表
     *
     * @return
     */
    public static List<HandoverRecord> getRecordList() {
        List<HandoverRecord> recordList = new ArrayList<>();
        for (HandoverSum sum : sumList) {
            HandoverRecord handoverRecord = new HandoverRecord();
            handoverRecord.setCashier(sum.getCashier());
            handoverRecord.setCashierName(sum.getCashierName());
            handoverRecord.setDepCode(ZgParams.getCurrentDep().getDepCode());
            // 交易汇总
            handoverRecord.setSaleTotal(sum.getSaleTotal());
            handoverRecord.setSaleCount(sum.getSaleCount());
            handoverRecord.setRtnCount(sum.getRtnCount());
            handoverRecord.setRtnTotal(sum.getRtnTotal());
            // 支付汇总
            HandoverPay payCash = new HandoverPay();
            HandoverPay payPrepaid = new HandoverPay();
            HandoverPay paySqb = new HandoverPay();
            getUserPayInfo(sum.getCashier(), payCash, payPrepaid, paySqb);
            handoverRecord.setMoneyTotal(payCash.getTotal());
            handoverRecord.setMoneyCount(payCash.getCount());
            handoverRecord.setSqbTotal(paySqb.getTotal());
            handoverRecord.setSqbCount(paySqb.getCount());
            handoverRecord.setCardTotal(payPrepaid.getTotal());
            handoverRecord.setCardCount(payPrepaid.getCount());
            handoverRecord.setAliPayTotal(0);
            handoverRecord.setAliPayCount(0);
            handoverRecord.setWechatTotal(0);
            handoverRecord.setWechatCount(0);

            recordList.add(handoverRecord);
        }

        return recordList;
    }
}
