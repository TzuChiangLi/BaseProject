package com.ftrend.zgp.utils;

import android.util.Log;

import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradePay;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.TradeProd_Table;
import com.ftrend.zgp.model.Trade_Table;
import com.ftrend.zgp.utils.log.LogUtil;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 交易操作类
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/6
 */
public class TradeHelper {
    private static String TAG = "TradeHelper";

    // 交易类型：T-销售
    private static final String TRADE_FLAG_SALE = "T";
    // 交易类型：R-退货
    private static final String TRADE_FLAG_RETURN = "R";

    // 交易状态：0-未结
    private static final String TRADE_STATUS_NOTPAY = "0";
    // 交易状态：1-挂起
    private static final String TRADE_STATUS_HANGUP = "1";
    // 交易状态：2-已结
    private static final String TRADE_STATUS_PAID = "2";
    // 交易状态：3-取消
    private static final String TRADE_STATUS_CANCELLED = "3";
    // 交易流水
    private static Trade trade = null;
    // 商品列表
    private static List<TradeProd> prodList = null;
    // 支付信息
    private static TradePay pay = null;

    /**
     * 初始化当前操作的交易流水，读取未结销售流水，不存在则创建新的流水
     */
    public static void initSale() {
        trade = SQLite.select().from(Trade.class)
                .where(Trade_Table.status.eq(TRADE_STATUS_NOTPAY))
                .and(Trade_Table.tradeFlag.eq(TRADE_FLAG_SALE))
                .and(Trade_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                .querySingle();
        if (trade == null) {
            trade = new Trade();
            trade.setDepCode(ZgParams.getCurrentDep().getDepCode());
            trade.setLsNo(newLsNo());
            trade.setTradeTime(null);
            trade.setTradeFlag(TRADE_FLAG_SALE);
            trade.setCashier(ZgParams.getCurrentUser().getUserCode());
            trade.setDscTotal(0);
            trade.setTotal(0);
            trade.setCustType("0");
            trade.setVipCode("");
            trade.setCardCode("");
            trade.setVipTotal(0);
            trade.setCreateTime((String.valueOf(LogUtil.getDateTime())));
            trade.setCreateIp(ZgParams.getCurrentIp());
            trade.setStatus(TRADE_STATUS_NOTPAY);

            prodList = new ArrayList<>();
        } else {
            prodList = SQLite.select().from(TradeProd.class)
                    .where(TradeProd_Table.lsNo.eq(trade.getLsNo()))
                    .queryList();
        }
    }

    /**
     * 添加商品到商品列表
     *
     * @param product 商品信息
     * @return
     */
    public static long addProduct(DepProduct product) {
        long index = prodList.size();
        TradeProd prod = new TradeProd();
        prod.setLsNo(trade.getLsNo());
        prod.setSortNo(index + 1);//商品序号从1开始
        prod.setProdCode(product.getProdCode());
        prod.setProdName(product.getProdName());
        prod.setBarCode(product.getBarCode());
        prod.setDepCode(product.getDepCode());
        prod.setPrice(product.getPrice());
        prod.setAmount(1);
        prod.setManuDsc(0);
        prod.setVipDsc(0);
        prod.setTranDsc(0);
        prod.setTotal(prod.getPrice() * prod.getAmount());
        prod.setVipDsc(0);
        prod.setSaleInfo("");
        prod.setDelFlag("0");
        //保存商品记录并重新汇总流水金额（此时会保存交易流水）
        // TODO: 2019/9/7 启用事务，避免数据异常
        if (prod.insert() > 0 && recalcTotal()) {
            prodList.add(prod);
            return index;
        } else {
            return -1;
        }
    }

    /**
     * 行清
     *
     * @param index 行清的商品索引
     * @return
     */
    public static boolean delProduct(int index) {
        if (index < 0 || index >= prodList.size()) {
            Log.e(TAG, "行清: 索引无效");
            return false;
        }
        TradeProd prod = prodList.get(index);
        prod.setDelFlag("1");
        return prod.save();
    }

    /**
     * 完成支付
     *
     * @param payTypeCode 支付类型编号
     * @param amount      支付金额
     * @param change      找零金额
     * @param payCode     支付账号
     * @return
     */
    public static boolean pay(String payTypeCode, float amount, float change, String payCode) {
        if (pay == null) {
            pay = new TradePay();
            pay.setLsNo(trade.getLsNo());
        }
        pay.setPayTypeCode(payTypeCode);
        pay.setAmount(amount);
        pay.setChange(change);
        pay.setPayCode(payCode);
        pay.setPayTime(new Date());
        // TODO: 2019/9/7 启用事务，避免数据异常
        if (pay.save()) {
            trade.setTradeTime(pay.getPayTime());
            trade.setCashier(ZgParams.getCurrentUser().getUserCode());
            trade.setStatus(TRADE_STATUS_PAID);
            return trade.save();
        } else {
            return false;
        }
    }

    /**
     * 完成支付（仅适用于现金支付）
     *
     * @param payTypeCode 支付类型编号
     * @return
     */
    public static boolean pay(String payTypeCode) {
        return pay(payTypeCode, trade.getTotal(), 0, "(无)");
    }

    /**
     * 完成支付（适用于微信、支付宝、储值卡等支付方式）
     *
     * @param payTypeCode 支付类型编号
     * @param payCode     支付账号
     * @return
     */
    public static boolean pay(String payTypeCode, String payCode) {
        return pay(payTypeCode, trade.getTotal(), 0, payCode);
    }

    /**
     * 生成新的流水号
     *
     * @return
     */
    private static String newLsNo() {
        //初始流水号
        final String DEF_LS_NO = ZgParams.getPosCode() + "00001";

        FlowCursor cursor = SQLite.select(Method.max(Trade_Table.lsNo)).from(Trade.class).query();
        if (cursor == null) {
            return DEF_LS_NO;
        }
        cursor.moveToNext();
        if (cursor.getCount() == 0 || cursor.isNull(0)) {
            return DEF_LS_NO;
        }

        String lsNo = cursor.getStringOrDefault(0);
        int max = Integer.valueOf(lsNo.substring(3));
        int current = max == 99999 ? 1 : max + 1;
        return ZgParams.getPosCode() + String.format("%05d", current);
    }

    /**
     * 重新汇总流水金额：优惠、合计、积分金额
     */
    private static boolean recalcTotal() {
        float dscTotal = 0;
        float total = 0;
        float vipTotal = 0;

        for (TradeProd prod : prodList) {
            dscTotal += prod.getManuDsc() + prod.getTranDsc() + prod.getVipDsc();
            total += prod.getTotal();
            vipTotal += prod.getVipTotal();
        }
        trade.setDscTotal(dscTotal);
        trade.setTotal(total);
        trade.setVipTotal(vipTotal);
        return trade.save();
    }


    public static Trade getTrade() {
        return trade;
    }

}
