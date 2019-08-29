package com.ftrend.zgp.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ftrend.zgp.R;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepProduct;

import java.util.List;

/**
 * 收银-商品选择界面列表适配器
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    /**
     * 0是类别，1是商品
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
                helper.setText(R.id.shop_cart_rv_classes_item_tv, ((DepCls) item).getClsName());
                break;
            case 1:
                helper.setText(R.id.shop_rv_product_tv_code, ((DepProduct) item).getProdCode());
                helper.setText(R.id.shop_rv_product_tv_prodname, ((DepProduct) item).getProdName());
                helper.setText(R.id.shop_rv_product_price, String.valueOf(((DepProduct) item).getPrice()));
                helper.addOnClickListener(R.id.shop_rv_product_btn_add);
                break;
            default:
                break;
        }

    }
}
