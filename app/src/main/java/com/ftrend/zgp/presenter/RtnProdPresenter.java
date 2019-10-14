package com.ftrend.zgp.presenter;

import android.text.TextUtils;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.utils.TradeHelper;

/**
 * @author liziqiang@ftrend.cn
 */
public class RtnProdPresenter implements Contract.RtnProdPresenter {
    private Contract.RtnProdView mView;

    private RtnProdPresenter(Contract.RtnProdView mView) {
        this.mView = mView;
    }

    public static RtnProdPresenter createPresenter(Contract.RtnProdView mView) {
        return new RtnProdPresenter(mView);
    }


    @Override
    public void getTradeByLsNo(String lsNo) {
        if (TextUtils.isEmpty(lsNo)) {
            mView.showError("请输入流水单号");
        } else {
            if (TradeHelper.initSale(lsNo)) {
                //有此流水
                mView.existTrade(TradeHelper.getProdList());
            } else {
                //本地无此流水，开始联网查询
                //TODO 联网操作
                if (false) {
                    //网络有数据
                } else {
                    //网络无数据
                    mView.showError("流水号不存在");
                }

            }
        }
    }

    @Override
    public void checkDelProdRight(int index) {

    }

    @Override
    public void delTradeProd(int index) {

    }

    @Override
    public void getProdPriceFlag(String prodCode, String barCode, int index) {

    }

    @Override
    public void changeAmount(int index, double changeAmount) {

    }

    @Override
    public void checkProdForDsc(int index) {

    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }
}
