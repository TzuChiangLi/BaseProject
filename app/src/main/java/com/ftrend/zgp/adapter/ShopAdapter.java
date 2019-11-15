package com.ftrend.zgp.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ftrend.zgp.R;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.RtnHelper;
import com.ftrend.zgp.utils.TradeHelper;

import java.util.List;
import java.util.Locale;

import butterknife.BindColor;
import butterknife.ButterKnife;

/**
 * 收银-商品选择界面列表适配器
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    /**
     * 0是选择界面的类别，1是选择界面的商品信息，2是查看购物车内的商品信息，3是支付列表
     * 4是取单界面
     */
    private int type;
    @BindColor(R.color.shop_rv_cls_bg)
    int rv_cls_selected;
    @BindColor(R.color.common_rv_item)
    int rv_item_selected;
    @BindColor(R.color.common_white)
    int rv_item_normal;


    public ShopAdapter(int layoutResId, @Nullable List<T> data, int type) {
        super(layoutResId, data);
        this.type = type;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, T item) {
        ButterKnife.bind(this, helper.itemView);
        switch (type) {
            case 0:
                //选择商品界面左部类别分栏
                helper.setBackgroundColor(R.id.shop_cart_rv_classes_item_tv, ((DepCls) item).isSelect() ? rv_item_normal : rv_cls_selected);
                helper.setText(R.id.shop_cart_rv_classes_item_tv, ((DepCls) item).getClsName());
                break;
            case 1:
                //选择商品界面右部商品分栏
                helper.setText(R.id.shop_rv_product_tv_prodcode, ((DepProduct) item).getProdCode());
                helper.setText(R.id.shop_rv_product_tv_prodname, ((DepProduct) item).getProdName());
                helper.setText(R.id.shop_rv_product_price, String.format("%.2f", ((DepProduct) item).getPrice()));
                helper.setText(R.id.shop_rv_product_tv_barcode, ((DepProduct) item).getBarCode());
                helper.setText(R.id.shop_rv_product_tv_num, String.format("%d", TradeHelper.getProdCountList().get(0).get(((DepProduct) item).getProdCode())));
                helper.setGone(R.id.shop_rv_product_tv_num, TradeHelper.getProdCount(((DepProduct) item).getProdCode(), ((DepProduct) item).getBarCode()) == 0 ? false : true);
                helper.setBackgroundColor(R.id.shop_cart_rv_product_rl, ((DepProduct) item).isSelect() ? rv_item_selected : rv_item_normal);
                break;
            case 2:
                //购物车商品列表
                if (((TradeProd) item).getDelFlag().equals(TradeHelper.DELFLAG_NO)) {
                    helper.setText(R.id.shop_list_rv_product_tv_prodcode, ((TradeProd) item).getProdCode());
                    helper.setText(R.id.shop_list_rv_product_tv_prodname, ((TradeProd) item).getProdName());
                    helper.setText(R.id.shop_list_rv_product_tv_num, String.valueOf(((TradeProd) item).getAmount()).replace(".0", ""));
                    helper.setText(R.id.shop_list_rv_product_tv_num_unit, TradeHelper.getProdUnit(((TradeProd) item).getProdCode(), ((TradeProd) item).getBarCode()));
                    helper.setText(R.id.shop_list_rv_product_tv_per_price, String.format("%.2f", ((TradeProd) item).getPrice()));
                    helper.setText(R.id.shop_list_rv_product_tv_total, String.format("%.2f", ((TradeProd) item).getTotal()));
                    helper.setText(R.id.shop_list_rv_product_tv_barcode, ((TradeProd) item).getBarCode());
                    helper.setText(R.id.shop_list_rv_product_tv_discount, String.format("%.2f%s%s", ((TradeProd) item).getManuDsc() + ((TradeProd) item).getVipDsc() + ((TradeProd) item).getTranDsc(),
                            String.format("(-%d", Math.round(100 * ((((TradeProd) item).getManuDsc() + ((TradeProd) item).getVipDsc() + ((TradeProd) item).getTranDsc()) / ((TradeProd) item).getAmount()) / ((TradeProd) item).getPrice())), "%)"));
                    helper.setBackgroundColor(R.id.shop_list_rv_product_rl, ((TradeProd) item).isSelect() ? rv_item_selected : rv_item_normal);
                    helper.setGone(R.id.shop_list_rv_ll_btn, ((TradeProd) item).isSelect() ? true : false);
                    helper.addOnClickListener(R.id.shop_list_rv_img_add);
                    helper.addOnClickListener(R.id.shop_list_rv_img_minus);
                    helper.addOnClickListener(R.id.shop_list_rv_btn_change_price);
                    helper.addOnClickListener(R.id.shop_list_rv_btn_discount);
                    helper.addOnClickListener(R.id.shop_list_rv_btn_del);
                }
                break;
            case 3:
                //支付方式列表
                helper.setImageResource(R.id.pay_way_rv_img, ((Menu.MenuList) item).getMenuImg());
                helper.setText(R.id.pay_way_rv_tv, ((Menu.MenuList) item).getMenuName());
                break;
            case 4:
                //取单
                helper.setText(R.id.out_order_tv_lsno, ((Trade) item).getLsNo());
                helper.setText(R.id.out_order_tv_num, String.valueOf(((Trade) item).getProdNum()).replace(".0", ""));
                helper.setText(R.id.out_order_tv_total, String.format("%.2f", ((Trade) item).getTotal()));
                helper.setText(R.id.out_order_tv_prod_name, ((Trade) item).getProdName());
                helper.setText(R.id.out_order_tv_amount, String.valueOf(((Trade) item).getAmount()).replace(".0", ""));
                helper.addOnClickListener(R.id.out_order_btn_get_out);
                break;
            case 5:
                //退货商品列表
                TradeProd prod = (TradeProd) item;
                if (prod.getDelFlag().equals(TradeHelper.DELFLAG_NO)) {
                    //销售单区域
                    helper.setText(R.id.rtn_list_rv_product_tv_prodcode, prod.getProdCode());
                    helper.setText(R.id.rtn_list_rv_product_tv_prodname, prod.getProdName());
                    helper.setText(R.id.rtn_list_rv_product_tv_num, String.valueOf(prod.getAmount()).replace(".0", ""));
                    helper.setText(R.id.rtn_list_rv_product_tv_num_unit,
                            prod.getLastRtnAmount() < 0
                                    ? String.format(Locale.CHINA, "(%d)", Math.round(prod.getLastRtnAmount()))
                                    : "");
//                    helper.setText(R.id.rtn_list_rv_product_tv_num_unit, TradeHelper.getProdUnit(prod.getProdCode(), prod.getBarCode()));
                    helper.setText(R.id.rtn_list_rv_ls_tv_price, String.format("%.2f", prod.getPrice()));
                    helper.setText(R.id.rtn_list_rv_ls_tv_total, String.format("%.2f", prod.getTotal()));
                    helper.setText(R.id.rtn_list_rv_product_tv_barcode, prod.getBarCode());
                    helper.setText(R.id.rtn_list_rv_ls_tv_discount,
                            String.format("%.2f(-%d%%)",
                                    prod.getTotalDsc() / prod.getAmount(),
                                    Math.round(100 * prod.getTotalDsc() / prod.getTotal())));


                    //退货数量(如果是退货流水，显示已退数量)
                    helper.setText(R.id.rtn_list_rv_rtn_tv_amount,
                            String.valueOf(RtnHelper.getTrade().getRtnFlag().equals(RtnHelper.TRADE_FLAG_RTN)
                                    ? prod.getRtnAmount()
                                    : RtnHelper.getRtnAmountBySortNo(prod.getSortNo())).replace(".0", ""));
                    //实退小计(如果是退货流水，显示已退金额)
                    helper.setText(R.id.rtn_list_rv_rtn_tv_total,
                            String.format("%.2f", RtnHelper.getTrade().getRtnFlag().equals(RtnHelper.TRADE_FLAG_RTN)
                                    ? prod.getRtnTotal()
                                    : (RtnHelper.getRtnTotalBySortNo(prod.getSortNo()))));
                    //实退单价
                    helper.setText(R.id.rtn_list_rv_rtn_tv_price, String.format("%.2f", prod.getRtnPrice()));
                    //实退单位
                    helper.setText(R.id.rtn_list_rv_rtn_tv_unit, TradeHelper.getProdUnit(prod.getProdCode(), prod.getBarCode()));

                    //样式变更
                    helper.setBackgroundColor(R.id.rtn_list_rv_product_rl, prod.isSelect() ? rv_item_selected : rv_item_normal);
                    helper.setGone(R.id.rtn_list_rv_ll_btn, prod.isSelect() ? true : false);
                    helper.addOnClickListener(R.id.rtn_list_rv_img_add);
                    helper.addOnClickListener(R.id.rtn_list_rv_img_minus);
                    helper.addOnClickListener(R.id.rtn_list_rv_btn_change_price);
                }
                break;
            default:
                break;
        }

    }
}
