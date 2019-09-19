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
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import java.util.ArrayList;
import java.util.Arrays;
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

    // 交易流水
    private static Trade trade = null;
    // 商品列表
    private static List<TradeProd> prodList = null;
    // 支付信息
    private static TradePay pay = null;
    // 用户权限: 0-行清权限
    public static final int USER_RIGHT_DEL = 0;
    // 用户权限: 1-取消交易权限
    public static final int USER_RIGHT_CANCEL = 1;

    public static Trade getTrade() {
        return trade;
    }

    /**
     * 清空当前交易信息
     */
    public static void clear() {
        trade = null;
        prodList = null;
        pay = null;
    }

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
        double dscTotal = 0;
        double total = 0;
        double vipTotal = 0;

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

    /**
     * 更新交易状态
     */
    public static void setTradeStatus(String status) {
        SQLite.update(Trade.class)
                .set(Trade_Table.status.eq(status))
                .where(Trade_Table.lsNo.is(trade.getLsNo()))
                .async()
                .execute(); // non-UI blocking
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
        tradeProd.setTotal((tradeProd.getAmount()) * tradeProd.getPrice());
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
     *
     * @return
     */
    public static double getTradeTotal() {
        double total = 0.00;
        for (int i = 0; i < prodList.size(); i++) {
            total += prodList.get(i).getTotal();
        }
        return total;
    }

    /**
     * 购物车 - 获取商品列表（过滤掉了已被行清的商品）
     *
     * @return 购物车内商品
     */
    public static List<TradeProd> getTradeProdList() {
        return prodList;
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
            tradeProd.setPrice(price);
            tradeProd.setTotal(tradeProd.getAmount() * price);
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
            tradeProd.setPrice(price);
            tradeProd.setTotal(tradeProd.getAmount() * price);
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
     * 检查商品优惠权限
     *
     * @param index 索引
     * @return 0:false  1:true
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
     * 获取单项优惠金额上限取一下三值的最小值
     *
     * @return 单项优惠
     */
    public static double getSingleDsc(int index) {
        double firstDsc, secondDsc, thirdDsc;
        String prodCode = prodList.get(index).getProdCode();
        String barCode = prodList.get(index).getBarCode();
        int maxDscRate = ZgParams.getCurrentUser().getMaxDscRate();
        double maxDscTotal = ZgParams.getCurrentUser().getMaxDscTotal();

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
        secondDsc = trade.getTotal() - trade.getDscTotal() - (trade.getTotal() * (1 - maxDscRate));
        //整单金额 - 整单已优惠金额 - 单笔最大优惠金额MaxDscTotal
        thirdDsc = trade.getTotal() - maxDscTotal;

        double[] dsc = {firstDsc, secondDsc, thirdDsc};
        Arrays.sort(dsc);
        recalcTotal();
        return dsc[0];
    }

    /**
     * 输入折扣率获取整单优惠金额
     *
     * @param rate 折扣率,整形数字
     * @return 优惠金额
     */
    public static double getWholeDscByRate(int rate) {
        //根据折扣率，给每个商品计算整单优惠金额
        //先计算，所有可能的金额，比对之后再保存
        double firstDsc, price = 0;
        double singleDsc, vipDsc, manuDsc, tempDsc;
        double maxDscTotal = ZgParams.getCurrentUser().getMaxDscTotal();
        firstDsc = price * (rate / 100);
        //优惠金额 = 商品原价 × 折扣率
        for (TradeProd prod : prodList) {
            //优惠金额

            //该商品其他优惠金额
            singleDsc = prod.getSingleDsc();
            vipDsc = prod.getVipDsc();
            manuDsc = prod.getManuDsc();

            //取较大值
            double[] dsc = {firstDsc, singleDsc, vipDsc, manuDsc};
            Arrays.sort(dsc);

            //优惠金额不能大于：商品原价 - 最低限价MinimumPrice
            firstDsc = dsc[dsc.length - 1];

            if (TextUtils.isEmpty(prod.getBarCode())) {
                tempDsc = SQLite.select(DepProduct_Table.minimumPrice).from(DepProduct.class)
                        .where(DepProduct_Table.prodCode.eq(prod.getProdCode()))
                        .querySingle().getMinimumPrice();
            } else {
                tempDsc = SQLite.select(DepProduct_Table.minimumPrice).from(DepProduct.class)
                        .where(DepProduct_Table.barCode.eq(prod.getBarCode()))
                        .querySingle().getMinimumPrice();
            }
            firstDsc = firstDsc > prod.getPrice() - tempDsc ? (prod.getPrice() - tempDsc) : firstDsc;

            //总的优惠金额不能大于单笔最大优惠金额MaxDscTotal

            prod.setWholeDsc(firstDsc > maxDscTotal ? maxDscTotal : firstDsc);
            prod.save();
        }

        recalcTotal();
        return firstDsc;
    }

    /**
     * 输入整单优惠金额获取折扣率
     *
     * @return 折扣率
     */
    public static double getWholeRateByDsc(double dsc) {
        //TODO 2019年9月19日19:23:05 在P层处理好输入值与最大值的提示关系
        //最大折扣金额
        double price = 0;
        double rate = 0;
        double maxDscTotal = ZgParams.getCurrentUser().getMaxDscTotal();
        //最大折扣率
        int maxDscRate = ZgParams.getCurrentUser().getMaxDscRate();
        if (dsc > maxDscTotal) {
            dsc = maxDscTotal;
        }
        //需要筛选出来可以分摊的商品
        List<TradeProd> tempList = new ArrayList<>();
        for (int i = 0; i < prodList.size(); i++) {
            if (checkForDsc(i)) {
                tempList.add(prodList.get(i));
            }
            price += prodList.get(i).getPrice() * prodList.get(i).getAmount();
        }
        //分摊到每个商品中
        for (TradeProd prod : tempList) {
            prod.setWholeDsc((prod.getPrice() * prod.getAmount() / price) * dsc);
            prod.save();
        }
        rate = dsc / price;
        recalcTotal();
        return rate > maxDscRate ? maxDscRate : rate;
    }


}
