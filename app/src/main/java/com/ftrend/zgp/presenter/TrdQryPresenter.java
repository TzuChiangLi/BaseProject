package com.ftrend.zgp.presenter;

import android.os.Handler;
import android.text.TextUtils;

import com.ftrend.zgp.api.TrdQryContract;
import com.ftrend.zgp.model.Trade;
import com.ftrend.zgp.utils.TradeHelper;

import java.util.ArrayList;
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
    public void queryTradeProd(final int index) {
        if (tradeList.isEmpty()) {
            return;
        }
        //此处初始化数据
        if (TradeHelper.queryTradeByLsNo(tradeList.get(index).getLsNo())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mView.goTradeProdActivity(tradeList.get(index).getLsNo());
                }
            }, 800);

        }
    }

    @Override
    public void search(String lsNo) {
        if (tradeList.isEmpty()) {
            return;
        }
        if (TextUtils.isEmpty(lsNo)) {
            mView.updateFilterTrade(tradeList);
        } else {
            List<Trade> filterList = new ArrayList<>();
            for (Trade trade : tradeList) {
                if (trade.getLsNo().equals(lsNo) || trade.getLsNo().contains(lsNo)
                        || lsNo.contains(trade.getLsNo())) {
                    filterList.add(trade);
                }
            }
            mView.updateFilterTrade(filterList);
        }
    }

    @Override
    public void onDestory() {
        if (mView != null) {
            mView = null;
            tradeList = null;
            TradeHelper.clear();
            TradeHelper.clearVip();
        }
    }
}
