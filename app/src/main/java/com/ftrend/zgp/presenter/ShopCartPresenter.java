package com.ftrend.zgp.presenter;

import android.text.TextUtils;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.utils.http.BaseResponse;
import com.ftrend.zgp.utils.http.HttpCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 收银-选择商品P层
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopCartPresenter implements Contract.ShopCartPresenter, HttpCallBack {
    private Contract.ShopCartView mView;

    public ShopCartPresenter(Contract.ShopCartView mView) {
        this.mView = mView;
    }

    public static ShopCartPresenter createPresenter(Contract.ShopCartView mView) {
        return new ShopCartPresenter(mView);
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onSuccess(Object body, BaseResponse.ResHead head) {

    }

    @Override
    public void onFailed() {

    }

    @Override
    public void onError(String errorMsg) {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void initProdList() {
        List<DepCls> clsList = new ArrayList<>();
        List<DepProduct> prodList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            clsList.add(new DepCls(String.valueOf(10000 + i), String.valueOf(i), "类别" + i));
        }
        for (int i = 0; i < 15; i++) {
            prodList.add(new DepProduct(String.valueOf(01010000 + i), "示例商品" + i, i % 2 == 0 ? "10001" : "10002", String.valueOf(i)));
        }
        mView.setClsList(clsList);
        mView.setProdList(prodList);
    }

    @Override
    public void searchProdList(String key) {
        List<DepProduct> mList = new ArrayList<>();
        if (TextUtils.isEmpty(key)) {
            //
        } else {
            mList.clear();
            for (DepProduct depProduct : mList) {

            }
        }
    }

}
