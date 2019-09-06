package com.ftrend.zgp.presenter;

import android.text.TextUtils;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepCls_Table;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.DepProduct_Table;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.TradeProd_Table;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.http.HttpCallBack;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import java.util.ArrayList;
import java.util.List;

import static com.raizlabs.android.dbflow.sql.language.Method.count;
import static com.raizlabs.android.dbflow.sql.language.Method.sum;

/**
 * 收银-选择商品P层
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopCartPresenter implements Contract.ShopCartPresenter, HttpCallBack {
    private Contract.ShopCartView mView;
    private List<DepProduct> mProdList = new ArrayList<>();

    private ShopCartPresenter(Contract.ShopCartView mView) {
        this.mView = mView;
    }

    public static ShopCartPresenter createPresenter(Contract.ShopCartView mView) {
        return new ShopCartPresenter(mView);
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onSuccess(Object body) {

    }

    @Override
    public void onFailed(String errorCode, String errorMessage) {

    }

    @Override
    public void onHttpError(int errorCode, String errorMsg) {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void initProdList() {
        List<DepCls> clsList = SQLite.select().from(DepCls.class).where(DepCls_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode())).queryList();
        mProdList = SQLite.select().from(DepProduct.class).where(DepProduct_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode())).queryList();
        //region 可能数据表自己测试用的有点问题，此处修复那个问题后再把牵扯到V层的代码修正掉
        mView.setProdList(mProdList);
        DepCls depCls = new DepCls();
        depCls.setClsName("全部类别");
        depCls.setDepCode("all");
        depCls.setClsCode("all");

        clsList.add(0, depCls);
        mView.setClsList(clsList);
        //endregion
    }

    @Override
    public void initOrderInfo(String lsNo) {
        long count = SQLite.select(count(TradeProd_Table.id)).from(TradeProd.class).where(TradeProd_Table.lsNo.eq(lsNo)).count();
        FlowCursor csr = SQLite.select(sum(TradeProd_Table.price)).from(TradeProd.class).where(TradeProd_Table.lsNo.eq(lsNo)).query();
        csr.moveToFirst();
        float price = csr.getFloat(0);
        mView.updateTradeProd(count, price);
    }

    @Override
    public void searchProdList(String key) {
        if (!TextUtils.isEmpty(key)) {
            if (key.equals("all")) {
                mView.updateProdList(mProdList);
                return;
            }
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
            }//TODO 如果是空那么需要用emptyView
            mView.updateProdList(fliterList);
        } else {
            mView.updateProdList(mProdList);
        }
    }

    @Override
    public void addToShopCart(DepProduct depProduct, String lsNo) {
        TradeProd tradeProd = new TradeProd();
        tradeProd.setLsNo(lsNo);
        tradeProd.setProdCode(depProduct.getProdCode());
        tradeProd.setProdName(depProduct.getProdName());
        tradeProd.setDepCode(depProduct.getDepCode());
        tradeProd.setAmount(1);
        tradeProd.setPrice(depProduct.getPrice());
        tradeProd.setSortNo(String.valueOf(createSortNo(lsNo) + 1));
        tradeProd.insert();
        FlowCursor csr = SQLite.select(sum(TradeProd_Table.price)).from(TradeProd.class).where(TradeProd_Table.lsNo.eq(lsNo)).query();
        csr.moveToFirst();
        float price = csr.getFloat(0);
        mView.updateTradeProd(createSortNo(lsNo), price);
    }

    private long createSortNo(String lsNo) {
        return SQLite.select(count()).from(TradeProd.class).
                where(TradeProd_Table.lsNo.eq(lsNo)).count();
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }


//        Cursor cursor = DatabaseManger.getInstance(context).query("DepCls", new String[]{"*"}, null, null, null, null, null, null);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                do {
//                    DepCls depCls = new DepCls();
//                    depCls.setDepCode(cursor.getString(cursor.getColumnIndex("DepCode")));
//                    depCls.setClsCode(cursor.getString(cursor.getColumnIndex("ClsCode")));
//                    depCls.setClsName(cursor.getString(cursor.getColumnIndex("ClsName")));
//                    clsList.add(depCls);
//                } while (cursor.moveToNext());
//            }
//        }
//        if (cursor != null) {
//            cursor.close();
//        }
    //        cursor = DatabaseManger.getInstance(context).query("DepProduct", new String[]{"*"}, null, null, null, null, null, null);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                do {
//                    DepProduct depProduct = new DepProduct();
//                    depProduct.setClsCode(cursor.getString(cursor.getColumnIndex("ClsCode")));
//                    depProduct.setBarCode(cursor.getString(cursor.getColumnIndex("BarCode")));
//                    depProduct.setProdCode(cursor.getString(cursor.getColumnIndex("ProdCode")));
//                    depProduct.setProdName(cursor.getString(cursor.getColumnIndex("ProdName")));
//                    depProduct.setPrice(cursor.getFloat(cursor.getColumnIndex("Price")));
//                    depProduct.setSpec(cursor.getString(cursor.getColumnIndex("Spec")));
//                    mProdList.add(depProduct);
//                } while (cursor.moveToNext());
//            }
//        }
//        if (cursor != null) {
//            cursor.close();
//        }
}
