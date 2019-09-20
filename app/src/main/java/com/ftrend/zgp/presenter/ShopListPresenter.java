package com.ftrend.zgp.presenter;

import android.text.TextUtils;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.log.LogUtil;

/**
 * 收银-选择商品P层
 *
 * @author liziqiang@ftrend.cn
 */
public class ShopListPresenter implements Contract.ShopListPresenter {
    private Contract.ShopListView mView;

    private ShopListPresenter(Contract.ShopListView mView) {
        this.mView = mView;
    }

    public static ShopListPresenter createPresenter(Contract.ShopListView mView) {
        return new ShopListPresenter(mView);
    }


    @Override
    public void checkProdForDsc(String prodCode, String barCode) {
    }

    @Override
    public void initShopList(String lsNo) {
        TradeHelper.initSale();
        //加载商品列表
        mView.showTradeProd(TradeHelper.getTradeProdList());
        //获取商品总件数
        mView.updateCount(TradeHelper.getTradeCount());
        //获取商品总金额
        mView.updateTotal(TradeHelper.getTradeTotal());
    }

    @Override
    public void setTradeStatus(String status) {
        TradeHelper.setTradeStatus(status);
        mView.returnHomeActivity(TradeHelper.convertTradeStatus(status));
        TradeHelper.clear();
    }

    @Override
    public void changeAmount(int index, double changeAmount) {
        LogUtil.d("----amount:"+changeAmount);
        TradeHelper.changeAmount(index, changeAmount);
        updateTradeInfo();
        mView.updateTradeProd(index);
    }

    @Override
    public void updateTradeInfo() {
        //获取商品总件数
        mView.updateCount(TradeHelper.getTradeCount());
        //获取商品总金额
        mView.updateTotal(TradeHelper.getTradeTotal());
    }

    @Override
    public void updateTradeList(int index, TradeProd tradeProd) {
        mView.updateTradeProd(index);
    }

    /**
     * @param index 索引
     */
    @Override
    public void delTradeProd(int index) {
        TradeHelper.delProduct(index);
        mView.delTradeProd(index);
        updateTradeInfo();
    }


    /**
     * @param prodCode 商品编码，可能不唯一
     * @param barCode  商品条码，可能为空
     */
    @Override
    public void getProdPriceFlag(String prodCode, String barCode, int index) {
        //如果条码不为空，即查条码
        if (TextUtils.isEmpty(barCode)) {
            mView.showPriceChangeDialog(TradeHelper.getPriceFlagByBarCode(barCode), index);
        } else {
            mView.showPriceChangeDialog(TradeHelper.getPriceFlagByProdCode(prodCode), index);
        }

    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }


}
