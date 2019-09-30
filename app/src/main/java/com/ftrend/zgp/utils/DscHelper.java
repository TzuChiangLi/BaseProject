package com.ftrend.zgp.utils;

import android.text.TextUtils;

import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.DepProduct_Table;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.db.ZgpDb;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;

import java.util.ArrayList;
import java.util.List;

import static com.ftrend.zgp.utils.TradeHelper.getMaxWholeDsc;
import static com.ftrend.zgp.utils.TradeHelper.priceFormat;

/**
 * @author liziqiang@ftrend.cn
 */

public class DscHelper {
    private static final String TAG = "DscHelper";
    //可优惠商品列表
    private static List<TradeProd> dscList = null;
    //可优惠商品总价
    private static double prodTotal = 0;
    //计算结果（避免直接修改商品的整单优惠金额）
    private static List<Double> calcList = null;
    //计算类型：1 - 打折，2 - 抹零，其他 - 计算结果无效
    private static int calcType = 0;

    /**
     * @return 能整单优惠的商品列表
     */
    public static void beginWholeDsc() {
        List<TradeProd> prodList = TradeHelper.getProdList();
//        int forDsc;
//        double singleDsc, vipDsc, tranDsc;
        dscList = new ArrayList<>();
        calcList = new ArrayList<>();
        prodTotal = 0;
        for (TradeProd prod : prodList) {
            /*if (TextUtils.isEmpty(prod.getBarCode())) {
                //根据prodCode查商品优惠限制
                forDsc = SQLite.select().from(DepProduct.class)
                        .where(DepProduct_Table.barCode.eq(prod.getBarCode()))
                        .querySingle().getForDsc();
            } else {
                //根据barCode查商品优惠限制
                forDsc = SQLite.select().from(DepProduct.class)
                        .where(DepProduct_Table.prodCode.eq(prod.getProdCode()))
                        .querySingle().getForDsc();
            }
            singleDsc = prod.getSingleDsc();
            vipDsc = prod.getVipDsc();
            tranDsc = prod.getTranDsc();
            if ((forDsc != 0) && (singleDsc == 0) && (vipDsc == 0) && (tranDsc == 0)) {
                dscList.add(prod);
            }*/
            if (prod.getProdIsLargess() != 0) {
                continue;//跳过赠品
            }
            if (prod.getProdForDsc() == 0) {
                continue;//跳过不允许优惠商品
            }
            dscList.add(prod);
            calcList.add(0D);
            prodTotal += prod.getAmount() * prod.getPrice();
        }
    }

    /**
     * 按折扣率计算整单优惠（打折）
     *
     * @param rate 折扣率
     * @return 实际优惠金额
     */
    public static double wholeDscByRate(double rate) {
        calcType = 1;
        double dscTotal = 0;
        for (int i = 0; i < dscList.size(); i++) {
            TradeProd prod = dscList.get(i);
            double dscPrice = prod.getPrice() * (100 - rate) / 100D;//折扣后价格
            //折扣后价格不能低于最低售价
            if (dscPrice < prod.getProdMinPrice()) {
                dscPrice = prod.getProdMinPrice();
            }
            //根据折扣后价格计算优惠金额
            double dsc = prod.getAmount() * (prod.getPrice() - dscPrice);
            calcList.set(i, dsc);
            dscTotal += dsc;
        }
        return dscTotal;
    }

    /**
     * 按优惠金额分摊整单优惠（抹零）
     *
     * @param dscTotal 抹零金额
     * @return 实际抹零金额
     */
    public static double wholeDscByTotal(double dscTotal) {
        calcType = 2;
        double remainTotal = dscTotal;
        for (int i = 0; i < dscList.size() - 1; i++) {
            TradeProd prod = dscList.get(i);
            double dsc = dscTotal * prod.getPrice() * prod.getAmount() / prodTotal;//按金额比例分摊
            //折扣后价格不能低于最低售价（抹零与其他优惠共存）
            double dscMax = prodMaxDscByTotal(prod); //最大整单优惠金额
            if (dsc > dscMax) {
                dsc = dscMax;
            }
            calcList.set(i, dsc);
            remainTotal -= dsc;
        }
        //余额分摊到最后一个商品
        int index = dscList.size() - 2;
        double max = prodMaxDscByTotal(dscList.get(index));
        if (max >= remainTotal) {
            calcList.set(index, remainTotal);
            return dscTotal;
        }

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
        if (remainTotal > 0) {
            //剩余金额无法再分摊，直接舍弃
        }
        //返回实际抹零金额
        return dscTotal - remainTotal;
    }

