package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.OrderOutContract;
import com.ftrend.zgp.utils.TradeHelper;
import com.ftrend.zgp.utils.log.LogUtil;

/**
 * @author liziqiang@ftrend.cn
 */

public class OrderOutPresenter implements OrderOutContract.OrderOutPresenter {
    private OrderOutContract.OrderOutView mView;


    private OrderOutPresenter(OrderOutContract.OrderOutView mView) {
        this.mView = mView;
    }

    public static OrderOutPresenter createPresenter(OrderOutContract.OrderOutView mView) {
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
        if (TradeHelper.orderOut(lsNo)) {
            LogUtil.u("取单", "取单成功");
            return 0;
        } else {
            LogUtil.u("取单", "取单失败");
            return 1;
        }
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
        }
    }
}
