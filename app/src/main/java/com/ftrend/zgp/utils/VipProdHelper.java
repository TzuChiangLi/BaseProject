package com.ftrend.zgp.utils;

import android.text.TextUtils;

import com.ftrend.zgp.model.Product;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradePay;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.TradeUploadQueue;
import com.ftrend.zgp.utils.db.TransHelper;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.pay.PayType;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ftrend.zgp.utils.TradeHelper.TRADE_CUST_VIP;
import static com.ftrend.zgp.utils.TradeHelper.TRADE_FLAG_SALE;
import static com.ftrend.zgp.utils.TradeHelper.TRADE_STATUS_NOTPAY;
import static com.ftrend.zgp.utils.TradeHelper.TRADE_STATUS_PAID;
import static com.ftrend.zgp.utils.TradeHelper.newLsNo;

/**
 * @author liziqiang@ftrend.cn
 */

public class VipProdHelper {
    private static final String TAG = "VipProdHelper";
    private static Trade trade = null;
    private static TradePay pay = null;
    private static List<TradeProd> prodList = null;

    /**
     * 初始化流水
     */
    public static void initSale() {
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
        trade.setCreateTime(new Date());
        trade.setCreateIp(ZgParams.getCurrentIp());
        trade.setStatus(TRADE_STATUS_NOTPAY);

        prodList = new ArrayList<>();
        pay = null;

    }

    /**
     * @param product 商品
     */
    public static void initProdList(Product product) {
        long size = prodList.size();
        if (size != 0) {
            prodList.clear();
        }
        TradeProd prod = new TradeProd();
        prod.setLsNo(trade.getLsNo());
        prod.setSortNo(size + 1);
        prod.setProdCode(product.getProdCode());
        prod.setProdName(product.getProdName());
        prod.setBarCode(product.getBarCode());
        prod.setDepCode(product.getDepCode());
        prod.setProdForDsc(product.getForDsc());
        prod.setProdPriceFlag(product.getPriceFlag());
        prod.setProdIsLargess(product.getIsLargess());
        prod.setProdMinPrice(product.getMinimumPrice());
        prod.setAmount(1);
        prod.setSingleDsc(0);
        prod.setWholeDsc(0);
        prod.setVipDsc(0);
        prod.setTranDsc(0);
        prod.setVipDsc(0);
        prod.setSaleInfo("");
        prod.setDelFlag("0");
        prod.setPrice(product.getPrice());
        prodList.add(prod);
        //刷新总价
        trade.setTotal(product.getPrice());
    }

    /**
     * 完成支付
     *
     * @param appPayType APP支付类型
     * @param payCode    支付账号
     * @return 支付结果
     */
    public static boolean pay(final String appPayType, final String payCode, final double balance) {
        return TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                return doPay(databaseWrapper, appPayType, payCode, balance);
            }
        });
    }

    private static boolean doPay(DatabaseWrapper databaseWrapper,
                                 String appPayType, String payCode, double balance) {
        try {
            String payTypeCode = PayType.appPayTypeToPayType(appPayType);
            if (pay == null) {
                pay = new TradePay();
                pay.setLsNo(trade.getLsNo());
            }
            pay.setPayTypeCode(payTypeCode);
            pay.setAppPayType(appPayType);
            pay.setAmount(trade.getTotal());
            pay.setChange(0);
            pay.setPayCode(payCode);
            pay.setPayTime(new Date());
            pay.setBalance(balance);
            if (!pay.save(databaseWrapper)) {
                return false;
            }
            trade.setTradeTime(pay.getPayTime());
            trade.setCashier(ZgParams.getCurrentUser().getUserCode());
            trade.setStatus(TRADE_STATUS_PAID);
            //储值卡或IC卡支付，记录卡号，用于生成会员台账和计算积分
            if (payTypeCode.equalsIgnoreCase(PayType.PAYTYPE_ICCARD)
                    || payTypeCode.equalsIgnoreCase(PayType.PAYTYPE_PREPAID)) {
                if (TextUtils.isEmpty(trade.getVipCode())) {//已存在会员信息时，以先刷的会员卡进行积分
                    trade.setCustType(TRADE_CUST_VIP);
                    trade.setCardCode(payCode);
                }
            }

            if (trade.save(databaseWrapper)) {
                for (TradeProd prod : prodList) {
                    prod.save(databaseWrapper);
                }
            } else {
                return false;
            }
            //添加到上传队列
            TradeUploadQueue queue = new TradeUploadQueue(trade.getDepCode(), trade.getLsNo());
            return queue.insert(databaseWrapper) > 0;
        } catch (Exception e) {
            LogUtil.u(TAG, "会员卡消费支付", "支付异常: " + pay.getLsNo() + " - " + appPayType + "\r\n" + e.getMessage());
            return false;
        }
    }

    public static Trade getTrade() {
        return trade;
    }

    public static TradePay getPay() {
        return pay;
    }

    public static List<TradeProd> getProdList() {
        return prodList;
    }
}