    /**
     * 计算指定流水商品的最大抹零金额
     *
     * @param prod
     * @return
     */
    private static double prodMaxDscByTotal(TradeProd prod) {
        return (prod.getPrice() - prod.getProdMinPrice())
                - prod.getSingleDsc() - prod.getVipDsc() - prod.getTranDsc();
    }

    /**
     * 将整单优惠计算结果保存到当前交易流水
     *
     * @return
     */
    public static boolean commitWholeDsc() {
        if (calcType != 1 && calcType != 2) {
            return false;//计算结果无效
        }
        for (int i = 0; i < dscList.size(); i++) {
            TradeProd prod = dscList.get(i);
            prod.setWholeDsc(calcList.get(i));
            if (calcType == 1) {
                //打折，清除其他优惠
                prod.setSingleDsc(0);
                prod.setVipDsc(0);
                prod.setTranDsc(0);
            }
        }
        FlowManager.getDatabase(ZgpDb.class).executeTransaction(
                FastStoreModelTransaction
                        .insertBuilder(FlowManager.getModelAdapter(TradeProd.class))
                        .addAll(dscList)
                        .build());
        return TradeHelper.recalcTotal();
    }

    /**
     * 保存整单优惠
     */
    @Deprecated
    public static boolean commitWholeDsc(double wholeDsc) {
        //获取可整单优惠的商品列表
        //优惠金额=商品总价*折扣率
        //优惠金额不能大于：商品原价-最低限价
        //总的优惠金额不能大于单笔最大优惠金额（能执行到本方法时，已经排除大于最大优惠金额）
        //需要筛选出来可以分摊的商品
        double dsc = 0, minumPrice, rate;
        //本折扣率为实际折扣率，非界面显示折扣率
        double maxDsc = getMaxWholeDsc();
        double price = 0;
        for (TradeProd prod : dscList) {
            price += prod.getPrice() * prod.getAmount();
        }
        rate = wholeDsc / price;
        //可优惠金额是否比最大可优惠金额大
        wholeDsc = wholeDsc >= maxDsc ? maxDsc : wholeDsc;
        //计数变量
        int i = 0;
        //给每个商品按照比例分摊优惠的金额
        for (TradeProd prod : dscList) {
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
                dsc = i == dscList.size() - 1 ? wholeDsc : dsc;

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

        return TradeHelper.recalcTotal();
    }

    /**
     * 清空列表
     */
    public static void cancelWholeDsc() {
        dscList = null;
        calcList.clear();
        calcList = null;
        prodTotal = 0;
    }


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
     * 界面mDscTv展示金额
     * 获取整单优惠金额
     *
     * @param rate
     * @return
     */
    public static double getWholeDsc(int rate) {
        return priceFormat(getAfterWholePrice() * rate / 100);
    }

    /**
     * 界面mTotalTv展示金额
     * 获取整单优惠后的当前总金额
     *
     * @param dsc
     * @return
     */
    public static double getWholeTotal(double dsc) {
        return priceFormat(getAfterWholePrice() - dsc);
    }

    /**
     * 界面mTotalTv展示金额
     * 获取整单优惠后的当前总金额
     *
     * @return
     */
    public static double getWholeTotal(int rate) {
        return priceFormat(getAfterWholePrice() - getWholeDsc(rate));
    }

    /**
     * 界面mRateEdt展示金额
     * 获取当前整单优惠折扣率
     *
     * @param wholeDsc
     * @return
     */
    public static long getWholeRate(double wholeDsc) {
        return Math.round((wholeDsc / getAfterWholePrice()) * 100);
    }

}
