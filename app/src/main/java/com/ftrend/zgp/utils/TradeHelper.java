package com.ftrend.zgp.utils;

import android.text.TextUtils;
import android.util.Log;

import com.ftrend.zgp.model.AppParams;
import com.ftrend.zgp.model.AppParams_Table;
import com.ftrend.zgp.model.Dep;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepPayInfo;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.DepProduct_Table;
import com.ftrend.zgp.model.SysParams;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradePay;
import com.ftrend.zgp.model.TradePay_Table;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.TradeProd_Table;
import com.ftrend.zgp.model.TradeUploadQueue;
import com.ftrend.zgp.model.Trade_Table;
import com.ftrend.zgp.model.User;
import com.ftrend.zgp.model.User_Table;
import com.ftrend.zgp.model.VipInfo;
import com.ftrend.zgp.utils.db.TransHelper;
import com.ftrend.zgp.utils.db.ZgpDb;
import com.ftrend.zgp.utils.log.LogUtil;
import com.ftrend.zgp.utils.pay.PayType;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.raizlabs.android.dbflow.sql.language.Method.count;
import static com.raizlabs.android.dbflow.sql.language.Method.sum;

/**
 * 交易操作类
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/9/6
 */
public class TradeHelper {
    private static String TAG = "TradeHelper";

    //行清标记：0-未删除
    public static final String DELFLAG_NO = "0";
    //行清标记：0-已删除
    public static final String DELFLAG_YES = "1";

    // 交易类型：T-销售
    public static final String TRADE_FLAG_SALE = "T";
    // 交易类型：R-退货
    public static final String TRADE_FLAG_REFUND = "R";

    // 交易状态：0-未结
    public static final String TRADE_STATUS_NOTPAY = "0";
    // 交易状态：1-挂起
    public static final String TRADE_STATUS_HANGUP = "1";
    // 交易状态：2-已结
    public static final String TRADE_STATUS_PAID = "2";
    // 交易状态：3-取消
    public static final String TRADE_STATUS_CANCELLED = "3";
    // 超市版：会员优惠规则-1
    public static final int VIP_ONE = 1;
    // 超市版：会员优惠规则-2
    public static final int VIP_TWO = 2;
    // 超市版：会员优惠规则-3
    public static final int VIP_THREE = 3;
    // VIP强制优惠：1-强制优惠，无视商品的forDsc属性
    public static final String VIP_DSC_FORCE = "1";
    // VIP强制优惠：0-不强制
    public static final String VIP_DSC_NORMAL = "0";
    // 顾客类型：2-会员
    public static final String TRADE_CUST_VIP = "2";
    // APP界面支付类型：现金
    public static final String APP_PAY_TYPE_CASH = "1";
    // APP界面支付类型：支付宝
    public static final String APP_PAY_TYPE_ALIPAY = "2";
    // APP界面支付类型：微信支付
    public static final String APP_PAY_TYPE_WECHAT = "3";
    // APP界面支付类型：储值卡
    public static final String APP_PAY_TYPE_CARD = "4";
    // APP界面支付类型：收钱吧
    public static final String APP_PAY_TYPE_SQB = "5";
    // 交易流水
    private static Trade trade = null;
    // 退货流水
    private static Trade rtnTrade = null;
    // 商品列表
    private static List<TradeProd> prodList = null;
    // 退货商品列表
    private static List<TradeProd> rtnProdList = null;
    // 支付信息
    private static TradePay pay = null;
    // 退货支付信息
    private static TradePay rtnPay = null;
    // 会员信息
    public static VipInfo vip = null;

    public static Trade getTrade() {
        return trade;
    }

    public static Trade getRtnTrade() {
        return rtnTrade;
    }

    public static TradePay getPay() {
        return pay;
    }

    public static List<TradeProd> getProdList() {
        return prodList;
    }

    public static List<TradeProd> getRtnProdList() {
        return rtnProdList;
    }

    public static void setTrade(Trade trade) {
        TradeHelper.trade = trade;
    }

    public static void setProdList(List<TradeProd> prodList) {
        TradeHelper.prodList = prodList;
    }

    public static void setPay(TradePay pay) {
        TradeHelper.pay = pay;
    }

    /**
     * 清空当前交易信息
     */
    public static void clear() {
        trade = null;
        prodList = null;
        pay = null;
    }

    //region 销售

    /**
     * 查询当前购物车交易流水
     *
     * @return 交易流水主表信息，如果购物车为空，返回null
     */
    private static Trade getCartLs() {
        return SQLite.select().from(Trade.class)
                .where(Trade_Table.status.eq(TRADE_STATUS_NOTPAY))
                .and(Trade_Table.tradeFlag.eq(TRADE_FLAG_SALE))
                .and(Trade_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                .querySingle();
    }

    /**
     * 初始化当前操作的交易流水，读取未结销售流水，不存在则创建新的流水
     *
     * @return 数据库内是否有次流水
     */
    public static boolean initSale() {
        trade = getCartLs();
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
            trade.setCreateTime(new Date());
            trade.setCreateIp(ZgParams.getCurrentIp());
            trade.setStatus(TRADE_STATUS_NOTPAY);

            prodList = new ArrayList<>();
            pay = null;
            return false;
        } else {
            prodList = SQLite.select().from(TradeProd.class)
                    .where(TradeProd_Table.lsNo.eq(trade.getLsNo()))
                    .and(TradeProd_Table.delFlag.eq(DELFLAG_NO))
                    .queryList();
            pay = SQLite.select().from(TradePay.class)
                    .where(TradePay_Table.lsNo.eq(trade.getLsNo()))
                    .querySingle();
            return true;
        }
    }

