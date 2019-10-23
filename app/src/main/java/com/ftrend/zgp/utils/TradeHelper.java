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
import com.ftrend.zgp.model.VipInfo;
import com.ftrend.zgp.utils.db.TransHelper;
import com.ftrend.zgp.utils.db.ZgpDb;
import com.ftrend.zgp.utils.pay.PayType;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import java.util.ArrayList;
import java.util.Arrays;
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

    // VIP强制优惠：1-强制优惠，无视商品的forDsc属性
    public static final String VIP_DSC_FORCE = "1";
    // VIP强制优惠：0-不强制
    public static final String VIP_DSC_NORMAL = "0";

    // 超市版：会员优惠规则-1
    public static final int VIP_ONE = 1;
    // 超市版：会员优惠规则-2
    public static final int VIP_TWO = 2;
    // 超市版：会员优惠规则-3
    public static final int VIP_THREE = 3;
    // 顾客类型：2-会员
    public static final String TRADE_CUST_VIP = "2";

    // 交易流水
    private static Trade trade = null;
    // 商品列表
    private static List<TradeProd> prodList = null;

    // 支付信息
    private static TradePay pay = null;
    // 会员信息
    public static VipInfo vip = null;

    public static Trade getTrade() {
        return trade;
    }

    public static TradePay getPay() {
        return pay;
    }

    public static List<TradeProd> getProdList() {
        return prodList;
    }

    //region clear----清空当前交易信息

    /**
     * 清空当前交易信息
     */
    public static void clear() {
        trade = null;
        prodList = null;
        pay = null;
    }
    //endregion

    //region initSale----初始化当前操作流水

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
     */
    public static void initSale() {
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
        } else {
            prodList = SQLite.select().from(TradeProd.class)
                    .where(TradeProd_Table.lsNo.eq(trade.getLsNo()))
                    .and(TradeProd_Table.delFlag.eq(DELFLAG_NO))
                    .queryList();
            pay = SQLite.select().from(TradePay.class)
                    .where(TradePay_Table.lsNo.eq(trade.getLsNo()))
                    .querySingle();
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
     * @param lsNo
     * @return 是否有该流水
     */
    public static boolean initSale(String lsNo) {
        trade = getPaidLs(lsNo);
        if (trade == null) {
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
    //endregion

    //region addProduct----添加到商品表

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
    //endregion

    //region delProduct----行清

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

    //region pay----完成支付

    /**
     * 完成支付
     *
     * @param appPayType APP支付类型
     * @param amount     支付金额
     * @param change     找零金额
     * @param payCode    支付账号
     * @return 支付结果
     */
    public static boolean pay(final String appPayType, final double amount, final double change, final String payCode) {
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
     * @param value 实际支付金额，为0时全额支付
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
//        FlowManager.getDatabaseForTable(Trade_Table.class).getWritableDatabase();
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

    private static int doChangeAmount(DatabaseWrapper databaseWrapper, int index, double changeAmount) {
        if (index < 0 || index >= prodList.size()) {
            Log.e(TAG, "改变数量: 索引无效");
            return -1;
        }
        double dsc = 0;
        for (int i = 0; i < prodList.size(); i++) {
            dsc += prodList.get(i).getManuDsc() + prodList.get(i).getVipDsc() + prodList.get(i).getTranDsc();
        }
        TradeProd tradeProd = prodList.get(index);
        if (tradeProd.getManuDsc() + tradeProd.getTranDsc() + tradeProd.getVipDsc() > 0) {
            if ((dsc + changeAmount * ((tradeProd.getManuDsc() + tradeProd.getVipDsc() + tradeProd.getTranDsc()) / tradeProd.getAmount()) > ZgParams.getCurrentUser().getMaxDscTotal())) {
                return 0;
            }
        }
        double oldAmount = tradeProd.getAmount();
        double newAmount = oldAmount + changeAmount;
        tradeProd.setVipDsc((tradeProd.getVipDsc() / oldAmount) * newAmount);
        tradeProd.setSingleDsc((tradeProd.getSingleDsc() / oldAmount) * newAmount);
        tradeProd.setWholeDsc((tradeProd.getWholeDsc() / oldAmount) * newAmount);
        tradeProd.setManuDsc(tradeProd.getSingleDsc() + tradeProd.getWholeDsc());
        tradeProd.setTranDsc((tradeProd.getTranDsc() / oldAmount) * newAmount);

        tradeProd.setAmount(newAmount);
        tradeProd.setTotal(priceFormat((newAmount * tradeProd.getPrice()) - tradeProd.getManuDsc() - tradeProd.getVipDsc() - tradeProd.getTranDsc()));
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
            recalcTotal(databaseWrapper);
            return true;
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

    private static void doRollackPriceChangeInShopCart(DatabaseWrapper databaseWrapper) {
        TradeProd tradeProd = prodList.get(prodList.size() - 1);
        if (tradeProd != null) {
            if (tradeProd.delete(databaseWrapper)) {
                prodList.remove(tradeProd);
            }
        }
        recalcTotal(databaseWrapper);
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
     * 检查商品优惠权限
     *
     * @param index 索引
     * @return 是或否
     */
    public static boolean checkForDsc(int index) {
        if (index < 0 || index >= prodList.size()) {
            Log.e(TAG, "行清: 索引无效");
            return false;
        }
        int forDsc;
        if (TextUtils.isEmpty(prodList.get(index).getBarCode())) {
            //根据prodCode查商品优惠限制
            forDsc = SQLite.select().from(DepProduct.class)
                    .where(DepProduct_Table.barCode.eq(prodList.get(index).getBarCode()))
                    .querySingle().getForDsc();
        } else {
            //根据barCode查商品优惠限制
            forDsc = SQLite.select().from(DepProduct.class)
                    .where(DepProduct_Table.prodCode.eq(prodList.get(index).getProdCode()))
                    .querySingle().getForDsc();
        }
        return forDsc != 0;
    }

    /**
     * 检查商品优惠权限且无单项优惠
     *
     * @param index 索引
     * @return 是或否
     */
    public static boolean checkForDscAndNoDsc(int index) {
        if (index < 0 || index >= prodList.size()) {
            Log.e(TAG, "行清: 索引无效");
            return false;
        }
        int forDsc;
        double singleDsc, vipDsc, tranDsc;
        if (TextUtils.isEmpty(prodList.get(index).getBarCode())) {
            //根据prodCode查商品优惠限制
            forDsc = SQLite.select().from(DepProduct.class)
                    .where(DepProduct_Table.barCode.eq(prodList.get(index).getBarCode()))
                    .querySingle().getForDsc();
        } else {
            //根据barCode查商品优惠限制
            forDsc = SQLite.select().from(DepProduct.class)
                    .where(DepProduct_Table.prodCode.eq(prodList.get(index).getProdCode()))
                    .querySingle().getForDsc();
        }
        singleDsc = prodList.get(index).getSingleDsc();
        vipDsc = prodList.get(index).getVipDsc();
        tranDsc = prodList.get(index).getTranDsc();
        //forDsc:0否1是
        return (forDsc != 0) && (singleDsc == 0) && (vipDsc == 0) && (tranDsc == 0);
    }

    /**
     * @return 单项优惠
     */
    public static double getSingleDsc(int index, int rate) {
        return ((double) rate / 100) * prodList.get(index).getPrice();
    }

    /**
     * @return 单项折扣率
     */
    public static long getSingleRate(int index, double singleDsc) {
        return Math.round((singleDsc / prodList.get(index).getPrice()) * 100);
    }

    /**
     * @return 单项优惠价
     */
    public static double getSingleTotal(int index, double singleDsc) {
        return prodList.get(index).getPrice() - singleDsc;
    }


    /**
     * 获取单项优惠金额上限
     * 取三值的最小值
     *
     * @return 单项优惠上限
     */
    public static double getMaxSingleDsc(int index) {
        double firstDsc, secondDsc, thirdDsc;
        String prodCode = prodList.get(index).getProdCode();
        String barCode = prodList.get(index).getBarCode();
        int maxDscRate = ZgParams.getCurrentUser().getMaxDscRate();
        double wholePrice = 0, wholeDsc = 0;
        if (TextUtils.isEmpty(barCode)) {
            //原价-最低限价
            firstDsc = SQLite.select(DepProduct_Table.minimumPrice).from(DepProduct.class)
                    .where(DepProduct_Table.prodCode.eq(prodCode))
                    .querySingle().getMinimumPrice();
            firstDsc = prodList.get(index).getPrice() - firstDsc;
        } else {
            //原价-最低限价
            firstDsc = SQLite.select(DepProduct_Table.minimumPrice).from(DepProduct.class)
                    .where(DepProduct_Table.barCode.eq(barCode))
                    .querySingle().getMinimumPrice();
            firstDsc = prodList.get(index).getPrice() - firstDsc;
        }
        //整单金额 - 整单已优惠金额 - （整单金额 × 最大优惠折扣MaxDscRate）
        for (int i = 0; i < prodList.size(); i++) {
            if (i != index) {
                wholeDsc += (prodList.get(i).getManuDsc() + prodList.get(i).getVipDsc()
                        + prodList.get(i).getTranDsc()) * prodList.get(i).getAmount();
            }
            wholePrice += prodList.get(i).getPrice() * prodList.get(i).getAmount();
        }
        secondDsc = (wholePrice * maxDscRate > ZgParams.getCurrentUser().getMaxDscTotal() ?
                ZgParams.getCurrentUser().getMaxDscTotal() : wholePrice * maxDscRate) / prodList.get(index).getAmount();
        thirdDsc = ZgParams.getCurrentUser().getMaxDscTotal() - wholeDsc >= 0 ?
                (ZgParams.getCurrentUser().getMaxDscTotal() - wholeDsc) / prodList.get(index).getAmount() : 0;
//        secondDsc = wholePrice - wholeDsc - (wholePrice * (maxDscRate / 100));
//        LogUtil.d("----second:" + secondDsc);
//        //整单金额 - 整单已优惠金额 - 单笔最大优惠金额MaxDscTotal，第三个参数需要计算
//        thirdDsc = wholePrice - wholeDsc - ZgParams.getCurrentUser().getMaxDscTotal();
//        LogUtil.d("----third:" + wholePrice + "/" + wholeDsc+"/"+thirdDsc);
        double[] dsc = {firstDsc, secondDsc, thirdDsc};
        Arrays.sort(dsc);
        return dsc[0] < 0 ? dsc[1] : dsc[0];
    }

    /**
     * 获取单项优惠折扣上限
     *
     * @param index
     * @return
     */
    public static long getMaxSingleRate(int index) {
        long maxRate = Math.round((getMaxSingleDsc(index) / prodList.get(index).getPrice()) * 100);
        return maxRate;
    }

    /**
     * 本笔交易原价
     *
     * @return
     */
    public static double getWholePrice() {
        double price = 0;
        for (TradeProd prod : prodList) {
            price += prod.getPrice() * prod.getAmount();
        }
        return price;
    }

    /**
     * 整单优惠：获取可整单优惠的所有商品的原价
     *
     * @return
     */
    public static double getWholeForDscPrice() {
        double price = 0;
        int i = 0;
        for (TradeProd prod : prodList) {
            if (checkForDsc(i) && prod.getSingleDsc() == 0 && prod.getVipDsc() == 0 && prod.getTranDsc() == 0) {
                price += prod.getPrice() * prod.getAmount();
            }
            i++;
        }
        return price;
    }

    /**
     * 收款员当前可以优惠的最大金额：最大上限-已有优惠
     *
     * @return 当前最大折扣金额
     */
    public static double getMaxWholeDsc() {
        double dscTotal = 0;
        double mPrice = 0;
        //按照系统折扣设置该收款员在交易中最多优惠金额
        //商品原价*折扣率
        double maxDscTotal = getWholePrice() * getUserMaxDscRate() / 100;
        //这个值与设置最大金额谁大
        maxDscTotal = maxDscTotal >= ZgParams.getCurrentUser().getMaxDscTotal() ? ZgParams.getCurrentUser().getMaxDscTotal() : maxDscTotal;
        //当前商品的全部优惠
        for (TradeProd prod : prodList) {
            //TODO amount
            dscTotal += (prod.getVipDsc() + prod.getTranDsc() + prod.getSingleDsc());
            mPrice = (prod.getProdForDsc() != 1) ? 0 : (prod.getPrice() - prod.getProdMinPrice()) * prod.getAmount();
        }
        //最大金额减去目前所有的优惠，即可优惠的金额
        dscTotal = maxDscTotal - dscTotal >= 0 ? ((maxDscTotal - dscTotal) > mPrice ? mPrice : maxDscTotal - dscTotal) : 0;
        return dscTotal;
    }


    /**
     * 整单优惠最大折扣
     *
     * @return
     */
    public static long getMaxWholeRate() {
        return Math.round(getMaxWholeDsc() / DscHelper.getAfterWholePrice() * 100);
    }

    /**
     * 界面mTotalTv展示金额
     * 获取整单优惠后的当前总金额
     *
     * @param dsc
     * @return
     */
    public static double getWholeTotal(double dsc) {
        return priceFormat(DscHelper.getAfterWholePrice() - dsc);
    }

    /**
     * 界面mTotalTv展示金额
     * 获取整单优惠后的当前总金额
     *
     * @param rate
     * @return
     */
    public static double getWholeTotal(int rate) {
        return priceFormat(DscHelper.getAfterWholePrice() - getWholeDsc(rate));
    }

    /**
     * 界面mDscTv展示金额
     * 获取整单优惠金额
     *
     * @param rate
     * @return
     */
    public static double getWholeDsc(int rate) {
        return priceFormat(DscHelper.getAfterWholePrice() * rate / 100);
    }

    /**
     * 界面mRateEdt展示金额
     * 获取当前整单优惠折扣率
     *
     * @param wholeDsc
     * @return
     */
    public static long getWholeRate(double wholeDsc) {
        return Math.round((wholeDsc / DscHelper.getAfterWholePrice()) * 100);
    }

    /**
     * 系统参数：收款员最大折扣率
     *
     * @return 当前用户最大折扣率
     */
    public static int getUserMaxDscRate() {
        return ZgParams.getCurrentUser().getMaxDscRate();
    }


    /**
     * 保存单项优惠
     *
     * @param index
     * @param singleDsc
     * @return
     */
    public static boolean saveSingleDsc(final int index, final double singleDsc) {
        return TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                return doSaveSingleDsc(databaseWrapper, index, singleDsc);
            }
        });
    }

    private static boolean doSaveSingleDsc(DatabaseWrapper databaseWrapper,
                                           int index, double singleDsc) {
        if (index < 0 || index >= prodList.size()) {
            Log.e(TAG, "行清: 索引无效");
            return false;
        }
        TradeProd prod = prodList.get(index);
        //TODO AMOUNT
        prod.setSingleDsc(priceFormat(singleDsc * prod.getAmount()));
        //单项优惠的时候，清空整单优惠
        prod.setWholeDsc(0);
        prod.setVipTotal(0);
        prod.setTotal(priceFormat(prod.getPrice() * prod.getAmount() - prod.getManuDsc() - prod.getVipDsc() - prod.getTranDsc()));
        if (prod.save(databaseWrapper)) {
            return recalcTotal(databaseWrapper);
        }
        return false;
    }


    /**
     * 保存整项优惠
     *
     * @param wholeDsc 输入的整单优惠金额
     * @return
     */
    @Deprecated
    public static boolean saveWholeDsc(double wholeDsc) {
        //优惠金额=商品总价*折扣率
        //优惠金额不能大于：商品原价-最低限价
        //总的优惠金额不能大于单笔最大优惠金额（能执行到本方法时，已经排除大于最大优惠金额）
        //需要筛选出来可以分摊的商品
        double dsc = 0, minumPrice, rate;
        //本折扣率为实际折扣率，非界面显示折扣率
        double maxDsc = getMaxWholeDsc();
        double price = 0;
        //region 需要筛选出来可以分摊且没有单项优惠\会员优惠\促销优惠的商品
        List<TradeProd> tempList = new ArrayList<>();
        for (int i = 0; i < prodList.size(); i++) {
            if (checkForDscAndNoDsc(i)) {
                tempList.add(prodList.get(i));
            }
        }
        //endregion
        //可优惠商品的总金额
        for (TradeProd prod : tempList) {
            price += prod.getPrice() * prod.getAmount();
        }
        rate = wholeDsc / price;
        //可优惠金额是否比最大可优惠金额大
        wholeDsc = wholeDsc >= maxDsc ? maxDsc : wholeDsc;
        //计数变量
        int i = 0;
        //给每个商品按照比例分摊优惠的金额
        for (TradeProd prod : tempList) {
            //取商品的最低限价
            if (TextUtils.isEmpty(prod.getBarCode())) {
                minumPrice = SQLite.select(DepProduct_Table.minimumPrice).from(DepProduct.class)
                        .where(DepProduct_Table.prodCode.eq(prod.getProdCode()))
                        .querySingle().getMinimumPrice();
            } else {
                minumPrice = SQLite.select(DepProduct_Table.minimumPrice).from(DepProduct.class)
                        .where(DepProduct_Table.barCode.eq(prod.getBarCode()))
                        .querySingle().getMinimumPrice();
            }
            if (maxDsc > 0) {
                dsc = prod.getPrice() * rate;
                //优惠金额与最低限价相比，超过最低限价则取最低限价
                dsc = dsc >= prod.getPrice() - minumPrice ? prod.getPrice() - minumPrice : dsc;
                dsc = i == tempList.size() - 1 ? wholeDsc : dsc;
                //如果会员优惠比整单优惠金额大，那么跳过本商品
                //整单优惠为0，保留会员优惠
                if (prod.getVipDsc() > dsc) {
                    dsc = 0;
                } else {
                    prod.setVipDsc(0);
                }
                prod.setWholeDsc(dsc * prod.getAmount());
                //整单优惠的时候，单项优惠清零
                prod.setSingleDsc(0);
                prod.setTotal(priceFormat(prod.getPrice() * prod.getAmount() - prod.getManuDsc() - prod.getVipDsc() - prod.getTranDsc()));
            }
            if (prod.save()) {
                wholeDsc = wholeDsc - dsc < 0 ? 0 : wholeDsc - dsc;
            }
            i++;
        }
        //重新汇总
        return recalcTotal();
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
     * 保存会员优惠
     *
     * @return
     */
    public static boolean saveVipDsc() {
        return TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                return doSaveVipDsc(databaseWrapper);
            }
        });
    }

    private static boolean doSaveVipDsc(DatabaseWrapper databaseWrapper) {
        //筛选没有单项优惠、整项优惠的商品
        List<TradeProd> tempList = new ArrayList<>();
        if (VIP_DSC_FORCE.equals(vip.getForceDsc())) {
            //强制打折
            for (TradeProd prod : prodList) {
                if (prod.getManuDsc() == 0) {
                    tempList.add(prod);
                }
            }
        } else {
            //不强制打折
            for (int i = 0; i < prodList.size(); i++) {
                if (checkForDsc(i)) {
                    if (prodList.get(i).getManuDsc() == 0) {
                        tempList.add(prodList.get(i));
                    }
                }
            }
        }
        //区分版本优惠
        double vipPriceType = vip.getVipPriceType();
        if (ZgParams.getProgramEdition().equals(ZgParams.PROG_EDITION_BH)) {
            //百货版
            double rate = vip.getVipDscRate();
            double vipDsc, rateDsc;
            for (TradeProd prod : tempList) {
                rateDsc = prod.getPrice() * (100 - rate) / 100;
                vipDsc = prod.getPrice() - queryVipPrice(vipPriceType, prod);
                //TODO AMOUNT
                prod.setVipDsc(Math.max(rateDsc, vipDsc) * prod.getAmount());
                prod.setWholeDsc(0);
                prod.setSingleDsc(0);
                //已存在手工优惠的不参与会员优惠
                prod.setTotal(priceFormat(prod.getPrice() * prod.getAmount() - prod.getManuDsc() - prod.getVipDsc() - prod.getTranDsc()));
                prod.save(databaseWrapper);
            }
        } else {
            //超市版
            double rateRule = vip.getRateRule();
            double vipDsc, rateDsc;
            for (TradeProd prod : tempList) {
                //如果没有会员规则，rateRule=0;
                rateRule = queryRateRule(rateRule, prod);
                rateDsc = prod.getPrice() * (100 - rateRule) / 100;
                //如果没有会员价，vipPrice = prod.getPrice()商品原价
                vipDsc = prod.getPrice() - queryVipPrice(vipPriceType, prod);
                prod.setVipDsc(Math.max(rateDsc, vipDsc) * prod.getAmount());
                prod.setWholeDsc(0);
                prod.setSingleDsc(0);
                //已存在手工优惠的不参与会员优惠
                prod.setTotal(priceFormat(prod.getPrice() * prod.getAmount() - prod.getManuDsc() - prod.getVipDsc() - prod.getTranDsc()));
                prod.save(databaseWrapper);
            }
        }
        return recalcTotal(databaseWrapper);
    }

    private static double queryVipPrice(double vipPriceType, TradeProd prod) {
        double vipPrice = prod.getPrice();
        if (TextUtils.isEmpty(prod.getBarCode())) {
            if (vipPriceType == VIP_ONE) {
                vipPrice = SQLite.select(DepProduct_Table.vipPrice1).from(DepProduct.class)
                        .where(DepProduct_Table.prodCode.eq(prod.getProdCode()))
                        .querySingle().getVipPrice1();
            } else if (vipPriceType == VIP_TWO) {
                vipPrice = SQLite.select(DepProduct_Table.vipPrice2).from(DepProduct.class)
                        .where(DepProduct_Table.prodCode.eq(prod.getProdCode()))
                        .querySingle().getVipPrice2();
            } else if (vipPriceType == VIP_THREE) {
                vipPrice = SQLite.select(DepProduct_Table.vipPrice3).from(DepProduct.class)
                        .where(DepProduct_Table.prodCode.eq(prod.getProdCode()))
                        .querySingle().getVipPrice3();
            }
        } else {
            if (vipPriceType == VIP_ONE) {
                vipPrice = SQLite.select(DepProduct_Table.vipPrice1).from(DepProduct.class)
                        .where(DepProduct_Table.barCode.eq(prod.getBarCode()))
                        .querySingle().getVipPrice1();
            } else if (vipPriceType == VIP_TWO) {
                vipPrice = SQLite.select(DepProduct_Table.vipPrice2).from(DepProduct.class)
                        .where(DepProduct_Table.barCode.eq(prod.getBarCode()))
                        .querySingle().getVipPrice2();
            } else if (vipPriceType == VIP_THREE) {
                vipPrice = SQLite.select(DepProduct_Table.vipPrice3).from(DepProduct.class)
                        .where(DepProduct_Table.barCode.eq(prod.getBarCode()))
                        .querySingle().getVipPrice3();
            }
        }
        return vipPrice;
    }


    /**
     * @param rateRule 折扣规则
     * @param prod     商品信息
     * @return 折扣
     */
    private static double queryRateRule(double rateRule, TradeProd prod) {
        double rate = 0;
        if (TextUtils.isEmpty(prod.getBarCode())) {
            if (rateRule == VIP_ONE) {
                rate = SQLite.select(DepProduct_Table.vipRate1).from(DepProduct.class)
                        .where(DepProduct_Table.prodCode.eq(prod.getProdCode()))
                        .querySingle().getVipRate1();
            } else if (rateRule == VIP_TWO) {
                rate = SQLite.select(DepProduct_Table.vipRate2).from(DepProduct.class)
                        .where(DepProduct_Table.prodCode.eq(prod.getProdCode()))
                        .querySingle().getVipRate2();
            } else if (rateRule == VIP_THREE) {
                rate = SQLite.select(DepProduct_Table.vipRate3).from(DepProduct.class)
                        .where(DepProduct_Table.prodCode.eq(prod.getProdCode()))
                        .querySingle().getVipRate3();
            }
        } else {
            if (rateRule == VIP_ONE) {
                rate = SQLite.select(DepProduct_Table.vipRate1).from(DepProduct.class)
                        .where(DepProduct_Table.barCode.eq(prod.getBarCode()))
                        .querySingle().getVipRate1();
            } else if (rateRule == VIP_TWO) {
                rate = SQLite.select(DepProduct_Table.vipRate1).from(DepProduct.class)
                        .where(DepProduct_Table.barCode.eq(prod.getBarCode()))
                        .querySingle().getVipRate2();
            } else if (rateRule == VIP_THREE) {
                rate = SQLite.select(DepProduct_Table.vipRate3).from(DepProduct.class)
                        .where(DepProduct_Table.barCode.eq(prod.getBarCode()))
                        .querySingle().getVipRate3();
            }
        }
        return rate;
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
     * 根据流水号查询商品明细
     *
     * @param lsNo 流水号
     * @return 商品明细
     */
    public static List<TradeProd> getProdListByLsNo(String lsNo) {
        return SQLite.select().from(TradeProd.class)
                .where(TradeProd_Table.lsNo.eq(lsNo))
                .queryList();
    }

    /**
     * 根据流水单号查询交易记录
     *
     * @param lsNo 流水单号
     * @return 交易流水
     */
    public static Trade getTradeByLsNo(String lsNo) {
        return SQLite.select().from(Trade.class)
                .where(Trade_Table.lsNo.eq(lsNo))
                .and(Trade_Table.status.eq(TRADE_STATUS_PAID))
                .querySingle();
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
     * 折扣率格式化
     *
     * @param total 优惠后总金额
     * @param price 原价
     * @return 保留小数点后两位
     */
    public static long rateFormat(double total, double price) {
        return Math.round((total / price) * 100);
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
        switch (payType) {
            case "0":
                return "现金";
            case "1":
                return "支付宝";
            case "2":
                return "微信支付";
            case "3":
                return "储值卡";
            case "4":
                return "聚合支付";
            default:
                return "未知方式";
        }
    }
}
