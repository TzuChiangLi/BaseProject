package com.ftrend.zgp.utils;

import android.text.TextUtils;

import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.DepProduct_Table;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.db.TransHelper;
import com.ftrend.zgp.utils.pop.DscData;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.ArrayList;
import java.util.List;

import static com.ftrend.zgp.utils.TradeHelper.priceFormat;
import static com.ftrend.zgp.utils.TradeHelper.recalcTotal;

/**
 * @author liziqiang@ftrend.cn
 */

public class DscHelper {
    private static final String TAG = "DscHelper";
    //可优惠商品列表
    private static List<TradeProd> dscList = null;
    //计算结果（避免直接修改商品的整单优惠金额）
    private static List<Double> calcList = null;
    //计算类型：1 - 打折，2 - 抹零，其他 - 计算结果无效
    private static int calcType = 0;

    //计算类型：1 - 打折
    private static final int CALC_BY_RATE = 1;
    //计算类型：2 - 抹零
    private static final int CALC_BY_TOTAL = 2;

    //计算参数
    private static DscData dscData = null;

    //region 单项优惠计算

    /**
     * 单项优惠计算初始化
     *
     * @param index 交易流水中商品索引
     * @return 单项优惠计算参数对象。如果该商品不允许优惠，返回null
     */
    public static DscData beginSingleDsc(int index) {
        List<TradeProd> prodList = TradeHelper.getProdList();
        if (index < 0 || index >= prodList.size()) {
            return null;
        }
        TradeProd prod = prodList.get(index);
        if (prod.getProdIsLargess() != 0) {
            return null;//赠品
        }
        if (prod.getProdForDsc() == 0) {
            return null;//不允许优惠商品
        }
        dscList = new ArrayList<>();
        calcList = new ArrayList<>();
        dscList.add(prod);
        calcList.add(0D);
        makeSingleDscData();
        return dscData;
    }

    /**
     * 生成单项优惠计算参数
     */
    private static void makeSingleDscData() {
        TradeProd tradeProd = dscList.get(0);
        int prodIndex = TradeHelper.getProdList().indexOf(tradeProd);

        dscData = new DscData();
        dscData.setProdName(tradeProd.getProdName());
        dscData.setAmount(tradeProd.getAmount());
        dscData.setUnit("");
        dscData.setPrice(tradeProd.getPrice());
        dscData.setTotal(dscData.getPrice() * dscData.getAmount());

        dscData.setPriceBefore(tradeProd.getTotal() / tradeProd.getAmount());
        dscData.setDscMoneyBefore(dscData.getTotal() - tradeProd.getTotal());
        dscData.setDscOtherBefore(0);
        dscData.setTotalBefore(tradeProd.getTotal());

        dscData.setPriceAfter(dscData.getPriceBefore());
        dscData.setDscMoneyAfter(dscData.getDscMoneyBefore());
        dscData.setDscOtherAfter(dscData.getDscOtherBefore());
        dscData.setTotalAfter(dscData.getTotalBefore());

        dscData.setDscRateMax((int) TradeHelper.getMaxSingleRate(prodIndex));
        dscData.setDscMoneyMax(TradeHelper.getMaxSingleDsc(prodIndex));
    }

    /**
     * 按折扣率计算优惠金额
     *
     * @param rate 折扣率
     * @return 实际优惠金额
     */
    public static double singleDscByRate(double rate) {
        calcType = CALC_BY_RATE;
        int index = 0;
        TradeProd prod = dscList.get(index);
        double dsc = dscByRate(prod, rate);
        calcList.set(index, dsc);
        // 计算优惠后参数
        dscData.dscCalc(dsc, 0);
        return dsc;
    }

    /**
     * 按金额计算优惠
     * @param dscTotal 优惠金额
     * @return 实际优惠金额
     */
    public static double singleDscByTotal(double dscTotal) {
        calcType = CALC_BY_TOTAL;
        int index = 0;
        TradeProd prod = dscList.get(index);
        double dscPrice = dscTotal / prod.getAmount();
        //折扣后价格不能低于最低售价（单项优惠自动取消其他优惠）
        if (dscPrice < prod.getProdMinPrice()) {
            dscPrice = prod.getProdMinPrice();
        }
        double dsc = dscPrice * prod.getAmount();
        calcList.set(index, dsc);
        // 计算优惠后参数
        dscData.dscCalc(dsc, 0);
        return dsc;
    }

