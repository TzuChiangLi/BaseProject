package com.ftrend.zgp.presenter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.utils.db.DatabaseManger;
import com.ftrend.zgp.utils.http.BaseResponse;
import com.ftrend.zgp.utils.http.HttpCallBack;
import com.ftrend.zgp.utils.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 收银-选择商品P层
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopCartPresenter implements Contract.ShopCartPresenter, HttpCallBack {
    private Contract.ShopCartView mView;
    private List<DepProduct> mProdList = new ArrayList<>();

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
    public void initProdList(Context context) {
        List<DepCls> clsList = new ArrayList<>();
        clsList.add(new DepCls("", "", "全部类别"));
        Cursor cursor = DatabaseManger.getInstance(context).query("DepCls", new String[]{"*"}, null, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    DepCls depCls = new DepCls();
                    depCls.setDepCode(cursor.getString(cursor.getColumnIndex("DepCode")));
                    depCls.setClsCode(cursor.getString(cursor.getColumnIndex("ClsCode")));
                    depCls.setClsName(cursor.getString(cursor.getColumnIndex("ClsName")));
                    clsList.add(depCls);
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        cursor = DatabaseManger.getInstance(context).query("DepProduct", new String[]{"*"}, null, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    DepProduct depProduct = new DepProduct();
                    depProduct.setClsCode(cursor.getString(cursor.getColumnIndex("ClsCode")));
                    depProduct.setBarCode(cursor.getString(cursor.getColumnIndex("BarCode")));
                    depProduct.setProdCode(cursor.getString(cursor.getColumnIndex("ProdCode")));
                    depProduct.setProdName(cursor.getString(cursor.getColumnIndex("ProdName")));
                    depProduct.setPrice(cursor.getFloat(cursor.getColumnIndex("Price")));
                    depProduct.setSpec(cursor.getString(cursor.getColumnIndex("Spec")));
                    mProdList.add(depProduct);
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        mView.setClsList(clsList);
        mView.setProdList(mProdList);
    }

    @Override
    public void searchProdList(String key) {
        LogUtil.d("----" + key);
        if (!TextUtils.isEmpty(key)) {
            List<DepProduct> fliterList = new ArrayList<>();
            if (mProdList.size() != 0 || mProdList != null) {
                for (DepProduct depProduct : mProdList) {
                    if (!TextUtils.isEmpty(depProduct.getProdCode())) {
                        if (depProduct.getProdCode().contains(key)) {
                            fliterList.add(depProduct);
                        }
                    }
                    if (!TextUtils.isEmpty(depProduct.getProdName())) {
                        if (depProduct.getProdName().contains(key)) {
                            fliterList.add(depProduct);
                        }
                    }
                    if (!TextUtils.isEmpty(depProduct.getBarCode())) {
                        if (depProduct.getBarCode().contains(key)) {
                            fliterList.add(depProduct);
                        }
                    }
                }
            }
            mView.updateProdList(fliterList);
        } else {
            mView.updateProdList(mProdList);
        }
    }

}
