package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.RtnProdContract;
import com.ftrend.zgp.model.Product;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.RtnHelper;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */
public class RtnProdPresenter implements RtnProdContract.RtnProdPresenter {
    private static final String TAG = "RtnProdPresenter";
    private RtnProdContract.RtnProdView mView;
    private List<Product> mProdList = new ArrayList<>();

    //查询参数
    private int mPageSize = 20;//每页行数
    private int mPage = 0;//当前页
    private String mQueryStr = null;//查询字符串，用于扫码或模糊查询

    private RtnProdPresenter(RtnProdContract.RtnProdView mView) {
        this.mView = mView;
    }

    public static RtnProdPresenter createPresenter(RtnProdContract.RtnProdView mView) {
        return new RtnProdPresenter(mView);
    }

    @Override
    public void showRtnProdDialog() {
        RtnHelper.initSale();
        mPage = 0;
        mQueryStr = null;
        loadPage();
        mView.showRtnProdDialog(mProdList);
    }

    @Override
    public void loadMoreProd() {
        mPage++;
        loadPage();
        mView.appendProdList(mProdList);
    }

    private void loadPage() {
        mProdList = TradeHelper.loadProduct(null, mQueryStr, mPage, mPageSize);
        for (Product product : mProdList) {
            product.setSelect(false);
        }
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
            updateTradeInfo();
        }
    }

    @Override
    public void searchProdByScan(String code, List<Product> products) {
        mPage = 0;
        mQueryStr = code;
        loadPage();
        mView.updateProdList(mProdList);
        if (mProdList.size() == 0) {
            mView.showError("无此商品");
        } else {
            addRtnProd(mProdList.get(0));
        }
    }


    @Override
    public List<Product> searchDepProdList(String key, List<Product> depProdList) {
        mPage = 0;
        mQueryStr = key;
        loadPage();
        return mProdList;
    }

    @Override
    public void addRtnProd(Product prod) {
        addRtnProd(prod, prod.getPrice());
    }

    @Override
    public void addRtnProd(Product prod, double price) {
        if (prod.getForSaleRet() == 1) {
            //不允许退货
            mView.showError("该商品不允许退货");
        } else {
            prod.setPrice(price);
            if (!RtnHelper.addProduct(prod)) {
                LogUtil.e("向数据库添加商品失败");
            } else {
                updateTradeInfo();
            }
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
        Product product = RtnHelper.getProduct(prod.getProdCode());
        if ((price > (prod.getTotal() / prod.getAmount())) && product.getPrice() != 0) {
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
    public void checkInputNum(int index, double changeAmount) {
        if ("1".equals(ZgParams.getInputNum())) {
            mView.showInputNumDialog(index);
        } else {
            changeAmount(index, changeAmount);
        }
    }

    @Override
    public void coverAmount(int index, double changeAmount) {
        if (RtnHelper.coverRtnProdAmount(index, changeAmount)) {
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