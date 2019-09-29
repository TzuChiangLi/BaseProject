package com.ftrend.zgp.utils;

import android.text.TextUtils;
import android.util.Log;

import com.ftrend.zgp.model.DepPayInfo;
import com.ftrend.zgp.model.DepPayInfo_Table;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.DepProduct_Table;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradePay;
import com.ftrend.zgp.model.TradePay_Table;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.TradeProd_Table;
import com.ftrend.zgp.model.TradeUploadQueue;
import com.ftrend.zgp.model.Trade_Table;
import com.ftrend.zgp.model.VipInfo;
import com.ftrend.zgp.utils.log.LogUtil;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    // APP支付类型: 1-现金
    public static final String APP_PAY_TYPE_CASH = "1";
    // APP支付类型: 2-支付宝
    public static final String APP_PAY_TYPE_ALIPAY = "2";
    // APP支付类型: 3-微信支付
    public static final String APP_PAY_TYPE_WXPAY = "3";
    // APP支付类型: 4-储值卡
    public static final String APP_PAY_TYPE_VIPCARD = "4";


    // 用户权限: 0-行清权限
    public static final int USER_RIGHT_DEL = 0;
    // 用户权限: 1-取消交易权限
    public static final int USER_RIGHT_CANCEL = 1;

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
     * 取单初始化单据信息
     *
     * @param lsNo 取单取出的流水单号
     */
    public static void initSale(String lsNo) {
        trade = SQLite.select().from(Trade.class)
                .where(Trade_Table.status.eq(TRADE_STATUS_HANGUP))
                .and(Trade_Table.tradeFlag.eq(TRADE_FLAG_SALE))
                .and(Trade_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                .and(Trade_Table.lsNo.eq(lsNo))
                .querySingle();
        prodList = SQLite.select().from(TradeProd.class)
                .where(TradeProd_Table.lsNo.eq(lsNo))
                .and(TradeProd_Table.delFlag.eq(DELFLAG_NO))
                .queryList();
        pay = SQLite.select().from(TradePay.class)
                .where(TradePay_Table.lsNo.eq(lsNo))
                .querySingle();
    }
    //endregion

    //region addProduct----添加到商品表

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
    //endregion

    //region delProduct----行清

    /**
     * 行清
     *
     * @param index 行清的商品索引
     * @return 是否成功
     */
    public static boolean delProduct(int index) {
        if (index < 0 || index >= prodList.size()) {
            Log.e(TAG, "行清: 索引无效");
            return false;
        }
        TradeProd prod = prodList.get(index);
        prod.setDelFlag("1");
        prodList.remove(index);
        return prod.save();
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
     * @return
     */
    public static boolean pay(String appPayType, double amount, double change, String payCode) {
        try {
            String payTypeCode = SQLite.select(DepPayInfo_Table.payTypeCode).from(DepPayInfo.class)
                    .where(DepPayInfo_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                    .and(DepPayInfo_Table.appPayType.eq(appPayType))
                    .querySingle().getPayTypeCode();

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
            // TODO: 2019/9/7 启用事务，避免数据异常
            if (pay.save()) {
                trade.setTradeTime(pay.getPayTime());
                trade.setCashier(ZgParams.getCurrentUser().getUserCode());
                trade.setStatus(TRADE_STATUS_PAID);
                return trade.save();
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "支付异常: " + pay.getLsNo() + " - " + appPayType, e);
            return false;
        }

    }


    /**
     * 完成支付（仅适用于现金支付）
     *
     * @param appPayType APP支付类型
     * @return
     */
    public static boolean pay(String appPayType) {
        return pay(appPayType, trade.getTotal(), 0, "(无)");
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
     * 重新汇总流水金额：优惠、合计
     */
    public static boolean recalcTotal() {
        double dscTotal = 0;
        double total = 0;

        for (TradeProd prod : prodList) {
            dscTotal += prod.getManuDsc() + prod.getTranDsc() + prod.getVipDsc();
            total += prod.getTotal();
        }
        trade.setDscTotal(dscTotal);
        trade.setTotal(total);
        return trade.save();
    }

    /**
     * 更新交易状态
     */
    public static void setTradeStatus(String status) {
        if (TradeHelper.vip != null) {
            SQLite.update(Trade.class)
                    .set(Trade_Table.status.eq(status), Trade_Table.vipCode.eq(TradeHelper.vip.getVipCode()),
                            Trade_Table.vipTotal.eq(TradeHelper.trade.getVipTotal()),
                            Trade_Table.custType.eq(TRADE_CUST_VIP),
                            Trade_Table.cardCode.eq(TradeHelper.vip.getCardCode()))
                    .where(Trade_Table.lsNo.is(trade.getLsNo()))
                    .async()
                    .execute(); // non-UI blocking
        } else {
            SQLite.update(Trade.class)
                    .set(Trade_Table.status.eq(status))
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
     * @return 购物车中未行清的所有商品的数量
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
     * @return 是否成功
     */
    public static boolean changeAmount(int index, double changeAmount) {
        if (index < 0 || index >= prodList.size()) {
            Log.e(TAG, "改变数量: 索引无效");
            return false;
        }
        TradeProd tradeProd = prodList.get(index);
        tradeProd.setAmount(tradeProd.getAmount() + changeAmount);
        tradeProd.setTotal(priceFormat(tradeProd.getAmount() * (tradeProd.getPrice() - tradeProd.getManuDsc() - tradeProd.getVipDsc() - tradeProd.getTranDsc())));
        if (tradeProd.save()) {
            recalcTotal();
            return true;
        } else {
            return false;
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
    public static boolean priceChangeInShopList(int index, double price) {
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
            tradeProd.setManuDsc(0);
            tradeProd.setVipDsc(0);
            tradeProd.setTotal(priceFormat(tradeProd.getAmount() * price));
        }
        if (tradeProd.save()) {
            recalcTotal();
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
    public static boolean priceChangeInShopCart(double price) {
        if (price < 0) {
            Log.e(TAG, "改价: 价格无效");
            return false;
        }
        TradeProd tradeProd = prodList.get(prodList.size() - 1);
        if (tradeProd != null) {
            tradeProd.setPrice(priceFormat(price));
            tradeProd.setTotal(priceFormat(tradeProd.getAmount() * price));
        }
        if (tradeProd.save()) {
            recalcTotal();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 选择商品界面改价撤销
     */
    public static void rollackPriceChangeInShopCart() {
        TradeProd tradeProd = prodList.get(prodList.size() - 1);
        if (tradeProd != null) {
            tradeProd.delete();
            prodList.remove(prodList.size() - 1);
        }
        recalcTotal();
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
                return "取消交易";
            case TradeHelper.TRADE_STATUS_HANGUP:
                return "已挂单";
            case TradeHelper.TRADE_STATUS_NOTPAY:
                return "尚未支付";
            case TradeHelper.TRADE_STATUS_PAID:
                return "支付成功";
            default:
                return "交易异常";
        }
    }


    /**
     * 购物车 - 行清、取消交易权限
     *
     * @param rightType 查看权限类型
     * @return 是否有此权限
     */
    public static boolean getUserRight(int rightType) {
        return true;
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
        int forDsc = 0;
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

        if (TextUtils.isEmpty(prodList.get(index).getBarCode())) {
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
        for (TradeProd prod : prodList) {
            wholePrice += prod.getPrice() * prod.getAmount();
            wholeDsc += (prod.getManuDsc() + prod.getVipDsc() + prod.getTranDsc()) * prod.getAmount();
        }
        secondDsc = wholePrice - wholeDsc - (wholePrice * (maxDscRate / 100));
        //整单金额 - 整单已优惠金额 - 单笔最大优惠金额MaxDscTotal，第三个参数需要计算
        thirdDsc = wholePrice - wholeDsc - ZgParams.getCurrentUser().getMaxDscTotal();

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
        return maxRate >= getUserMaxDscRate() ? getUserMaxDscRate() : maxRate;
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
        }
        return price;
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
//        return priceFormat(getWholePrice() * rate / 100);
    }

    /**
     * 界面mTotalTv展示金额
     * 获取整单优惠后的当前总金额
     *
     * @param dsc
     * @return
     */
    public static double getWholeTotal(double dsc) {
        return priceFormat(getWholePrice() - dsc);
    }

    /**
     * 界面mRateEdt展示金额
     * 获取当前整单优惠折扣率
     *
     * @param wholeDsc
     * @return
     */
    public static long getWholeRate(double wholeDsc) {
        return Math.round((wholeDsc / getWholePrice()) * 100);
    }


    /**
     * 收款员当前可以优惠的最大金额：最大上限-已有优惠
     *
     * @return 当前最大折扣金额
     */
    public static double getMaxWholeDsc() {
        double dscTotal = 0;
        //按照系统折扣设置该收款员在交易中最多优惠金额
        //商品原价*折扣率
        double maxDscTotal = getWholePrice() * getUserMaxDscRate() / 100;
        //这个值与设置最大金额谁大
        maxDscTotal = maxDscTotal >= ZgParams.getCurrentUser().getMaxDscTotal() ? ZgParams.getCurrentUser().getMaxDscTotal() : maxDscTotal;
        //当前商品的全部优惠
        for (TradeProd prod : prodList) {
            dscTotal += prod.getVipDsc() + prod.getTranDsc() + prod.getSingleDsc();
        }
        //最大金额减去目前所有的优惠，即可优惠的金额
        dscTotal = maxDscTotal - dscTotal >= 0 ? maxDscTotal - dscTotal : 0;
        return dscTotal;
    }


    /**
     * 整单优惠最大折扣
     *
     * @return
     */
    public static long getMaxWholeRate() {
        return Math.round(getMaxWholeDsc() / getWholePrice() * 100) > getUserMaxDscRate() ?
                getUserMaxDscRate() : Math.round(getMaxWholeDsc() / getWholePrice() * 100);
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
    public static boolean saveSingleDsc(int index, double singleDsc) {
        if (index < 0 || index >= prodList.size()) {
            Log.e(TAG, "行清: 索引无效");
            return false;
        }
        TradeProd prod = prodList.get(index);
        prod.setSingleDsc(priceFormat(singleDsc));
        //单项优惠的时候，清空整单优惠
        prod.setWholeDsc(0);
        prod.setVipTotal(0);
        prod.setManuDsc(priceFormat(prod.getSingleDsc() + prod.getWholeDsc()));
        prod.setTotal(priceFormat((prod.getPrice() - prod.getManuDsc() - prod.getVipDsc() - prod.getTranDsc()) * prod.getAmount()));
        if (prod.save()) {
            return recalcTotal();
        }
        return false;
    }


    /**
     * 保存整项优惠
     *
     * @param wholeDsc 输入的整单优惠金额
     * @return
     */
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
                prod.setWholeDsc(dsc);
                //整单优惠的时候，单项优惠清零
                prod.setSingleDsc(0);

                prod.setManuDsc(priceFormat(prod.getSingleDsc() + dsc));
                prod.setTotal(priceFormat((prod.getPrice() - prod.getManuDsc() - prod.getVipDsc() - prod.getTranDsc()) * prod.getAmount()));
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
                rateDsc = prod.getPrice() * (rate) / 100;
                vipDsc = prod.getPrice() - queryVipPrice(vipPriceType, prod);
                LogUtil.d("----rate/vip:" + rateDsc + "/" + vipDsc);
                prod.setVipDsc(Math.max(rateDsc, vipDsc));
                prod.setManuDsc(0);
                prod.setWholeDsc(0);
                prod.setSingleDsc(0);
                //已存在手工优惠的不参与会员优惠
                prod.setTotal(priceFormat((prod.getPrice() - prod.getManuDsc() - prod.getVipDsc() - prod.getTranDsc()) * prod.getAmount()));
                prod.save();
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
                prod.setVipDsc(Math.max(rateDsc, vipDsc));
                prod.setManuDsc(0);
                prod.setWholeDsc(0);
                prod.setSingleDsc(0);
                //已存在手工优惠的不参与会员优惠
                prod.setTotal(priceFormat((prod.getPrice() - prod.getManuDsc() - prod.getVipDsc() - prod.getTranDsc()) * prod.getAmount()));
                prod.save();
            }
        }
        return recalcTotal();
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
     * @param rateRule
     * @param prod
     * @return
     */
    private static double queryRateRule(double rateRule, TradeProd prod) {
        double rate = 0;
        if (TextUtils.isEmpty(prod.getBarCode())) {
            if (rateRule == VIP_ONE) {
                rateRule = SQLite.select(DepProduct_Table.vipRate1).from(DepProduct.class)
                        .where(DepProduct_Table.prodCode.eq(prod.getProdCode()))
                        .querySingle().getVipRate1();
            } else if (rateRule == VIP_TWO) {
                rateRule = SQLite.select(DepProduct_Table.vipRate2).from(DepProduct.class)
                        .where(DepProduct_Table.prodCode.eq(prod.getProdCode()))
                        .querySingle().getVipRate2();
            } else if (rateRule == VIP_THREE) {
                rateRule = SQLite.select(DepProduct_Table.vipRate3).from(DepProduct.class)
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
     * @return 获取本机内所有未结流水单
     */
    public static List<Trade> getOutOrder() {
        List<Trade> tradeList = SQLite.select().distinct().from(Trade.class)
                .where(Trade_Table.status.eq(TRADE_STATUS_HANGUP))
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


}
