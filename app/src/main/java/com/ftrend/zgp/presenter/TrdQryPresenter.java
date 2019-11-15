package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.TrdQryContract;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.utils.TradeHelper;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public class TrdQryPresenter implements TrdQryContract.TrdQryPresenter {
    private TrdQryContract.TrdQryView mView;
    private List<Trade> tradeList;

    private TrdQryPresenter(TrdQryContract.TrdQryView mView) {
        this.mView = mView;
        tradeList = null;
    }

    public static TrdQryPresenter createPresenter(TrdQryContract.TrdQryView mView) {
        return new TrdQryPresenter(mView);
    }

    @Override
    public void initTradeList() {
        //只查询本地数据库
        tradeList = TradeHelper.getTradeList();
        mView.showTradeList(tradeList);
    }

    @Override
    public void queryTradeProd(int index) {
        if (tradeList.isEmpty()) {
            return;
        }
        mView.goTradeProdActivity(tradeList.get(index).getLsNo());
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
            tradeList = null;
        }
    }
}