    /**
     * 退货----根据流水单号获取流水信息
     *
     * @param lsNo 流水单号
     * @return 流水信息
     */
    private static Trade getPaidLs(String lsNo) {
        return SQLite.select().from(Trade.class)
                .where(Trade_Table.status.eq(TRADE_STATUS_PAID))
                .and(Trade_Table.tradeFlag.eq(TRADE_FLAG_SALE))
                .and(Trade_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                .and(Trade_Table.lsNo.eq(lsNo))
                .querySingle();
    }

    /**
     * 退货----初始化根据流水号查到的流水
     *
     * @return 是否有该流水
     */
    public static boolean initRtnSale(String lsNo) {
        trade = getPaidLs(lsNo);
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
            //新建退货流水
            rtnTrade = new Trade();
            rtnTrade.setLsNo(newLsNo());
            rtnTrade.setDepCode(trade.getDepCode());
            rtnTrade.setTradeTime(trade.getTradeTime());
            rtnTrade.setTradeFlag(TRADE_FLAG_REFUND);
            rtnTrade.setCashier(trade.getCashier());
            rtnTrade.setDscTotal(trade.getDscTotal());
            rtnTrade.setTotal(trade.getTotal());
            rtnTrade.setCustType(trade.getCustType());
            rtnTrade.setVipCode(trade.getVipCode());
            rtnTrade.setCardCode(trade.getCardCode());
            rtnTrade.setVipTotal(trade.getVipTotal());
            //初始化退货流水的时间
            rtnTrade.setCreateTime(new Date());
            //初始化退货流水的创建IP
            rtnTrade.setCreateIp(ZgParams.getCurrentIp());
            //初始化退货流水为未结单
            rtnTrade.setTradeFlag(TRADE_FLAG_REFUND);

            rtnProdList = new ArrayList<>();
            rtnPay = null;
//            pay = SQLite.select().from(TradePay.class)
//                    .where(TradePay_Table.lsNo.eq(lsNo))
//                    .querySingle();
//            prodList = SQLite.select().from(TradeProd.class)
//                    .where(TradeProd_Table.lsNo.eq(lsNo))
//                    .and(TradeProd_Table.delFlag.eq(DELFLAG_NO))
//                    .queryList();
//            for (TradeProd prod : prodList) {
//                //初始化退货单价
//                prod.setRtnPrice(prod.getPrice() - ((prod.getManuDsc() + prod.getVipDsc() + prod.getTranDsc()) / prod.getAmount()));
//            }
            return true;
        }

    }
//

    /**
     * 根据流水号查询交易
     *
     * @param lsNo 流水号
     * @return 流水
     */
    public static Trade getTradeByLsNo(String lsNo) {
        return SQLite.select().from(Trade.class)
                .where(Trade_Table.lsNo.eq(lsNo))
                .querySingle();
    }

    /**
     * @param lsNo 流水号
     * @return 支付信息
     */
    public static TradePay getPayByLsNo(String lsNo) {
        return SQLite.select().from(TradePay.class)
                .where(TradePay_Table.lsNo.eq(lsNo))
                .querySingle();
    }

    /**
     * 根据当前退货流水和序号获取商品已退货数量
     *
     * @param sortNo 序号
     * @return 数量
     */
    public static double getRtnAmountBySortNo(long sortNo) {
        return SQLite.select().from(TradeProd.class)
                .where(TradeProd_Table.sortNo.eq(sortNo))
                .and(TradeProd_Table.lsNo.eq(rtnTrade.getLsNo()))
                .querySingle().getAmount();
    }

    /**
     * 根据流水号获取未行清商品明细
     *
     * @param lsNo 流水号
     * @return 商品
     */
    public static List<TradeProd> getProdListByLsNo(String lsNo) {
        List<TradeProd> prodList = new ArrayList<>();
        prodList = SQLite.select().from(TradeProd.class)
                .where(TradeProd_Table.lsNo.eq(lsNo))
                .and(TradeProd_Table.delFlag.eq(DELFLAG_NO))
                .queryList();
        return prodList;
    }

