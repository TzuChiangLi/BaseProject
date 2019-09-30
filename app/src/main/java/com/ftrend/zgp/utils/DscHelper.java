package com.ftrend.zgp.utils;

import android.text.TextUtils;

import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.DepProduct_Table;
import com.ftrend.zgp.model.TradeProd;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import static com.ftrend.zgp.utils.TradeHelper.priceFormat;

/**
 * @author liziqiang@ftrend.cn
 */

public class DscHelper {
    private static final String TAG = "DscHelper";
    //可整单优惠的商品
    private static List<TradeProd> dscList = null;

    /**
     * @return 能整单优惠的商品列表
     */
    public static List<TradeProd> beginWholeDsc() {
        List<TradeProd> prodList = TradeHelper.getProdList();
        int forDsc;
        double singleDsc, vipDsc, tranDsc;
        dscList = new ArrayList<>();
        for (TradeProd prod : prodList) {
            if (TextUtils.isEmpty(prod.getBarCode())) {
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
            }
        }
        return dscList;
    }

    /**
     * 保存整单优惠
     */
    public static boolean commitWholeDsc(double wholeDsc) {
        //获取可整单优惠的商品列表
        //优惠金额=商品总价*折扣率
        //优惠金额不能大于：商品原价-最低限价
        //总的优惠金额不能大于单笔最大优惠金额（能执行到本方法时，已经排除大于最大优惠金额）
        //需要筛选出来可以分摊的商品
        double dsc = 0, minumPrice, rate;
        //本折扣率为实际折扣率，非界面显示折扣率
        double maxDsc = TradeHelper.getMaxWholeDsc();
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


}
