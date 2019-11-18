package com.ftrend.zgp.utils;

import android.util.Log;

import com.ftrend.zgp.model.SqbPayOrder;
import com.ftrend.zgp.model.SqbPayOrder_Table;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradePay;
import com.ftrend.zgp.model.TradePay_Table;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.TradeProd_Table;
import com.ftrend.zgp.model.TradeUploadQueue;
import com.ftrend.zgp.model.Trade_Table;
import com.ftrend.zgp.utils.db.TransHelper;
import com.ftrend.zgp.utils.db.ZgpDb;
import com.ftrend.zgp.utils.pay.PayType;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.ftrend.zgp.utils.TradeHelper.DELFLAG_NO;
import static com.ftrend.zgp.utils.TradeHelper.TRADE_FLAG_REFUND;
import static com.ftrend.zgp.utils.TradeHelper.TRADE_STATUS_PAID;
import static com.ftrend.zgp.utils.TradeHelper.newLsNo;

/**
 * @author liziqiang@ftrend.cn
 */

public class RtnHelper {
    private static final String TAG = "RtnHelper";
    // 退货流水
    private static Trade rtnTrade = null;
    // 退货商品列表
    private static List<TradeProd> rtnProdList = null;
    // 退货商品更新序号前的缓存
    private static List<TradeProd> tempRtnList = null;
    // 退货支付信息
    private static TradePay rtnPay = null;
    // 交易流水
    private static Trade trade = null;
    // 商品列表
    private static List<TradeProd> prodList = null;
    // 支付信息
    private static TradePay pay = null;
    // 已退流水
    public static final String TRADE_FLAG_RTN = "1";
    // 未退流水
    public static final String TRADE_FLAG_SALE = "0";

    public static Trade getRtnTrade() {
        return rtnTrade;
    }

    public static List<TradeProd> getRtnProdList() {
        return rtnProdList;
    }

    public static TradePay getRtnPay() {
        return rtnPay;
    }

    public static Trade getTrade() {
        return trade;
    }

    public static List<TradeProd> getProdList() {
        return prodList;
    }

    public static TradePay getPay() {
        return pay;
    }

    public static void setTrade(Trade trade) {
        RtnHelper.trade = trade;
    }

    public static void setProdList(List<TradeProd> prodList) {
        RtnHelper.prodList = prodList;
    }

    public static void setPay(TradePay pay) {
        RtnHelper.pay = pay;
    }

    /**
     * 退货----初始化根据流水号查到的流水
     *
     * @return 是否有该流水
     */
    public static boolean initRtnLocal(String lsNo) {
        //初始化时销售流水+退货流水
        trade = TradeHelper.getPaidLs(lsNo);
        if (trade == null) {
            return false;
        } else {
            //获取销售流水
            prodList = SQLite.select().from(TradeProd.class)
                    .where(TradeProd_Table.lsNo.eq(trade.getLsNo()))
                    .and(TradeProd_Table.delFlag.eq(DELFLAG_NO))
                    .queryList();
            pay = SQLite.select().from(TradePay.class)
                    .where(TradePay_Table.lsNo.eq(trade.getLsNo()))
                    .querySingle();
            //更新已退货数量和金额
            for (TradeProd prod : prodList) {
                String saleInfo = makeSaleInfo(trade.getLsNo(), trade.getTradeTime(), prod.getSortNo());
                FlowCursor cursor = SQLite.select(Method.sum(TradeProd_Table.amount), Method.sum(TradeProd_Table.total))
                        .from(TradeProd.class)
                        .where(TradeProd_Table.saleInfo.eq(saleInfo))
                        .query();
                if (cursor.moveToNext()) {
                    prod.setLastRtnAmount(cursor.getDoubleOrDefault(0));
                    prod.setLastRtnTotal(cursor.getDoubleOrDefault(1));
                }
            }

            //设置退货单价
            for (TradeProd prod : prodList) {
                prod.setRtnPrice(prod.getTotal() / prod.getAmount());
            }
            //新建退货流水
            rtnTrade = new Trade();
            rtnTrade.setLsNo(newLsNo());
            rtnTrade.setDepCode(trade.getDepCode());
            rtnTrade.setTradeTime(trade.getTradeTime());
            rtnTrade.setTradeFlag(TRADE_FLAG_REFUND);
            rtnTrade.setCashier(trade.getCashier());
            //必须保存会员信息
            rtnTrade.setCustType(trade.getCustType());
            rtnTrade.setVipCode(trade.getVipCode());
            rtnTrade.setCardCode(trade.getCardCode());
            //置零
            rtnTrade.setVipTotal(0);
            rtnTrade.setDscTotal(0);
            rtnTrade.setTotal(0);
            //初始化退货流水的时间
            rtnTrade.setCreateTime(new Date());
            //初始化退货流水的创建IP
            rtnTrade.setCreateIp(ZgParams.getCurrentIp());
            //初始化退货流水为未结单
            rtnTrade.setTradeFlag(TRADE_FLAG_REFUND);

            rtnProdList = new ArrayList<>();

            rtnPay = null;

            return rtnTrade != null;
        }
    }

