package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.Contract;
import com.ftrend.zgp.utils.TradeHelper;

/**
 * @author liziqiang@ftrend.cn
 */

public class OrderOutPresenter implements Contract.OrderOutPresenter {
    private Contract.OrderOutView mView;


    private OrderOutPresenter(Contract.OrderOutView mView) {
        this.mView = mView;
    }

    public static OrderOutPresenter createPresenter(Contract.OrderOutView mView) {
        return new OrderOutPresenter(mView);
    }

    @Override
    public void initView() {
        mView.initOutOrder(TradeHelper.getOutOrder());
    }

    @Override
    public int doOrderOut(String lsNo) {
        if (!TradeHelper.cartIsEmpty()) {
            return -1;
        }
        return TradeHelper.orderOut(lsNo) ? 0 : 1;
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }
}
