package com.ftrend.zgp.utils;

import android.text.TextUtils;

import com.blankj.utilcode.util.StringUtils;
import com.ftrend.zgp.R;
import com.ftrend.zgp.model.DepPayInfo;
import com.ftrend.zgp.model.DepPayInfo_Table;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.DepProduct_Table;
import com.ftrend.zgp.model.Product;
import com.ftrend.zgp.model.Product_Table;
import com.ftrend.zgp.model.SqbPayOrder;
import com.ftrend.zgp.model.SqbPayOrder_Table;
import com.ftrend.zgp.model.SqbPayResult;
import com.ftrend.zgp.model.SqbPayResult_Table;
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
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.ftrend.zgp.utils.FormatHelper.priceFormat;
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
    // 顾客类型：2-会员
    public static final String TRADE_CUST_VIP = "2";
    // 分页查询，一页查询条数
    public static final int PAGE_COUNT = 30;
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
                .and(Trade_Table.cashier.eq(ZgParams.getCurrentUser().getUserCode()))
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
     * @param lsNo 流水号
     * @return 初始化完成
     */
    public static boolean queryTradeByLsNo(String lsNo) {
        trade = getTradeByLsNo(lsNo);
        prodList = getProdListByLsNo(lsNo);
        pay = getPayByLsNo(lsNo);
        return trade != null && !prodList.isEmpty() && pay != null;
    }

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
     * 根据流水号获取未行清商品明细
     *
     * @param lsNo 流水号
     * @return 商品
     */
    public static List<TradeProd> getProdListByLsNo(String lsNo) {
        List<TradeProd> prodList = SQLite.select().from(TradeProd.class)
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
    public static long addProduct(final Product product) {
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

    private static long doAddProduct(DatabaseWrapper databaseWrapper, Product product) {
        //确定商品序号需要用的是流水单内最后一个商品的序号+1
        long index = prodList.size()==0?0:prodList.get(prodList.size()-1).getSortNo();
        final TradeProd prod = new TradeProd();
        prod.setLsNo(trade.getLsNo());
        prod.setSortNo(index + 1);//商品序号从1开始
        prod.setProdCode(product.getProdCode());
        prod.setProdName(product.getProdName());
        prod.setBarCode(product.getBarCode());
        prod.setDepCode(product.getDepCode());
//        prod.setDepCode(product.getProdDepCode());
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
     * 退货----根据流水单号获取流水信息
     *
     * @param lsNo 流水单号
     * @return 流水信息
     */
    public static Trade getPaidLs(String lsNo) {
        Trade trade = SQLite.select().from(Trade.class)
                .where(Trade_Table.status.eq(TRADE_STATUS_PAID))
                .and(Trade_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                .and(Trade_Table.lsNo.eq(lsNo))
                .querySingle();
        if (trade != null) {
            //查询收钱吧支付ClientSn
            SqbPayOrder order = SQLite.select(SqbPayOrder_Table.clientSn.withTable())
                    .from(SqbPayOrder.class)
                    .join(SqbPayResult.class, Join.JoinType.INNER)
                    .on(SqbPayOrder_Table.requestNo.withTable().eq(SqbPayResult_Table.requestNo.withTable()))
                    .where(SqbPayOrder_Table.lsNo.eq(lsNo))
                    .and(SqbPayResult_Table.status.eq("SUCCESS"))
                    .querySingle();
            if (order != null) {
                trade.setSqbPayClientSn(order.getClientSn());
            }
        }
        return trade;
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
            LogUtil.u(TAG, "交易行清", "行清: 索引无效");
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
        return pay(appPayType, amount, change, payCode, 0);
    }

    public static boolean pay(final String appPayType, final double amount,
                              final double change, final String payCode, final double balance) {
        return TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                return doPay(databaseWrapper, appPayType, amount, change, payCode, balance);
            }
        });
    }

    private static boolean doPay(DatabaseWrapper databaseWrapper,
                                 String appPayType, double amount, double change, String payCode, double balance) {
        try {
            String payTypeCode = PayType.appPayTypeToPayType(appPayType);

            if (pay == null) {
                pay = new TradePay();
                pay.setLsNo(trade.getLsNo());
            }
            pay.setPayTypeCode(payTypeCode);
            pay.setAppPayType(appPayType);
            pay.setAmount(priceFormat(amount));
            pay.setChange(priceFormat(change));
            pay.setPayCode(payCode);
            pay.setPayTime(new Date());
            pay.setBalance(priceFormat(balance));
            if (!pay.save(databaseWrapper)) {
                return false;
            }
            if (priceFormat(amount - change) >= trade.getTotal()) {
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
                if (!trade.save(databaseWrapper)) {
                    return false;
                }
                //添加到上传队列
                TradeUploadQueue queue = new TradeUploadQueue(trade.getDepCode(), trade.getLsNo());
                return queue.insert(databaseWrapper) > 0;
            }
            return true;
        } catch (Exception e) {
            LogUtil.u(TAG, "交易支付", "支付异常: " + pay.getLsNo() + " - " + appPayType + "\r\n" + e.getMessage());
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
     * 生成新的流水号
     *
     * @return
     */
    public static String newLsNo() {
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

        String lsNo;
        if (!TextUtils.isEmpty(lastNo)) {
            int max = Integer.valueOf(lastNo.substring(3));
            int current = (max == 99999) ? 1 : max + 1;
            lsNo = ZgParams.getPosCode() + String.format(Locale.CHINA, "%05d", current);
        } else {
            lsNo = ZgParams.getPosCode() + "00001";
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
        trade.setDscTotal(priceFormat(dscTotal));
        trade.setTotal(priceFormat(total));
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
                            Trade_Table.cardCode.eq(TradeHelper.vip.getCardCode()),
                            Trade_Table.vipGrade.eq(TradeHelper.vip.getVipGrade()))
                    .where(Trade_Table.lsNo.is(trade.getLsNo()))
                    .async()
                    .execute(); // non-UI blocking
        }
    }

    /**
     * 购物车 - 数量按钮、覆盖单个商品的数量
     *
     * @param index        索引序号
     * @param changeAmount 数量
     * @return -1:异常  0：超过优惠  >0:成功
     */
    public static int coverAmount(final int index, final double changeAmount) {
        final int[] result = {-1};
        TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                result[0] = doCoverAmount(databaseWrapper, index, changeAmount);
                return result[0] > 0;
            }
        });
        return result[0];
    }

    private static int doCoverAmount(DatabaseWrapper databaseWrapper, int index,
                                     double changeAmount) {
        if (index < 0 || index >= prodList.size()) {
            LogUtil.u(TAG, "交易改变数量", "改变数量: 索引无效");
            return -1;
        }

        // 手工优惠金额不能超过收款员单笔优惠上限
        TradeProd tradeProd = prodList.get(index);
        if (tradeProd.getManuDsc() > 0) {
            double dsc = 0;//已优惠金额
            for (int i = 0; i < prodList.size(); i++) {
                dsc += prodList.get(i).getManuDsc();
            }
            if ((dsc + changeAmount * (tradeProd.getManuDsc() / tradeProd.getAmount()) > ZgParams.getCurrentUser().getMaxDscTotal())) {
                return 0;
            }
        }

        double oldAmount = tradeProd.getAmount();
        tradeProd.setVipDsc(priceFormat((tradeProd.getVipDsc() / oldAmount) * changeAmount));
        tradeProd.setSingleDsc(priceFormat((tradeProd.getSingleDsc() / oldAmount) * changeAmount));
        tradeProd.setWholeDsc(priceFormat((tradeProd.getWholeDsc() / oldAmount) * changeAmount));
        tradeProd.setTranDsc(priceFormat((tradeProd.getTranDsc() / oldAmount) * changeAmount));

        tradeProd.setAmount(changeAmount);
        tradeProd.setTotal(priceFormat((changeAmount * tradeProd.getPrice()) - tradeProd.getTotalDsc()));
        if (tradeProd.save(databaseWrapper) && recalcTotal(databaseWrapper)) {
            return 1;
        } else {
            return -1;
        }
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
            LogUtil.u(TAG, "交易改变数量", "改变数量: 索引无效");
            return -1;
        }

        // 手工优惠金额不能超过收款员单笔优惠上限
        TradeProd tradeProd = prodList.get(index);
        if (tradeProd.getManuDsc() > 0) {
            double dsc = 0;
            for (int i = 0; i < prodList.size(); i++) {
                dsc += prodList.get(i).getManuDsc();
            }
            if ((dsc + changeAmount * (tradeProd.getManuDsc() / tradeProd.getAmount()) > ZgParams.getCurrentUser().getMaxDscTotal())) {
                return 0;
            }
        }

        double oldAmount = tradeProd.getAmount();
        double newAmount = oldAmount + changeAmount;
        tradeProd.setVipDsc(priceFormat((tradeProd.getVipDsc() / oldAmount) * newAmount));
        tradeProd.setSingleDsc(priceFormat((tradeProd.getSingleDsc() / oldAmount) * newAmount));
        tradeProd.setWholeDsc(priceFormat((tradeProd.getWholeDsc() / oldAmount) * newAmount));
        tradeProd.setTranDsc(priceFormat((tradeProd.getTranDsc() / oldAmount) * newAmount));

        tradeProd.setAmount(newAmount);
        tradeProd.setTotal(priceFormat((newAmount * tradeProd.getPrice()) - tradeProd.getTotalDsc()));
        if (tradeProd.save(databaseWrapper) && recalcTotal(databaseWrapper)) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * 购物车 - 当前购物车内商品总件数,需要Amount的和
     *
     * @return
     */
    public static long getTradeCount() {
        return prodList==null?0:prodList.size();
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
        return priceFormat(total);
    }

    /**
     * @return 获取商品原价
     */
    public static double getProdTotal() {
        double total = 0.00;
        for (int i = 0; i < prodList.size(); i++) {
            total += prodList.get(i).getPrice() * prodList.get(i).getAmount();
        }
        return priceFormat(total);
    }

    /**
     * 该商品是否有改价权
     *
     * @param barCode 根据条码
     * @return 是或否
     */
    public static boolean getPriceFlagByBarCode(String barCode) {
        int result = 0;
        FlowCursor csr = SQLite.select(Product_Table.priceFlag).from(Product.class)
                .where(Product_Table.barCode.eq(barCode)).query();
        if (csr == null) {
            return false;
        }
        if (csr.moveToFirst()) {
            do {
                result = csr.getIntOrDefault(0);
            } while (csr.moveToNext());
        }
        csr.close();
        return result != 0;
    }

    /**
     * 该商品是否有改价权
     *
     * @param prodCode 根据编码
     * @return 是或否
     */
    public static boolean getPriceFlagByProdCode(String prodCode) {
        Product product = SQLite.select(Product_Table.priceFlag).from(Product.class)
                .where(Product_Table.prodCode.eq(prodCode))
                .querySingle();
        return (product != null) && (product.getPriceFlag() != 0);
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
            LogUtil.u(TAG, "交易改价", "改价: 价格无效");
            return false;
        }

        if (index < 0 || index >= prodList.size()) {
            return false;
        }
        TradeProd tradeProd = prodList.get(index);
        tradeProd.setPrice(priceFormat(price));
        //改价把手动优惠+会员优惠清掉
        tradeProd.setSingleDsc(0);
        tradeProd.setWholeDsc(0);
        tradeProd.setVipDsc(0);
        tradeProd.setTotal(priceFormat(tradeProd.getAmount() * price));
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
            LogUtil.u(TAG, "交易改价", "改价: 价格无效");
            return false;
        }
        if (prodList == null || prodList.size() == 0) {
            return false;
        }
        TradeProd tradeProd = prodList.get(prodList.size() - 1);
        tradeProd.setPrice(priceFormat(price));
        tradeProd.setTotal(priceFormat(tradeProd.getAmount() * price));
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
        if (prodList == null || prodList.size() == 0) {
            return false;
        }
        TradeProd tradeProd = prodList.get(prodList.size() - 1);
        if (tradeProd.delete(databaseWrapper)) {
            prodList.remove(tradeProd);
        } else {
            return false;
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
     * 选择商品界面：获取购物车中指定商品的件数
     *
     * @param prodCode 商品码
     * @return 数量
     */
    public static long getProdCount(String prodCode) {
        long count;
        FlowCursor csr = SQLite.select(sum(TradeProd_Table.amount)).from(TradeProd.class)
                .where(TradeProd_Table.lsNo.eq(trade.getLsNo()))
                .and(TradeProd_Table.prodCode.eq(prodCode))
                .and(TradeProd_Table.delFlag.eq(DELFLAG_NO))
                .query();
        if (csr == null) {
            count = 0;
        } else {
            csr.moveToNext();
            count = csr.getLongOrDefault(0);
            csr.close();
        }
        return count;
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
            trade.setCustType(TRADE_CUST_VIP);
            trade.setCardCode(vip.getCardCode());
            trade.setVipGrade(vip.getVipGrade());
            return trade.save();
        }
        return true;
    }

    /**
     * 离线模式下只保存vipCode
     *
     * @param vipCode 会员号
     * @return 成功失败
     */
    public static boolean saveVipCodeOffline(String vipCode) {
        trade.setCardCode(vipCode);
        return trade.save();
    }

    /**
     * @return 挂单交易总数
     */
    public static long getHangUpCount() {
        return SQLite.select(count()).from(Trade.class)
                .where(Trade_Table.status.eq(TRADE_STATUS_HANGUP))
                .and(Trade_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                .count();
    }

    /**
     * 获取本机内所有挂起的流水
     *
     * @return
     */
    public static List<Trade> getOutOrder() {
        List<Trade> tradeList = SQLite.select().from(Trade.class)
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
            if (csr != null) {
                if (csr.moveToNext()) {
                    trade.setAmount(csr.getDoubleOrDefault(0));
                }
                csr.close();
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
            LogUtil.e("取单失败：购物车不为空");
            return false;
        }

        Trade trade = SQLite.select().from(Trade.class)
                .where(Trade_Table.lsNo.eq(lsNo))
                .and(Trade_Table.tradeFlag.eq(TRADE_FLAG_SALE))
                .and(Trade_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                .querySingle();
        // 验证流水状态为：挂起
        if (trade == null || !TRADE_STATUS_HANGUP.equals(trade.getStatus())) {
            LogUtil.e("取单失败：流水号无效或流水不是挂起状态");
            return false;
        }
        // 修改流水状态为：未结
        trade.setStatus(TRADE_STATUS_NOTPAY);
        if (trade.update()) {
            // 取单成功，自动读取购物车流水
            return initSale();
        } else {
            return false;
        }
    }

    /**
     * 根据编码条码获取商品的单位
     *
     * @param prodCode 商品码
     * @param barCode  条码
     * @return 单位
     */
    public static String getProdUnit(String prodCode, String barCode) {
        Product product;
        if (TextUtils.isEmpty(barCode)) {
            product = SQLite.select(Product_Table.unit)
                    .from(Product.class)
                    .where(Product_Table.prodCode.eq(prodCode))
                    .querySingle();
        } else {
            product = SQLite.select(Product_Table.unit)
                    .from(Product.class)
                    .where(Product_Table.barCode.eq(barCode))
                    .querySingle();
        }
        return product == null ? "" : product.getUnit();
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
     * @param appPayType
     * @return 支付方式
     */
    public static String convertAppPayType(String appPayType) {
        //支付方式不再区分专柜
        DepPayInfo payInfo = SQLite.select().from(DepPayInfo.class)
                .where(DepPayInfo_Table.depCode.eq("000"))
                .and(DepPayInfo_Table.appPayType.eq(appPayType))
                .querySingle();
        String payTypeName = (payInfo == null) ? "" : payInfo.getPayTypeName();
        if (TextUtils.isEmpty(payTypeName)) {
            return "未知方式";
        } else {
            return payTypeName;
        }
    }

    /**
     * @return 获取本地所有交易流水
     */
    public static long getTradeListSize() {
        return SQLite.select(count()).from(Trade.class)
                .where(Trade_Table.status.eq(TRADE_STATUS_PAID))
                .and(Trade_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                .count();
    }

    /**
     * @param page 页数
     * @return 分页查询交易流水
     */
    public static List<Trade> getTradeListPage(int page) {
        return SQLite.select()
                .from(Trade.class)
                .where(Trade_Table.status.eq(TRADE_STATUS_PAID))
                .and(Trade_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                .orderBy(Trade_Table.lsNo.desc())
                .limit(PAGE_COUNT)//条数-》3
                .offset(page * PAGE_COUNT)//当前页数
                .queryList();
    }

    /**
     * @param appPayType 支付方式代码
     * @return 图片资源
     */
    public static int payTypeImgRes(String appPayType) {
        switch (appPayType) {
            case PayType.PAYTYPE_CASH:
                //现金
                return R.drawable.money;
            case PayType.PAYTYPE_ICCARD:
                //IC卡
            case PayType.PAYTYPE_PREPAID:
                //储值卡
                return R.drawable.card;
            default:
                return R.drawable.shouqianba;
        }
    }

    /**
     * 计算实时积分
     *
     * @param trade
     * @param prodList
     * @param pay
     * @param callback
     */
    public static void calcVipScore(final Trade trade, List<TradeProd> prodList, TradePay pay,
                                    final OperateCallback callback) {
        ScoreHelper.calcVipTotal(trade, prodList, pay, callback);
    }

    /**
     * 计算实时积分
     *
     * @param callback 计算结果回调
     * @return
     */
    public static void calcVipScore(final OperateCallback callback) {
        calcVipScore(trade, prodList, pay, callback);
    }

    /**
     * 查询当前专柜的商品列表
     *
     * @param classCode
     * @param queryStr
     * @param page
     * @param pageSize
     * @return
     */
    public static List<Product> loadProduct(String classCode, String queryStr, int page, int pageSize) {
        IProperty[] selectColumns = new IProperty[Product_Table.ALL_COLUMN_PROPERTIES.length];
        //给字段名加上表名，防止查询语句字段名冲突
        for (int i = 0; i < selectColumns.length; i++) {
            selectColumns[i] = Product_Table.ALL_COLUMN_PROPERTIES[i].withTable();
        }
        From<Product> from = SQLite.select(selectColumns).from(Product.class);
        if (!"0".equalsIgnoreCase(ZgParams.getCurrentDep().getDepCode())) {
            from = from.join(DepProduct.class, Join.JoinType.INNER)
                    .on(Product_Table.prodCode.withTable().eq(DepProduct_Table.prodCode.withTable()),
                            DepProduct_Table.depCode.withTable().eq(ZgParams.getCurrentDep().getDepCode()));
        }
        Where<Product> where = from.where(Product_Table.prodStatus.withTable().notIn("2", "3"))
                //季节销售商品
                .and(OperatorGroup.clause(Product_Table.season.withTable().eq("0000"))
                        .or(Product_Table.season.withTable().like(makeSeanFilter())));
        //过滤条件：分类
        if (!StringUtils.isEmpty(classCode) && !"all".equalsIgnoreCase(classCode)) {
            where = where.and(Product_Table.clsCode.withTable().eq(classCode));
        }
        //过滤条件：模糊匹配字符串
        if (!StringUtils.isEmpty(queryStr)) {
            String filter = "%" + queryStr + "%";
            where = where.and(OperatorGroup.clause(Product_Table.prodCode.withTable().like(filter))
                    .or(Product_Table.prodName.withTable().like(filter))
                    .or(Product_Table.barCode.withTable().like(filter))
            );
        }
        if (pageSize > 0 && page >= 0) {
            where = where.offset(page * pageSize).limit(pageSize);
        }
        return where.queryList();
    }

    /**
     * 生成季节查询条件
     *
     * @return
     */
    public static String makeSeanFilter() {
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        if (month <= 3) {
            return "1___";
        } else if (month <= 6) {
            return "_1__";
        } else if (month <= 9) {
            return "__1_";
        } else {
            return "___1";
        }
    }
}