    /**
     * 添加商品到商品列表
     *
     * @param product 商品信息
     * @return
     */
    public static long addProduct(final DepProduct product) {
        final long[] index = {-1};
        TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                index[0] = doAddProduct(databaseWrapper, product);
                return index[0] >= 0;
            }
        });
        return index[0];
    }

    private static long doAddProduct(DatabaseWrapper databaseWrapper, DepProduct product) {
        long index = prodList.size();
        final TradeProd prod = new TradeProd();
        prod.setLsNo(trade.getLsNo());
        prod.setSortNo(index + 1);//商品序号从1开始
        prod.setProdCode(product.getProdCode());
        prod.setProdName(product.getProdName());
        prod.setBarCode(product.getBarCode());
        prod.setDepCode(product.getDepCode());
        prod.setPrice(product.getPrice());
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
        //保存商品记录并重新汇总流水金额（此时会保存交易流水）
        if (prod.insert(databaseWrapper) > 0) {
            prodList.add(prod);
            if (recalcTotal(databaseWrapper)) {
                return index;
            } else {
                //流水保存失败，删除新添加的商品
                prodList.remove(prod);
                recalcTotal(databaseWrapper);
                return -1;
            }
        } else {
            return -1;
        }
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
        return prod.getRtnPrice() == changePrice;
    }


    public static void rtnChangeAmount(int index, double changeAmount) {
        if (index < 0 || index >= prodList.size()) {
            Log.e(TAG, "改变数量: 索引无效");
            return;
        }
        long sortNo = prodList.get(index).getSortNo();
        //先检查是否已添加过，如果添加过，那么只需要更新数据，否则就得插入新的商品
        if (rtnProdList.size() != 0) {
            boolean isAdded = false;
            for (TradeProd rtnProd : rtnProdList) {
                if (rtnProd.getSortNo() == sortNo) {
                    //已经在退货列表中
                    isAdded = true;
                    double amount = rtnProd.getAmount();
                    rtnProd.setAmount(amount + changeAmount);
                }
            }
            if (!isAdded) {
                //不存在，那么插入
                TradeProd rtnProd = new TradeProd();
                TradeProd prod = prodList.get(index);
                rtnProd.setLsNo(rtnTrade.getLsNo());
                rtnProd.setSortNo(prod.getSortNo());
                rtnProd.setProdCode(prod.getProdCode());
                rtnProd.setProdName(prod.getProdName());
                rtnProd.setBarCode(prod.getBarCode());
                rtnProd.setDepCode(prod.getDepCode());
                rtnProd.setPrice(prod.getPrice());
                rtnProd.setProdForDsc(prod.getProdForDsc());
                rtnProd.setProdPriceFlag(prod.getProdPriceFlag());
                rtnProd.setProdIsLargess(prod.getProdIsLargess());
                rtnProd.setProdMinPrice(prod.getProdMinPrice());
                //优惠
                rtnProd.setSingleDsc(prod.getSingleDsc() / prod.getAmount() * changeAmount);
                rtnProd.setWholeDsc(prod.getWholeDsc() / prod.getAmount() * changeAmount);
                rtnProd.setTranDsc(prod.getTranDsc() / prod.getAmount() * changeAmount);
                rtnProd.setVipDsc(prod.getVipDsc() / prod.getAmount() * changeAmount);
                rtnProd.setAmount(changeAmount);
                //插入原单信息
                rtnProd.setSaleInfo(String.format("%s %s %s", trade.getLsNo(), trade.getTradeTime(), prod.getSortNo()));
                rtnProd.setDelFlag(DELFLAG_NO);
                rtnProdList.add(rtnProd);
            }
        } else {
            //不存在，那么插入
            TradeProd rtnProd = new TradeProd();
            TradeProd prod = prodList.get(index);
            rtnProd.setLsNo(rtnTrade.getLsNo());
            rtnProd.setSortNo(prod.getSortNo());
            rtnProd.setProdCode(prod.getProdCode());
            rtnProd.setProdName(prod.getProdName());
            rtnProd.setBarCode(prod.getBarCode());
            rtnProd.setDepCode(prod.getDepCode());
            rtnProd.setPrice(prod.getPrice());
            rtnProd.setProdForDsc(prod.getProdForDsc());
            rtnProd.setProdPriceFlag(prod.getProdPriceFlag());
            rtnProd.setProdIsLargess(prod.getProdIsLargess());
            rtnProd.setProdMinPrice(prod.getProdMinPrice());
            //优惠
            rtnProd.setSingleDsc(prod.getSingleDsc() / prod.getAmount() * changeAmount);
            rtnProd.setWholeDsc(prod.getWholeDsc() / prod.getAmount() * changeAmount);
            rtnProd.setTranDsc(prod.getTranDsc() / prod.getAmount() * changeAmount);
            rtnProd.setVipDsc(prod.getVipDsc() / prod.getAmount() * changeAmount);
            rtnProd.setAmount(changeAmount);
            //插入原单信息
            rtnProd.setSaleInfo(String.format("%s %s %s", trade.getLsNo(), trade.getTradeTime(), prod.getSortNo()));
            rtnProd.setDelFlag(DELFLAG_NO);
            rtnProdList.add(rtnProd);
        }
        //更新优惠信息
        double rtnAmount = 0, rtnTotal = 0;
        //更新退货信息以刷新界面
        for (TradeProd prod : rtnProdList) {
            rtnAmount += prod.getAmount();
            rtnTotal += prod.getTotal();
        }
        rtnTrade.setTotal(rtnTotal);
        rtnTrade.setAmount(rtnAmount);
    }

    /**
     * @param prod 商品
     * @return 优惠后的单价
     */
    public static double getRtnPrice(TradeProd prod) {
        return prod.getPrice() - ((prod.getManuDsc() + prod.getVipDsc() + prod.getTranDsc()) / prod.getAmount());
    }

    /**
     * 行清
     *
     * @param index 行清的商品索引
     * @return 是否成功
     */
    public static boolean delProduct(final int index) {
        return TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                return doDelProduct(databaseWrapper, index);
            }
        });
    }

    private static boolean doDelProduct(DatabaseWrapper databaseWrapper, int index) {
        if (index < 0 || index >= prodList.size()) {
            Log.e(TAG, "行清: 索引无效");
            return false;
        }
        TradeProd prod = prodList.get(index);
        prod.setDelFlag(DELFLAG_YES);
        //保存商品记录并重新汇总流水金额（此时会保存交易流水）
        if (prod.save(databaseWrapper)) {
            prodList.remove(prod);
            if (recalcTotal(databaseWrapper)) {
                return true;
            } else {
                //流水保存失败，重新添加删除的商品
                prodList.add(index, prod);
                recalcTotal(databaseWrapper);
                return false;
            }
        } else {
            return false;
        }
    }
    //endregion

    //region 结算

    /**
     * 完成支付
     *
     * @param appPayType APP支付类型
     * @param amount     支付金额
     * @param change     找零金额
     * @param payCode    支付账号
     * @return 支付结果
     */
    public static boolean pay(final String appPayType, final double amount,
                              final double change, final String payCode) {
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

            if (pay == null) {
                pay = new TradePay();
                pay.setLsNo(trade.getLsNo());
            }
            pay.setPayTypeCode(payTypeCode);
            pay.setAppPayType(appPayType);
            pay.setAmount(amount);
            pay.setChange(change);
            pay.setPayCode(payCode);
            pay.setPayTime(new Date());
            if (!pay.save(databaseWrapper)) {
                return false;
            }
            trade.setTradeTime(pay.getPayTime());
            trade.setCashier(ZgParams.getCurrentUser().getUserCode());
            trade.setStatus(TRADE_STATUS_PAID);
            return trade.save(databaseWrapper);
        } catch (Exception e) {
            Log.e(TAG, "支付异常: " + pay.getLsNo() + " - " + appPayType, e);
            return false;
        }
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
            if (value < trade.getTotal()) {
                return false;
            } else {
                return pay(appPayType, value, value - trade.getTotal(), "(无)");
            }
        } else {
            return pay(appPayType, trade.getTotal(), 0, "(无)");
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
        return pay(appPayType, trade.getTotal(), 0, payCode);
    }

    //endregion

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
     * 最小流水号
     *
     * @return
     */
    public static String lsNoMin() {
        return ZgParams.getPosCode() + "00001";
    }

    /**
     * 最大流水号
     *
     * @return
     */
    public static String lsNoMax() {
        return ZgParams.getPosCode() + "99999";
    }

    /**
     * @return 退货流水号
     */
    public static String newRtnLsNo() {
        return String.format("%s%s",
                new SimpleDateFormat("yyyyMMdd").format(new Date()), trade.getLsNo());
    }

    /**
     * 生成新的流水号
     *
     * @return
     */
    private static String newLsNo() {
        String lastNo = "";
        //查询数据库中当前最大的流水号
        FlowCursor cursor = SQLite.select(Method.max(Trade_Table.lsNo)).from(Trade.class).query();
        if (cursor != null) {
            if (cursor.moveToNext()) {
                lastNo = cursor.getStringOrDefault(0);
            }
        }
        //数据库无流水，则取本地参数中的最大流水号
        if (TextUtils.isEmpty(lastNo)) {
            lastNo = ZgParams.getLastLsNo();
        }

        String lsNo = lsNoMin();
        if (!TextUtils.isEmpty(lastNo)) {
            int max = Integer.valueOf(lastNo.substring(3));
            int current = (max == 99999) ? 1 : max + 1;
            lsNo = ZgParams.getPosCode() + String.format(Locale.CHINA, "%05d", current);
        }
        ZgParams.saveAppParams("lastLsNo", lsNo);
        ZgParams.setLastLsNo(lsNo);
        return lsNo;
    }

    /**
     * 重新汇总流水金额：优惠、合计
     */
    public static boolean recalcTotal() {
        return recalcTotal(FlowManager.getDatabase(ZgpDb.class).getWritableDatabase());
    }

    public static boolean recalcTotal(DatabaseWrapper databaseWrapper) {
        double dscTotal = 0;
        double total = 0;

        for (TradeProd prod : prodList) {
            dscTotal += prod.getManuDsc() + prod.getTranDsc() + prod.getVipDsc();
            total += prod.getTotal();
        }
        trade.setDscTotal(dscTotal);
        trade.setTotal(total);
        return trade.save(databaseWrapper);
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
        //需要退货的商品
        List<TradeProd> rtnProdList = new ArrayList<>();
        for (TradeProd prod : prodList) {
            if (prod.getRtnAmount() != 0) {
                //更新商品的流水为退货流水号
                prod.setLsNo(trade.getRtnLsNo());
                rtnProdList.add(prod);
            }
        }
        prodList.clear();
        prodList = rtnProdList;
        if (recalcRtnTotal() > 0) {
            //TODO 2019年11月6日16:48:08 需要保存为pay.getTradeTime()
            //保存交易日期
            trade.setTradeTime(new Date());
            //更新退货为已结状态
            trade.setStatus(TRADE_STATUS_PAID);
            //更新退货流水
            trade.setLsNo(trade.getRtnLsNo());
            return trade.save(databaseWrapper);
        } else {
            return false;
        }
    }

    /**
     * 重新汇总退货流水金额：优惠、合计
     * 保存的时候执行本方法
     *
     * @return 是否成功
     */
    public static long recalcRtnTotal() {
        return recalcRtnTotal(FlowManager.getDatabase(ZgpDb.class).getWritableDatabase());
    }

    public static long recalcRtnTotal(DatabaseWrapper databaseWrapper) {
        //重新计算优惠金额
        //赋值rtnAmount\rtnTotal
        double singleDsc, wholeDsc, vipDsc, tranDsc;
        for (TradeProd prod : prodList) {
            TradeProd tradeProd = new TradeProd();

            tradeProd.setLsNo(prod.getLsNo());
            tradeProd.setSortNo(prod.getSortNo());
            tradeProd.setProdCode(prod.getProdCode());
            tradeProd.setBarCode(prod.getBarCode());
            tradeProd.setProdName(prod.getProdName());
            tradeProd.setPrice(prod.getPrice());
            tradeProd.setDelFlag(prod.getDelFlag());
            tradeProd.setDepCode(prod.getDepCode());
            tradeProd.setProdForDsc(prod.getProdForDsc());
            tradeProd.setProdPriceFlag(prod.getProdPriceFlag());
            tradeProd.setProdIsLargess(prod.getProdIsLargess());
            tradeProd.setProdMinPrice(prod.getProdMinPrice());
            //重新计算商品优惠
            singleDsc = prod.getSingleDsc() / prod.getAmount() * prod.getRtnAmount();
            wholeDsc = prod.getWholeDsc() / prod.getAmount() * prod.getRtnAmount();
            vipDsc = prod.getVipDsc() / prod.getAmount() * prod.getRtnAmount();
            tranDsc = prod.getTranDsc() / prod.getAmount() * prod.getRtnAmount();
            //覆盖退货优惠信息
            tradeProd.setTranDsc(tranDsc);
            tradeProd.setVipDsc(vipDsc);
            tradeProd.setWholeDsc(wholeDsc);
            tradeProd.setSingleDsc(singleDsc);
            //插入原单信息
            tradeProd.setSaleInfo(String.format("%s %s %s", trade.getLsNo(), trade.getTradeTime(), prod.getSortNo()));
            //覆盖原销售单的实际退货金额
            tradeProd.setTotal(prod.getRtnTotal());
            //覆盖原销售单的实际退货数量
            tradeProd.setAmount(prod.getRtnAmount());
            tradeProd.insert(databaseWrapper);
        }
        long result = trade.insert(databaseWrapper);
        LogUtil.d("----insert:" + result);
        return result;
    }


    /**
     * 更新交易状态
     */
    public static void setTradeStatus(String status) {
        SQLite.update(Trade.class)
                .set(Trade_Table.status.eq(status))
                .where(Trade_Table.lsNo.is(trade.getLsNo()))
                .async()
                .execute(); // non-UI
    }

    /**
     * 保存会员信息
     */
    public static void saveVipInfo() {
        if (TradeHelper.vip != null) {
            SQLite.update(Trade.class)
                    .set(Trade_Table.vipCode.eq(TradeHelper.vip.getVipCode()),
                            Trade_Table.vipTotal.eq(TradeHelper.trade.getVipTotal()),
                            Trade_Table.custType.eq(TRADE_CUST_VIP),
                            Trade_Table.cardCode.eq(TradeHelper.vip.getCardCode()))
                    .where(Trade_Table.lsNo.is(trade.getLsNo()))
                    .async()
                    .execute(); // non-UI blocking
        }
    }

    /**
     * 上传交易流水
     */
    public static void uploadTradeQueue() {
        TradeUploadQueue queue = new TradeUploadQueue(trade.getDepCode(), trade.getLsNo());
        queue.insert();
    }

    /**
     * 清空数据库
     */
    public static void clearAllTradeData() {
        SQLite.delete(Trade.class).execute();
        SQLite.delete(TradePay.class).execute();
        SQLite.delete(TradeProd.class).execute();
    }

    /**
     * 获取购物车中未行清的所有商品列表
     *
     * @return
     */
    public static List<Map<String, Long>> getProdCountList() {
        List<Map<String, Long>> prodCountList = new ArrayList<>();
        Map<String, Long> map = new HashMap<>();
        long count;
        for (TradeProd prod : prodList) {
            if (TextUtils.isEmpty(prod.getBarCode())) {
                count = SQLite.select(count()).from(TradeProd.class)
                        .where(TradeProd_Table.lsNo.eq(trade.getLsNo()))
                        .and(TradeProd_Table.prodCode.eq(prod.getProdCode()))
                        .and(TradeProd_Table.delFlag.eq(DELFLAG_NO))
                        .count();
            } else {
                count = SQLite.select(count()).from(TradeProd.class)
                        .where(TradeProd_Table.lsNo.eq(trade.getLsNo()))
                        .and(TradeProd_Table.barCode.eq(prod.getBarCode()))
                        .and(TradeProd_Table.delFlag.eq(DELFLAG_NO))
                        .count();
            }
            map.put(prod.getProdCode(), count);
        }
        prodCountList.add(map);
        return prodCountList;
    }

    /**
     * 购物车 - 加减按钮、更改单个商品的数量
     *
     * @param index        索引序号
     * @param changeAmount 加减的数量
     * @return -1:异常  0：超过优惠  >0:成功
     */
    public static int changeAmount(final int index, final double changeAmount) {
        final int[] result = {-1};
        TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                result[0] = doChangeAmount(databaseWrapper, index, changeAmount);
                return result[0] > 0;
            }
        });
        return result[0];
    }

    private static int doChangeAmount(DatabaseWrapper databaseWrapper, int index,
                                      double changeAmount) {
        if (index < 0 || index >= prodList.size()) {
            Log.e(TAG, "改变数量: 索引无效");
            return -1;
        }
        double dsc = 0;
        for (int i = 0; i < prodList.size(); i++) {
            dsc += prodList.get(i).getManuDsc() + prodList.get(i).getVipDsc() + prodList.get(i).getTranDsc();
        }
        TradeProd tradeProd = prodList.get(index);
        // TODO 2019年10月24日13:50:44 员工权限仅限手工优惠
        if (tradeProd.getManuDsc() + tradeProd.getTranDsc() > 0) {
            if ((dsc + changeAmount * ((tradeProd.getManuDsc() + tradeProd.getTranDsc()) / tradeProd.getAmount()) > ZgParams.getCurrentUser().getMaxDscTotal())) {
                return 0;
            }
        }
        double oldAmount = tradeProd.getAmount();
        double newAmount = oldAmount + changeAmount;
        tradeProd.setVipDsc((tradeProd.getVipDsc() / oldAmount) * newAmount);
        tradeProd.setSingleDsc((tradeProd.getSingleDsc() / oldAmount) * newAmount);
        tradeProd.setWholeDsc((tradeProd.getWholeDsc() / oldAmount) * newAmount);
        tradeProd.setTranDsc((tradeProd.getTranDsc() / oldAmount) * newAmount);

        tradeProd.setAmount(newAmount);
        tradeProd.setTotal(priceFormat((newAmount * tradeProd.getPrice()) - tradeProd.getTotalDsc()));
        if (tradeProd.save(databaseWrapper)) {
            if (recalcTotal(databaseWrapper)) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    /**
     * 购物车 - 当前购物车内商品总件数,需要Amount的和
     *
     * @return
     */
    public static double getTradeCount() {
        double count = 0;
        for (int i = 0; i < prodList.size(); i++) {
            count += prodList.get(i).getAmount();
        }
        return count;
    }


    /**
     * @return 获取商品原价
     */
    public static double getTradePrice() {
        double price = 0;
        for (TradeProd prod : prodList) {
            price += prod.getPrice() * prod.getAmount();
        }
        return price;
    }

    /**
     * 购物车 - 当前购物车内商品总金额
     * 优惠后的价钱
     *
     * @return 当前购物车内商品总金额
     */
    public static double getTradeTotal() {
        double total = 0.00;
        for (int i = 0; i < prodList.size(); i++) {
            total += prodList.get(i).getTotal();
        }
        return total;
    }

    public static double getProdTotal() {
        double total = 0.00;
        for (int i = 0; i < prodList.size(); i++) {
            total += prodList.get(i).getPrice() * prodList.get(i).getAmount();
        }
        return total;
    }

    /**
     * 该商品是否有改价权
     *
     * @param barCode 根据条码
     * @return 是或否
     */
    public static boolean getPriceFlagByBarCode(String barCode) {
        int result = 0;
        FlowCursor csr = SQLite.select(DepProduct_Table.priceFlag).from(DepProduct.class)
                .where(DepProduct_Table.barCode.eq(barCode)).query();
        if (csr.moveToFirst()) {
            do {
                result = csr.getIntOrDefault(0);
            } while (csr.moveToNext());
        }
        return result != 0;
    }

    /**
     * 该商品是否有改价权
     *
     * @param prodCode 根据编码
     * @return 是或否
     */
    public static boolean getPriceFlagByProdCode(String prodCode) {
        int result = 0;
        FlowCursor csr = SQLite.select(DepProduct_Table.priceFlag).from(DepProduct.class)
                .where(DepProduct_Table.prodCode.eq(prodCode)).query();
        if (csr.moveToFirst()) {
            do {
                result = csr.getIntOrDefault(0);
            } while (csr.moveToNext());
        }
        return result != 0;
    }

    /**
     * 购物车界面改价
     *
     * @param index 商品索引
     * @param price 价格
     * @return 是否成功
     */
    public static boolean priceChangeInShopList(final int index, final double price) {
        return TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                return doPriceChangeInShopList(databaseWrapper, index, price);
            }
        });
    }

    private static boolean doPriceChangeInShopList(DatabaseWrapper databaseWrapper,
                                                   int index, double price) {
        if (price < 0) {
            Log.e(TAG, "改价: 价格无效");
            return false;
        }

        TradeProd tradeProd = prodList.get(index);
        if (tradeProd != null) {
            tradeProd.setPrice(priceFormat(price));
            //改价把手动优惠+会员优惠清掉
            tradeProd.setSingleDsc(0);
            tradeProd.setWholeDsc(0);
            tradeProd.setVipDsc(0);
            tradeProd.setTotal(priceFormat(tradeProd.getAmount() * price));
        }
        if (tradeProd.save(databaseWrapper)) {
            return recalcTotal(databaseWrapper);
        } else {
            return false;
        }
    }

    /**
     * 选择商品界面改价
     *
     * @param price 价格
     * @return 是否成功
     */
    public static boolean priceChangeInShopCart(final double price) {
        return TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                return doPriceChangeInShopCart(databaseWrapper, price);
            }
        });
    }

    private static boolean doPriceChangeInShopCart(DatabaseWrapper databaseWrapper, double price) {
        if (price < 0) {
            Log.e(TAG, "改价: 价格无效");
            return false;
        }
        TradeProd tradeProd = prodList.get(prodList.size() - 1);
        if (tradeProd != null) {
            tradeProd.setPrice(priceFormat(price));
            tradeProd.setTotal(priceFormat(tradeProd.getAmount() * price));
        }
        if (tradeProd.save(databaseWrapper)) {
            return recalcTotal(databaseWrapper);
        } else {
            return false;
        }
    }

    /**
     * 选择商品界面改价撤销
     */
    public static void rollackPriceChangeInShopCart() {
        TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                doRollackPriceChangeInShopCart(databaseWrapper);
                return true;
            }
        });
    }

    private static boolean doRollackPriceChangeInShopCart(DatabaseWrapper databaseWrapper) {
        TradeProd tradeProd = prodList.get(prodList.size() - 1);
        if (tradeProd != null) {
            if (tradeProd.delete(databaseWrapper)) {
                prodList.remove(tradeProd);
            } else {
                return false;
            }
        }
        return recalcTotal(databaseWrapper);
    }

    /**
     * 转换交易状态
     *
     * @param status 状态码
     * @return 提示文本
     */
    public static String convertTradeStatus(String status) {
        switch (status) {
            case TradeHelper.TRADE_STATUS_CANCELLED:
                return "交易已取消";
            case TradeHelper.TRADE_STATUS_HANGUP:
                return "挂单成功";
            case TradeHelper.TRADE_STATUS_NOTPAY:
                return "尚未支付";
            case TradeHelper.TRADE_STATUS_PAID:
                return "支付成功";
            default:
                return "交易异常";
        }
    }

    /**
     * 选择商品界面：获取每个商品的件数
     *
     * @param prodCode 商品码
     * @param barCode  条码（可能为null）
     * @return 数量
     */
    public static long getProdCount(String prodCode, String barCode) {
        if (TextUtils.isEmpty(barCode)) {
            return SQLite.select(count()).from(TradeProd.class)
                    .where(TradeProd_Table.lsNo.eq(trade.getLsNo()))
                    .and(TradeProd_Table.prodCode.eq(prodCode))
                    .and(TradeProd_Table.delFlag.eq(DELFLAG_NO))
                    .count();
        } else {
            return SQLite.select(count()).from(TradeProd.class)
                    .where(TradeProd_Table.lsNo.eq(trade.getLsNo()))
                    .and(TradeProd_Table.barCode.eq(barCode))
                    .and(TradeProd_Table.delFlag.eq(DELFLAG_NO))
                    .count();
        }
    }

    /**
     * 初始化会员信息
     *
     * @return
     */
    public static VipInfo vip() {
        if (vip == null) {
            vip = new VipInfo();
        }
        return vip;
    }

    /**
     * 清空会员信息
     */
    public static void clearVip() {
        vip = null;
    }

    /**
     * 流水保存vip信息
     *
     * @return
     */
    public static boolean saveVip() {
        if (vip != null) {
            trade.setVipCode(vip.getVipCode());
            trade.setCustType("2");
            trade.setCardCode(vip.getCardCode());
        }
        return trade.save();
    }

    /**
     * 离线模式下只保存vipCode
     *
     * @param vipCode 会员号
     * @return 成功失败
     */
    public static boolean saveVipCodeOffline(String vipCode) {
        trade.setVipCode(vipCode);
        return trade.save();
    }

    /**
     * @return 挂单交易总数
     */
    public static long getHangUpCount() {
        long count = 0;
        count = SQLite.select(count()).from(Trade.class)
                .where(Trade_Table.status.eq(TRADE_STATUS_HANGUP))
                .and(Trade_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                .count();
        return count;
    }

    /**
     * 获取本机内所有挂起的流水
     *
     * @return
     */
    public static List<Trade> getOutOrder() {
        List<Trade> tradeList = SQLite.select().distinct().from(Trade.class)
                .where(Trade_Table.status.eq(TRADE_STATUS_HANGUP))
                .and(Trade_Table.tradeFlag.eq(TRADE_FLAG_SALE))
                .and(Trade_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode())).queryList();

        for (Trade trade : tradeList) {
            TradeProd tradeProd = SQLite.select().from(TradeProd.class)
                    .where(TradeProd_Table.lsNo.eq(trade.getLsNo()))
                    .limit(1)
                    .querySingle();
            if (tradeProd == null) {
                break;
            }
            trade.setProdName(tradeProd.getProdName());
            trade.setProdNum(tradeProd.getAmount());

            FlowCursor csr = SQLite.select(sum(TradeProd_Table.amount)).from(TradeProd.class)
                    .where(TradeProd_Table.lsNo.eq(trade.getLsNo()))
                    .query();
            if (csr.moveToFirst()) {
                do {
                    trade.setAmount(csr.getDoubleOrDefault(0));
                } while (csr.moveToNext());
            }
        }
        return tradeList;
    }

    /**
     * 取单
     *
     * @param lsNo 流水号
     * @return
     */
    public static boolean orderOut(String lsNo) {
        // 验证购物车是否为空
        if (!cartIsEmpty()) {
            Log.e(TAG, "取单失败：购物车不为空");
            return false;
        }

        Trade trade = SQLite.select().from(Trade.class)
                .where(Trade_Table.lsNo.eq(lsNo))
                .and(Trade_Table.tradeFlag.eq(TRADE_FLAG_SALE))
                .and(Trade_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                .querySingle();
        // 验证流水状态为：挂起
        if (trade == null || !TRADE_STATUS_HANGUP.equals(trade.getStatus())) {
            Log.e(TAG, "取单失败：流水号无效或流水不是挂起状态");
            return false;
        }
        // 修改流水状态为：未结
        trade.setStatus(TRADE_STATUS_NOTPAY);
        if (trade.update()) {
            // 取单成功，自动读取购物车流水
            initSale();
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return 取单数量
     */
    public static boolean outOrderCount() {
        long count = SQLite.select(count()).from(Trade.class)
                .where(Trade_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                .and(Trade_Table.status.eq(TRADE_STATUS_HANGUP))
                .count();
        return count != 0;
    }

    /**
     * @return 可登录专柜
     */
    public static List<Dep> getDepList() {
        return SQLite.select().from(Dep.class).queryList();
    }

    /**
     * @return 可登录用户
     */
    public static List<User> getUserList() {
        return SQLite.select().from(User.class).queryList();
    }

    /**
     * 获取参数字段
     *
     * @param paramName 字段名
     * @return 字段值
     */
    public static String getAppParamValue(String paramName) {
        return SQLite.select().from(AppParams.class).where(AppParams_Table.paramName.eq(paramName))
                .querySingle().getParamValue();
    }

    /**
     * 根据编码条码获取商品的单位
     *
     * @param prodCode 商品码
     * @param barCode  条码
     * @return 单位
     */
    public static String getProdUnit(String prodCode, String barCode) {
        String unit = "";
        if (TextUtils.isEmpty(barCode)) {
            unit = SQLite.select(DepProduct_Table.unit)
                    .from(DepProduct.class)
                    .where(DepProduct_Table.prodCode.eq(prodCode))
                    .querySingle()
                    .getUnit();
        } else {
            unit = SQLite.select(DepProduct_Table.unit)
                    .from(DepProduct.class)
                    .where(DepProduct_Table.barCode.eq(barCode))
                    .querySingle()
                    .getUnit();
        }
        return unit;
    }

    /**
     * 判断购物车是否为空
     *
     * @return
     */
    public static boolean cartIsEmpty() {
        return getCartLs() == null;
    }

    /**
     * 初始化数据时点击取消此时清空数据
     */
    public static void rollbackInitTask() {
        SQLite.delete(User.class).execute();
        SQLite.delete(Dep.class).execute();
        SQLite.delete(DepCls.class).execute();
        SQLite.delete(DepProduct.class).execute();
        SQLite.delete(DepPayInfo.class).execute();
        SQLite.delete(SysParams.class).execute();
        clearAllTradeData();
    }


    /**
     * 价格格式化
     *
     * @param before 格式化前的价格
     * @return 保留小数点后两位
     */
    public static double priceFormat(double before) {
        return Double.parseDouble(String.format("%.2f", before));
    }

    /**
     * 金额正则表达式
     *
     * @param price 价格
     * @return 是或否
     */
    public static boolean checkPriceFormat(Object price) {
//        String match = "　^[0-9]+(.[0-9]{2})?$";
        String match = "(?!^0*(\\.0{1,2})?$)^\\d{1,13}(\\.\\d{1,2})?$";

        return String.valueOf(price).matches(match);
    }

    /**
     * 非负整数正则表达式
     *
     * @param rate 折扣率
     * @return 是或否
     */
    public static boolean checkRateFormat(Object rate) {
        String match = "^[1-9]\\d*|0$";
        return String.valueOf(rate).matches(match);
    }

    /**
     * @param userCode 用户编码
     * @return 用户名
     */
    public static String getCashierByUserCode(String userCode) {
        try {
            return SQLite.select(User_Table.userName).from(User.class)
                    .where(User_Table.userCode.eq(userCode))
                    .querySingle().getUserName();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 手机号正则表达式
     *
     * @param phone
     * @return 是或否
     */
    public static boolean checkPhoneNoFormat(Object phone) {
        String match = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
        return String.valueOf(phone).matches(match);
    }

    /**
     * @param payType 支付方式
     * @return 支付方式
     */
    public static String convertAppPayType(String payType) {
        LogUtil.d("----payType:" + payType);
        switch (payType) {
            case "1":
                return "现金";
            case "2":
                return "支付宝";
            case "3":
                return "微信支付";
            case "4":
                return "储值卡";
            case "5":
                return "收钱吧";
            default:
                return "未知方式";
        }
    }
}
