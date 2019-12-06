package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.RtnProdContract;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.DepProduct_Table;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.RtnHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.log.LogUtil;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */
public class RtnProdPresenter implements RtnProdContract.RtnProdPresenter {
    private RtnProdContract.RtnProdView mView;

    private RtnProdPresenter(RtnProdContract.RtnProdView mView) {
        this.mView = mView;
    }

    public static RtnProdPresenter createPresenter(RtnProdContract.RtnProdView mView) {
        return new RtnProdPresenter(mView);
    }

    @Override
    public void showRtnProdDialog() {
        RtnHelper.initSale();
        List<DepProduct> mProdList = SQLite.select().from(DepProduct.class)
                .where(DepProduct_Table.depCode.eq(ZgParams.getCurrentDep().getDepCode()))
                //3-注销；2-暂停销售
                .and(DepProduct_Table.prodStatus.notIn("2", "3"))
                //季节销售商品
                .and(OperatorGroup.clause(DepProduct_Table.season.eq("0000"))
                        .or(DepProduct_Table.season.like(ShopCartPresenter.makeSeanFilter())))
                .queryList();
        for (DepProduct product : mProdList) {
            product.setSelect(false);
        }
        mView.showRtnProdDialog(mProdList);
    }

    @Override
    public void updateRtnProdList() {
        mView.initProdList(RtnHelper.getRtnProdList());
        updateTradeInfo();
    }

    @Override
    public void delRtnProd(int index) {
        if (RtnHelper.delProduct(index)) {
            mView.delTradeProd(index);
        }
        updateTradeInfo();
    }

    @Override
    public void searchProdByScan(String code, List<DepProduct> depProducts) {
        boolean hasFlag = false;
        for (int i = 0; i < depProducts.size(); i++) {
            if (code.equals(depProducts.get(i).getProdCode()) ||
                    code.equals(depProducts.get(i).getBarCode())) {
                hasFlag = true;
                mView.setScanProdPosition(i);
                addRtnProd(depProducts.get(i));
                break;
            }
            if (i == depProducts.size() && hasFlag == false) {
                mView.showError("无此商品");
            }
        }
    }


    @Override
    public List<DepProduct> searchDepProdList(String key, List<DepProduct> depProdList) {
        if (!depProdList.isEmpty()) {
            return RtnHelper.searchDepProdList(key, depProdList);
        }
        return null;
    }

    @Override
    public boolean addRtnProd(DepProduct depProduct) {
        if (!RtnHelper.addProduct(depProduct)) {
            LogUtil.e("向数据库添加商品失败");
            return false;
        } else {
            updateTradeInfo();
            return true;
        }
    }


    @Override
    public void updateTradeInfo() {
        //获取退货流水金额
        mView.showRtnTotal(RtnHelper.getRtnTotal());
    }

    @Override
    public void changePrice(int index, double price) {
        TradeProd prod = RtnHelper.getRtnProdList().get(index);
        if (price > (prod.getTotal() / prod.getAmount())) {
            mView.showError("退货单价不能大于原销售单价");
            return;
        }
        if (price == 0) {
            mView.showError("退货单价应大于0");
            return;
        }
        if (RtnHelper.changeRtnProdPrice(index, price)) {
            mView.updateTradeProd(index);
            updateTradeInfo();
        } else {
            mView.showError("退货单价修改失败");
        }
    }

    @Override
    public void changeAmount(int index, double changeAmount) {
        //仅修改临时数据，不修改数据库内数据
        if (RtnHelper.changeRtnProdAmount(index, changeAmount)) {
            //更新列表界面
            mView.updateTradeProd(index);
            //更新底部信息
            updateTradeInfo();
        }
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
        RtnHelper.clearAllData();
    }
}
//                                RestSubscribe.getInstance().queryRefundLs(lsNo, new RestCallback(
//                                        new RestResultHandler() {
//                                            @Override
//                                            public void onSuccess(Map<String, Object> body) {
//                                                Map<String, Object> trade = (Map<String, Object>) body.get("trade");
//                                                List<Map<String, Object>> prod = (List<Map<String, Object>>) body.get("prod");
//                                                Map<String, Object> pay = (Map<String, Object>) body.get("pay");
//
//                                            }
//
//                                            @Override
//                                            public void onFailed(String errorCode, String errorMsg) {
//                                                mView.showError(errorMsg);
//                                            }
//                                        }));