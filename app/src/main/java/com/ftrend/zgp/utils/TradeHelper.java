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
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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


    // 交易流水
    private static Trade trade = null;
    // 商品列表
    private static List<TradeProd> prodList = null;
    // 支付信息
    private static TradePay pay = null;
    // 会员信息
    private static VipInfo vip = null;


    public static Trade getTrade() {
        return trade;
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
    private static boolean recalcTotal() {
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
            tradeProd.setPrice(priceFormat(price));
            //改价把手动优惠清掉
            tradeProd.setSingleDsc(0);
            tradeProd.setWholeDsc(0);
            tradeProd.setManuDsc(0);
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
    public static boolean checkForDscAndNoSingleDsc(int index) {
        if (index < 0 || index >= prodList.size()) {
            Log.e(TAG, "行清: 索引无效");
            return false;
        }
        int forDsc = 0;
        double singleDsc = 0;
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
        //forDsc:0否1是
        return (forDsc != 0) && (singleDsc == 0);
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
     * 界面mDscTv展示金额
     * 获取整单优惠金额
     *
     * @param rate
     * @return
     */
    public static double getWholeDsc(int rate) {
        return priceFormat(getWholePrice() * rate / 100);
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
            dscTotal += prod.getVipTotal() + prod.getTranDsc() + prod.getSingleDsc();
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
        //region 需要筛选出来可以分摊且没有单项优惠的商品
        List<TradeProd> tempList = new ArrayList<>();
        for (int i = 0; i < prodList.size(); i++) {
            if (checkForDsc(i)) {
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
                        .where(DepProduct_Table.prodCode.eq(prod.getProdCode()))
                        .querySingle().getMinimumPrice();
            }

            if (maxDsc > 0) {
                dsc = prod.getPrice() * rate;
                //优惠金额与最低限价相比，超过最低限价则取最低限价
                dsc = dsc >= prod.getPrice() - minumPrice ? prod.getPrice() - minumPrice : dsc;
                prod.setWholeDsc(priceFormat(i == prodList.size() - 1 ? wholeDsc : dsc));
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
        if (vip != null) {
            vip = null;
        }
    }

    /**
     * 保存会员优惠
     *
     * @return
     */
    public static boolean saveVipDsc() {
        //筛选没有单项优惠、整项优惠的商品
        List<TradeProd> tempList = new ArrayList<>();
        double vipDsc = 0;
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

        if (ZgParams.getProgramEdition().equals(ZgParams.PROG_EDITION_BH)) {
            double rate = vip.getVipDscRate();
            double vipPrice = 0;
            for (TradeProd prod : tempList) {
                vipPrice = prod.getPrice() * (100 - rate);
                //TODO 判断会员价谁最大
                prod.setVipTotal(vipPrice);
                prod.save();
            }
        } else {
            double rate = vip.getVipDscRate();
            double vipPrice = 0;
        }
        recalcTotal();

        return true;
    }

    /**
     * @return 获取本机内所有未结流水单
     */
    public static List<Trade> getOutOrder() {
        List<Trade> tradeList = SQLite.select().distinct().from(Trade.class)
                .where(Trade_Table.status.eq(TRADE_STATUS_HANGUP)).queryList();

        for (Trade trade : tradeList) {
            TradeProd tradeProd = SQLite.select().from(TradeProd.class)
                    .where(TradeProd_Table.lsNo.eq(trade.getLsNo()))
                    .limit(1)
                    .querySingle();
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
