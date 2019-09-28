package com.ftrend.zgp.presenter;

import android.text.TextUtils;

import com.ftrend.log.LogUtil;
import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepCls_Table;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.DepProduct_Table;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

/**
 * 收银-选择商品P层
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopCartPresenter implements Contract.ShopCartPresenter {
    private Contract.ShopCartView mView;
    private List<DepProduct> mProdList = new ArrayList<>();

    private ShopCartPresenter(Contract.ShopCartView mView) {
        this.mView = mView;
    }

    public static ShopCartPresenter createPresenter(Contract.ShopCartView mView) {
        return new ShopCartPresenter(mView);
    }


    @Override
    public void initProdList() {
        List<DepCls> clsList = SQLite.select().from(DepCls.class).where(DepCls_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode())).queryList();
        mProdList = SQLite.select().from(DepProduct.class).where(DepProduct_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode())).queryList();
        for (DepProduct product :
                mProdList) {
            product.setSelect(false);
        }
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
        mView.updateTradeProd(TradeHelper.getTradeCount(), TradeHelper.getTradeTotal());

    }

    @Override
    public void updateOrderInfo() {
        mView.updateOrderInfo();
        mView.updateTradeProd(TradeHelper.getTradeCount(), TradeHelper.getTradeTotal());
    }

    @Override
    public void searchProdList(String key) {
        if (!TextUtils.isEmpty(key)) {
            if ("all".equals(key)) {
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
            }
            mView.updateProdList(fliterList);
        } else {
            mView.updateProdList(mProdList);
        }
    }

    @Override
    public void addToShopCart(DepProduct depProduct, String lsNo) {
        if (TradeHelper.addProduct(depProduct) == -1) {
            LogUtil.e("向数据库添加商品失败");
        } else {
            updateTradeInfo();
        }


    }

    @Override
    public void setTradeStatus(String status) {
        TradeHelper.setTradeStatus(status);
        mView.returnHomeActivity(TradeHelper.convertTradeStatus(status));
        TradeHelper.clear();
    }

    @Override
    public void cancelPriceChange(int index) {
        TradeHelper.rollackPriceChangeInShopCart();
        mView.cancelAddProduct(index);
        updateTradeInfo();
    }

    @Override
    public void updateTradeInfo() {
        double price = TradeHelper.getTradeTotal();
        double count = TradeHelper.getTradeCount();
        mView.updateTradeProd(count, price);
    }

    @Override
    public void searchProdByScan(String code, List<DepProduct> prodList) {
        boolean hasFlag = false;
        for (int i = 0; i < prodList.size(); i++) {
            if (code.equals(prodList.get(i).getProdCode()) ||
                    code.equals(prodList.get(i).getBarCode())) {
                hasFlag = true;
                mView.setScanProdPosition(i);
                break;
            }
            if (i == prodList.size() && hasFlag == false) {
                mView.noScanProdPosition();
            }
        }

    }


    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }

}
