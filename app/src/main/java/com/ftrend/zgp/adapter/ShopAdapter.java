package com.ftrend.zgp.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ftrend.zgp.R;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.Menu;
import com.ftrend.zgp.model.TradeProd;

import java.util.List;

/**
 * 收银-商品选择界面列表适配器
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    /**
     * 0是选择界面的类别，1是选择界面的商品信息，2是查看购物车内的商品信息
     */
    private int type;

    public ShopAdapter(int layoutResId, @Nullable List<T> data, int type) {
        super(layoutResId, data);
        this.type = type;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, T item) {

        switch (type) {
            case 0:
                //选择商品界面左部类别分栏
                helper.setText(R.id.shop_cart_rv_classes_item_tv, ((DepCls) item).getClsName());
                break;
            case 1:
                //选择商品界面右部商品分栏
                helper.setText(R.id.shop_rv_product_tv_code, ((DepProduct) item).getProdCode());
                helper.setText(R.id.shop_rv_product_tv_prodname, ((DepProduct) item).getProdName());
                helper.setText(R.id.shop_rv_product_price, String.valueOf(((DepProduct) item).getPrice()));
                helper.addOnClickListener(R.id.shop_rv_product_btn_add);
                break;
            case 2:
                //购物车商品列表
                helper.setText(R.id.shop_list_rv_product_tv_code, ((TradeProd) item).getProdCode());
                helper.setText(R.id.shop_list_rv_product_tv_prodname, ((TradeProd) item).getProdName());
                helper.setText(R.id.shop_list_rv_product_tv_num, String.valueOf(((TradeProd) item).getAmount()));
                helper.setText(R.id.shop_list_rv_product_tv_per_price, String.valueOf(((TradeProd) item).getPrice()));
                helper.setText(R.id.shop_list_rv_product_tv_total, String.valueOf(((TradeProd) item).getTotal()));
                break;
            case 3:
                //支付方式列表
                helper.setImageResource(R.id.pay_way_rv_img, ((Menu.MenuList) item).getMenuImg());
                helper.setText(R.id.pay_way_rv_tv, ((Menu.MenuList) item).getMenuName());
                break;
            default:
                break;
        }

    }
}