    /**
     * 将单项优惠计算结果保存到交易流水中
     *
     * @return
     */
    public static boolean commitSingleDsc() {
        boolean result = TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                return doCommitSingleDsc(databaseWrapper);
            }
        });
        if (result) {
            finish();
        }
        return result;
    }

    private static boolean doCommitSingleDsc(DatabaseWrapper databaseWrapper) {
        if (calcType != CALC_BY_RATE && calcType != CALC_BY_TOTAL) {
            return false;//计算结果无效
        }
        int index = 0;
        TradeProd prod = dscList.get(index);
        prod.setSingleDsc(calcList.get(index));
        //单项优惠自动取消其他优惠
        prod.setWholeDsc(0);
        prod.setVipDsc(0);
        prod.setTranDsc(0);
        return prod.save(databaseWrapper) && TradeHelper.recalcTotal(databaseWrapper);
    }

    public static void cancelSingleDsc() {
        finish();
    }

    //endregion

    //region 整单优惠计算

    /**
     * 整单优惠计算初始化
     */
    public static DscData beginWholeDsc() {
        List<TradeProd> prodList = TradeHelper.getProdList();
        dscList = new ArrayList<>();
        calcList = new ArrayList<>();
        for (TradeProd prod : prodList) {
            if (prod.getProdIsLargess() != 0) {
                continue;//跳过赠品
            }
            if (prod.getProdForDsc() == 0) {
                continue;//跳过不允许优惠商品
            }
            dscList.add(prod);
            calcList.add(0D);
        }
        makeWholeDscData();
        return dscData;
    }

    /**
     * 生成整单优惠计算参数
     */
    private static void makeWholeDscData() {
        dscData = new DscData();

        dscData.setProdName("");
        dscData.setAmount(1);
        dscData.setUnit("");
        dscData.setPrice(0);
        dscData.setTotal(TradeHelper.getProdTotal());//流水商品原价

        dscData.setPriceBefore(0);
        dscData.setDscMoneyBefore(getWholeDscTotal());
        dscData.setDscOtherBefore(getDscTotal() - getWholeDscTotal());
        dscData.setTotalBefore(TradeHelper.getTradeTotal());//流水应收

        dscData.setPriceAfter(dscData.getPriceBefore());
        dscData.setDscMoneyAfter(dscData.getDscMoneyBefore());
        dscData.setDscOtherAfter(dscData.getDscOtherBefore());
        dscData.setTotalAfter(dscData.getTotalBefore());

        dscData.setDscRateMax((int) TradeHelper.getMaxWholeRate());
        dscData.setDscMoneyMax(TradeHelper.getMaxWholeDsc());
    }

    private static double getWholeDscTotal() {
        double dsc = 0.00;
        for (TradeProd prod : dscList) {
            dsc += prod.getWholeDsc();
        }
        return dsc;
    }

    private static double getDscTotal() {
        double dsc = 0.00;
        for (TradeProd prod : dscList) {
            dsc += prod.getTotalDsc();
        }
        return dsc;
    }

    /**
     * 按折扣率计算整单优惠（打折）
     *
     * @param rate 折扣率
     * @return 实际优惠金额
     */
    public static double wholeDscByRate(double rate) {
        //清除之前的计算结果
        reset();

        calcType = CALC_BY_RATE;
        double dscTotal = 0;
        double dscOther = 0;
        for (int i = 0; i < dscList.size(); i++) {
            TradeProd prod = dscList.get(i);
            double dsc = dscByRate(prod, rate);
            if (dsc < prod.getSingleDsc() || dsc < prod.getVipDsc() || dsc < prod.getTranDsc()) {
                dsc = 0;//取优惠较大值
                dscOther += prod.getSingleDsc() + prod.getVipDsc() + prod.getTranDsc();
            }
            calcList.set(i, dsc);
            dscTotal += dsc;
        }
        // 计算优惠后参数
        dscData.dscCalc(dscTotal, dscOther);
        return dscTotal;
    }

    /**
     * 按优惠金额分摊整单优惠（抹零）
     *
     * @param dscTotal 抹零金额
     * @return 实际抹零金额
     */
    public static double wholeDscByTotal(double dscTotal) {
        //清除之前的计算结果
        reset();

        calcType = CALC_BY_TOTAL;
        //用于分摊的总金额
        double prodTotal = 0, dscOther = 0;
        for (TradeProd prod : dscList) {
            prodTotal += prod.getTotal() + prod.getWholeDsc();
            dscOther += prod.getTotalDsc() - prod.getWholeDsc();
        }
        //分摊余额
        double remainTotal = dscTotal;
        for (int i = 0; i < dscList.size() - 1; i++) {
            TradeProd prod = dscList.get(i);
            //按减去其他优惠后的金额分摊
            double dsc = dscTotal * (prod.getTotal() + prod.getWholeDsc()) / prodTotal;
            //折扣后价格不能低于最低售价（抹零与其他优惠共存）
            double dscMax = prodMaxDscByTotal(prod); //最大整单优惠金额
            if (dsc > dscMax) {
                dsc = dscMax;
            }
            calcList.set(i, dsc);
            remainTotal -= dsc;
        }
        //余额分摊到最后一个商品
        int index = dscList.size() - 1;
        double max = prodMaxDscByTotal(dscList.get(index));
        if (max >= remainTotal) {
            calcList.set(index, remainTotal);
            remainTotal = 0;
        } else {
            //余额超过最后一个商品最低售价限制，再次进行分摊
            calcList.set(index, max);
            remainTotal -= max;
            for (int i = 0; i < dscList.size() - 1; i++) {
                TradeProd prod = dscList.get(i);
                double dscMax = prodMaxDscByTotal(prod) - calcList.get(i); //可再次分摊金额
                if (dscMax >= remainTotal) {
                    calcList.set(i, calcList.get(i) + remainTotal);
                    remainTotal = 0;
                    break;//分摊完毕
                } else {
                    calcList.set(i, calcList.get(i) + dscMax);
                    remainTotal -= dscMax;
                }
            }
        }
        // 计算优惠后参数
        dscData.dscCalc(dscTotal - remainTotal, dscOther);
        //返回实际抹零金额（剩余金额无法再分摊，直接舍弃）
        return dscTotal - remainTotal;
    }

    /**
     * 计算指定流水商品的最大抹零金额
     *
     * @param prod
     * @return
     */
    private static double prodMaxDscByTotal(TradeProd prod) {
        return (prod.getPrice() - prod.getProdMinPrice()) * prod.getAmount()
                - (prod.getTotalDsc() - prod.getWholeDsc());
    }

    /**
     * 将整单优惠计算结果保存到当前交易流水
     *
     * @return
     */
    public static boolean commitWholeDsc() {
        boolean result = TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                return doCommitWholeDsc(databaseWrapper);
            }
        });
        if (result) {
            finish();
        }
        return result;
    }

    private static boolean doCommitWholeDsc(DatabaseWrapper databaseWrapper) {
        if (calcType != CALC_BY_RATE && calcType != CALC_BY_TOTAL) {
            return false;//计算结果无效
        }
        for (int i = 0; i < dscList.size(); i++) {
            TradeProd prod = dscList.get(i);
            prod.setWholeDsc(calcList.get(i));
            if (prod.getWholeDsc() > 0 && calcType == CALC_BY_RATE) {
                //打折，清除其他优惠
                prod.setSingleDsc(0);
                prod.setVipDsc(0);
                prod.setTranDsc(0);
            }
        }
        for (TradeProd prod : dscList) {
            if (!prod.update(databaseWrapper)) {
                return false;
            }
        }
        return TradeHelper.recalcTotal(databaseWrapper);
    }

    /**
     * 清空列表
     */
    public static void cancelWholeDsc() {
        finish();
    }
    //endregion

    //region 会员优惠计算
    //------------------------------------------
    // 超市版：会员优惠规则-1
    private static final int VIP_ONE = 1;
    // 超市版：会员优惠规则-2
    private static final int VIP_TWO = 2;
    // 超市版：会员优惠规则-3
    private static final int VIP_THREE = 3;


    /**
     * 保存会员优惠
     *
     * @return
     */
    public static boolean saveVipProdDsc(final int index) {
        return TransHelper.transSync(new TransHelper.TransRunner() {
            @Override
            public boolean execute(DatabaseWrapper databaseWrapper) {
                return doSaveVipDsc(index, databaseWrapper);
            }
        });
    }

    /**
     * 保存会员优惠
     *
     * @param databaseWrapper 数据库
     * @return 是否成功
     */
    private static boolean doSaveVipDsc(int index, DatabaseWrapper databaseWrapper) {
        double vipPriceType = TradeHelper.vip.getVipPriceType();
        TradeProd prod = TradeHelper.getProdList().get(index);
        if (ZgParams.getProgramEdition().equals(ZgParams.PROG_EDITION_BH)) {
            //百货版
            double rate = TradeHelper.vip.getVipDscRate();
            double vipDsc, rateDsc;
            //强制打折
            if (TradeHelper.VIP_DSC_FORCE.equalsIgnoreCase(TradeHelper.vip.getForceDsc())
                    || TradeHelper.checkForDsc(index)) {
                //按优惠率
                rateDsc = prod.getPrice() * (100 - rate) / 100;
                //按优惠价
                vipDsc = prod.getPrice() - queryVipPrice(vipPriceType, prod);
                //已存在手工优惠的不参与会员优惠，修改depProduct的价格
                prod.setVipDsc(Math.max(rateDsc, vipDsc) * prod.getAmount());
                prod.setWholeDsc(0);
                prod.setSingleDsc(0);
                prod.setTotal(priceFormat(prod.getPrice() * prod.getAmount() - prod.getTotalDsc()));
            }
        } else {
            //超市版，尚不支持促销
            //如果商品允许优惠
            if (TradeHelper.checkForDsc(index)) {
                double rateRule = TradeHelper.vip.getRateRule();
                double vipDsc, rateDsc, vipRate;
                //优惠率规则
                vipRate = queryRateRule(rateRule, prod);
                rateDsc = prod.getPrice() * (100 - vipRate) / 100;
                //如果没有会员价，vipPrice = prod.getPrice()商品原价
                vipDsc = prod.getPrice() - queryVipPrice(vipPriceType, prod);
                //已存在手工优惠的不参与会员优惠
                prod.setVipDsc(Math.max(rateDsc, vipDsc) * prod.getAmount());
                prod.setWholeDsc(0);
                prod.setSingleDsc(0);
                prod.setTotal(priceFormat(prod.getPrice() * prod.getAmount() - prod.getTotalDsc()));
            }
        }
        if (prod.save(databaseWrapper)) {
            return recalcTotal();
        } else {
            return false;
        }
    }

    /**
     * @param rateRule 折扣规则
     * @param prod     商品信息
     * @return 折扣
     */
    private static double queryRateRule(double rateRule, TradeProd prod) {
        double rate = 0;
        DepProduct dep;
        if (TextUtils.isEmpty(prod.getBarCode())) {
            dep = SQLite.select().from(DepProduct.class)
                    .where(DepProduct_Table.prodCode.eq(prod.getProdCode()))
                    .querySingle();
        } else {
            dep = SQLite.select().from(DepProduct.class)
                    .where(DepProduct_Table.barCode.eq(prod.getBarCode()))
                    .querySingle();
        }
        if (rateRule == VIP_ONE) {
            rate = dep.getVipRate1();
        } else if (rateRule == VIP_TWO) {
            rate = dep.getVipRate2();
        } else if (rateRule == VIP_THREE) {
            rate = dep.getVipRate3();
        }
        return rate;
    }


    /**
     * @param vipPriceType 类型
     * @param prod         商品
     * @return 会员价
     */
    private static double queryVipPrice(double vipPriceType, TradeProd prod) {
        double vipPrice = 0;
        DepProduct dep;
        if (TextUtils.isEmpty(prod.getBarCode())) {
            dep = SQLite.select().from(DepProduct.class)
                    .where(DepProduct_Table.prodCode.eq(prod.getProdCode()))
                    .querySingle();
        } else {
            dep = SQLite.select().from(DepProduct.class)
                    .where(DepProduct_Table.barCode.eq(prod.getBarCode()))
                    .querySingle();
        }
        if (vipPriceType == VIP_ONE) {
            vipPrice = dep.getVipPrice1();
        } else if (vipPriceType == VIP_TWO) {
            vipPrice = dep.getVipPrice2();
        } else if (vipPriceType == VIP_THREE) {
            vipPrice = dep.getVipPrice3();
        }
        //如果商品的会员价为0，那还是执行商品原价
        return vipPrice == 0 ? prod.getPrice() : vipPrice;
    }
    //endregion

    //-----------------------------------------------------------------------------------------------------------

    /**
     * @return 筛选后的交易原价
     */
    public static double getAfterWholePrice() {
        double price = 0;
        for (TradeProd prod : dscList) {
            price += prod.getPrice() * prod.getAmount();
        }
        return price;
    }

    /**
     * 单品计算折扣金额
     *
     * @param prod
     * @param rate
     * @return
     */
    private static double dscByRate(TradeProd prod, double rate) {
        double dscPrice = prod.getPrice() * (100 - rate) / 100.00;//折扣后价格
        //折扣后价格不能低于最低售价
        if (dscPrice < prod.getProdMinPrice()) {
            dscPrice = prod.getProdMinPrice();
        }
        //根据折扣后价格计算优惠金额
        return prod.getAmount() * (prod.getPrice() - dscPrice);
    }

    /**
     * 清除计算结果
     */
    private static void reset() {
        for (int i = 0; i < calcList.size(); i++) {
            calcList.set(i, 0D);
        }
    }

    /**
     * 清除计算参数和缓存
     */
    private static void finish() {
        calcType = 0;
        dscData = null;
        dscList = null;
        if (calcList != null) {
            calcList.clear();
            calcList = null;
        }
    }

}