    /**
     * 联网获取
     *
     * @return 是否初始化完成
     */
    public static boolean initRtnOnline() {
        if (trade != null && (prodList != null && !prodList.isEmpty()) && pay != null) {
            //新建退货流水
            rtnPay = null;
            rtnTrade = new Trade();
            rtnProdList = new ArrayList<>();
            //设置退货单价
            for (TradeProd prod : prodList) {
                prod.setRtnPrice(prod.getTotal() / prod.getAmount());
            }
            rtnTrade.setLsNo(newLsNo());
            rtnTrade.setDepCode(trade.getDepCode());
            rtnTrade.setTradeTime(trade.getTradeTime());
            rtnTrade.setTradeFlag(TRADE_FLAG_REFUND);
            rtnTrade.setCashier(trade.getCashier());
            //必须保存会员信息
            rtnTrade.setCustType(trade.getCustType());
            rtnTrade.setVipCode(trade.getVipCode());
            rtnTrade.setCardCode(trade.getCardCode());
            //置零
            rtnTrade.setVipTotal(0);
            rtnTrade.setDscTotal(0);
            rtnTrade.setTotal(0);
            //初始化退货流水的时间
            rtnTrade.setCreateTime(new Date());
            //初始化退货流水的创建IP
            rtnTrade.setCreateIp(ZgParams.getCurrentIp());
        }
        return (trade != null) && (rtnTrade != null);
    }

    /**
     * @param index       索引
     * @param changePrice 改价
     * @return 是否成功
     */
    public static boolean changeRtnPrice(int index, double changePrice) {
        if (index < 0 || index >= prodList.size()) {
            Log.e(TAG, "改变数量: 索引无效");
            return false;
        }
        TradeProd prod = prodList.get(index);
        prod.setRtnPrice(changePrice);
        if (!rtnProdList.isEmpty()) {
            for (TradeProd rtnProd : rtnProdList) {
                if (rtnProd.getSortNo().equals(prod.getSortNo())) {
                    rtnProd.setPrice(changePrice);
                    rtnProd.setTotal(rtnProd.getAmount() * changePrice);
                }
            }
        }
        recalcRtnTrade();
        return prod.getRtnPrice() == changePrice;
    }

    public static void recalcRtnTrade() {
        //统一更新交易流水
        double rtnAmount = 0, rtnTotal = 0;
        for (TradeProd p : rtnProdList) {
            rtnAmount += p.getAmount();
            rtnTotal += p.getTotal();
        }
        //更新退货信息以刷新界面
        rtnTrade.setTotal(rtnTotal);
        rtnTrade.setAmount(rtnAmount);
    }

