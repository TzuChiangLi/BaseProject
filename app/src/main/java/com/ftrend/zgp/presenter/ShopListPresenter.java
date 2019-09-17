package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.utils.TradeHelper;

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
    public void initShopList(String lsNo) {
        //加载商品列表
        mView.showTradeProd(TradeHelper.getTradeProdList());
        //获取商品总件数
        mView.updateCount(TradeHelper.getTradeCount());
        //获取商品总金额
        mView.updateTotal(TradeHelper.getTradeTotal());
    }

    @Override
    public void setTradeStatus(String lsNo, int status) {
        switch (status) {
            case 0:
                //未结
                break;
            case 1:
                //挂起
                break;
            case 2:
                //已结
                break;
            case 3:
                //取消
                TradeHelper.cancelTrade();
                mView.returnHomeActivity();
                break;
            default:
                break;
        }

    }

    @Override
    public void changeAmount(int index, double changeAmount) {
        TradeHelper.changeAmount(index, changeAmount);

        updateTradeInfo();
    }

    @Override
    public void updateTradeInfo() {
        //获取商品总件数
        mView.updateCount(TradeHelper.getTradeCount());
        //获取商品总金额
        mView.updateTotal(TradeHelper.getTradeTotal());
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }


}