    /**
     * @param index        索引
     * @param changeAmount 改变数量
     */
    public static void rtnChangeAmount(int index, double changeAmount) {
        if (index < 0 || index >= prodList.size()) {
            Log.e(TAG, "改变数量: 索引无效");
            return;
        }
        //根据sortNo来做查询条件，此时退货商品列表里的商品的sortNo还是原销售单里的sortNo
        //更新为新的sortNo在最后提交的时候完成
        TradeProd prod = prodList.get(index);
        //最大允许可退货数量
        double rtnMax = prod.getAmount() + prod.getLastRtnAmount();
        //先检查是否已添加过，如果添加过，那么只需要更新数据，否则就得插入新的商品
        if (!rtnProdList.isEmpty()) {
            boolean isAdded = false;
            for (TradeProd rtnProd : rtnProdList) {
                if (rtnProd.getSortNo().equals(prod.getSortNo())) {
                    //已经在退货列表中
                    isAdded = true;
                    double amount = rtnProd.getAmount() + changeAmount;
                    //不能超过可退货数量
                    if (amount < 0 || amount > rtnMax) {
                        return;
                    } else {
                        rtnProd.setAmount(amount);
                        rtnProd.setTotal(amount * prod.getRtnPrice());
                    }
                }
            }
            if (!isAdded && changeAmount > 0 && changeAmount <= rtnMax) {
                //不存在，那么插入
                TradeProd rtnProd = new TradeProd();
                rtnProd.setLsNo(rtnTrade.getLsNo());
                rtnProd.setSortNo(prod.getSortNo());
                rtnProd.setProdCode(prod.getProdCode());
                rtnProd.setProdName(prod.getProdName());
                rtnProd.setBarCode(prod.getBarCode());
                rtnProd.setDepCode(prod.getDepCode());
                //此处是退货单价
                rtnProd.setPrice(prod.getRtnPrice());
                rtnProd.setProdForDsc(prod.getProdForDsc());
                rtnProd.setProdPriceFlag(prod.getProdPriceFlag());
                rtnProd.setProdIsLargess(prod.getProdIsLargess());
                rtnProd.setProdMinPrice(prod.getProdMinPrice());
                //优惠
                rtnProd.setSingleDsc(0);
                rtnProd.setWholeDsc(0);
                rtnProd.setTranDsc(0);
                rtnProd.setVipDsc(0);
                //数量
                rtnProd.setAmount(changeAmount);
                //小计
                rtnProd.setTotal(prod.getRtnPrice() * rtnProd.getAmount());
                //插入原单信息
                rtnProd.setSaleInfo(makeSaleInfo(trade.getLsNo(), trade.getTradeTime(), prod.getSortNo()));
                rtnProd.setDelFlag(DELFLAG_NO);
                rtnProdList.add(rtnProd);
            }
        } else {
            if (changeAmount > 0 && changeAmount <= rtnMax) {
                //不存在，那么插入
                TradeProd rtnProd = new TradeProd();
                rtnProd.setLsNo(rtnTrade.getLsNo());
                rtnProd.setSortNo(prod.getSortNo());
                rtnProd.setProdCode(prod.getProdCode());
                rtnProd.setProdName(prod.getProdName());
                rtnProd.setBarCode(prod.getBarCode());
                rtnProd.setDepCode(prod.getDepCode());
                //此处是退货单价
                rtnProd.setPrice(prod.getRtnPrice());
                rtnProd.setProdForDsc(prod.getProdForDsc());
                rtnProd.setProdPriceFlag(prod.getProdPriceFlag());
                rtnProd.setProdIsLargess(prod.getProdIsLargess());
                rtnProd.setProdMinPrice(prod.getProdMinPrice());
                //优惠
                rtnProd.setSingleDsc(0);
                rtnProd.setWholeDsc(0);
                rtnProd.setTranDsc(0);
                rtnProd.setVipDsc(0);
                //数量
                rtnProd.setAmount(changeAmount);
                //小计
                rtnProd.setTotal(prod.getRtnPrice() * rtnProd.getAmount());
                //插入原单信息
                rtnProd.setSaleInfo(makeSaleInfo(trade.getLsNo(), trade.getTradeTime(), prod.getSortNo()));
                rtnProd.setDelFlag(DELFLAG_NO);
                rtnProdList.add(rtnProd);
            }
        }
        //统一更新交易流水
        recalcRtnTrade();
    }

    /**
     * 生成退货原单信息
     *
     * @param lsNo
     * @param tradeTime
     * @param sortNo
     * @return
     */
    private static String makeSaleInfo(String lsNo, Date tradeTime, Long sortNo) {
        return String.format(Locale.CHINA, "%s %s %d",
                lsNo,
                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(tradeTime),
                sortNo);
    }

    /**
     * 当前流水是否现金支付
     * @return
     */
    public static boolean isPayCash() {
        return (pay != null) && PayType.PAYTYPE_CASH.equals(pay.getPayTypeCode());
    }

    /**
     * 更新trade
     *
     * @return 是否成功
     */
    public static boolean saveRtnTrade() {
        return saveRtnTrade(FlowManager.getDatabase(ZgpDb.class).getWritableDatabase());
    }

    public static boolean saveRtnTrade(DatabaseWrapper databaseWrapper) {
        rtnTrade.setTradeTime(rtnPay.getPayTime());
        rtnTrade.setCashier(ZgParams.getCurrentUser().getUserCode());
        rtnTrade.setStatus(TRADE_STATUS_PAID);
        //pay中保存的已是负数
        rtnTrade.setTotal(rtnPay.getAmount());
        return rtnTrade.insert(databaseWrapper) > 0;
    }

    /**
     * @return 是否退货
     */
    public static boolean rtn() {
        return TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                return doRtn(databaseWrapper);
            }
        });

    }

    private static boolean doRtn(DatabaseWrapper databaseWrapper) {
        //退货需要集中处理退货商品的序号sortNo
        //需要把退货数量为0的商品移除
        //region 缓存备份当前退货列表
        if (tempRtnList == null) {
            tempRtnList = new ArrayList<>();
        }
        tempRtnList.clear();
        tempRtnList.addAll(rtnProdList);
        //endregion

        //region 移除数量为0的商品
        List<TradeProd> midList = new ArrayList<>();
        //生成sortNo
        long index = 1;
        for (TradeProd prod : rtnProdList) {
            //过滤数量
            if (prod.getAmount() != 0) {
                //修改序号
                prod.setSortNo(index);
                midList.add(prod);
                index++;
            }
        }
        //过滤完毕，清空原表
        rtnProdList.clear();
        //覆盖原表
        rtnProdList.addAll(midList);
        //清空中间表，释放空间
        midList.clear();
        //endregion
        for (TradeProd prod : rtnProdList) {
            prod.setTotal(prod.getTotal() * -1);
            prod.setAmount(prod.getAmount() * -1);
            prod.insert(databaseWrapper);
        }
        if (saveRtnTrade()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param sortNo 序号
     * @return 退货数量
     */
    public static double getRtnAmountBySortNo(long sortNo) {
        double rtnAmount = 0;
        if (rtnProdList.size() != 0) {
            for (TradeProd prod : rtnProdList) {
                if (prod.getSortNo() == sortNo) {
                    rtnAmount = prod.getAmount();
                }
            }
        }
        return rtnAmount;
    }

    /**
     * @param sortNo 序号
     * @return 退货小计
     */
    public static double getRtnTotalBySortNo(long sortNo) {
        double rtnTotal = 0;
        if (rtnProdList.size() != 0) {
            for (TradeProd prod : rtnProdList) {
                if (prod.getSortNo() == sortNo) {
                    rtnTotal = prod.getTotal();
                }
            }
        }
        return rtnTotal;
    }

    /**
     * 完成支付（仅适用于现金支付）
     *
     * @param appPayType APP支付类型
     * @param value      实际支付金额，为0时全额支付
     * @return
     */
    public static boolean pay(String appPayType, double value) {
        if (value > 0) {
            if (value < rtnTrade.getTotal()) {
                return false;
            } else {
                return pay(appPayType, value, value - rtnTrade.getTotal(), "(无)");
            }
        } else {
            return pay(appPayType, rtnTrade.getTotal(), 0, "(无)");
        }
    }

    /**
     * 完成支付（适用于微信、支付宝、储值卡等支付方式）
     *
     * @param appPayType APP支付类型
     * @param payCode    支付账号
     * @return
     */
    public static boolean pay(String appPayType, String payCode) {
        return pay(appPayType, rtnTrade.getTotal(), 0, payCode);
    }

    //endregion

    /**
     * 完成支付
     *
     * @param appPayType APP支付类型
     * @param amount     支付金额
     * @param payCode    支付账号
     * @return 支付结果
     */
    public static boolean pay(final String appPayType, final double amount, final double change,
                              final String payCode) {
        return TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                return doPay(databaseWrapper, appPayType, amount, change, payCode);
            }
        });
    }

    private static boolean doPay(DatabaseWrapper databaseWrapper,
                                 String appPayType, double amount, double change, String payCode) {
        try {
            String payTypeCode = PayType.appPayTypeToPayType(ZgParams.getCurrentDep().getDepCode(), appPayType);

            if (rtnPay == null) {
                rtnPay = new TradePay();
                rtnPay.setLsNo(rtnTrade.getLsNo());
            }
            rtnPay.setPayTypeCode(payTypeCode);
            rtnPay.setAppPayType(appPayType);
            //金额
            rtnPay.setAmount(amount * -1);
            rtnPay.setChange(change);
            rtnPay.setPayCode(payCode);
            rtnPay.setPayTime(new Date());
            if (!rtnPay.save(databaseWrapper)) {
                return false;
            }
            //添加到上传队列
            TradeUploadQueue queue = new TradeUploadQueue(rtnTrade.getDepCode(), rtnTrade.getLsNo());
            return queue.insert(databaseWrapper) > 0;
        } catch (Exception e) {
            Log.e(TAG, "支付异常: " + rtnPay.getLsNo() + " - " + appPayType, e);
            return false;
        }
    }

    /**
     * 检查结算是否成功
     *
     * @param lsNo 流水单号
     * @return 订单状态
     */
    public static boolean checkPayStatus(String lsNo) {
        Trade payTrade = SQLite.select().from(Trade.class).where(Trade_Table.lsNo.eq(lsNo))
                .querySingle();
        if (payTrade != null) {
            return TRADE_STATUS_PAID.equals(payTrade.getStatus());
        } else {
            return false;
        }
    }

    /**
     * @return 获取订单号
     */
    public static String getRtnSn() {
        String sn = "";
        SqbPayOrder sqbPayOrder = SQLite.select().from(SqbPayOrder.class)
                .where(SqbPayOrder_Table.lsNo.eq(trade.getLsNo()))
                .and(SqbPayOrder_Table.depCode.eq(trade.getDepCode()))
                .querySingle();
        if (sqbPayOrder != null) {
            return sqbPayOrder.getSn();
        }
        return sn;
    }


    /**
     * 清空所有临时数据
     */
    public static void clearAllData() {
        rtnTrade = null;
        rtnProdList = null;
        rtnPay = null;
        trade = null;
        prodList = null;
        pay = null;
    }
}


//退货流程:  保存pay---->保存trade---->保存prodList