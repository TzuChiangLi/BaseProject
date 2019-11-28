package com.ftrend.zgp.presenter;

import android.os.Handler;
import android.text.TextUtils;

import com.ftrend.log.LogUtil;
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
    private long totalCount = 0, currentCount = 0;

    private TrdQryPresenter(TrdQryContract.TrdQryView mView) {
        this.mView = mView;
        tradeList = null;
    }

    public static TrdQryPresenter createPresenter(TrdQryContract.TrdQryView mView) {
        return new TrdQryPresenter(mView);
    }

    @Override
    public void loadTradeList(int page) {
        //只查询本地数据库
        tradeList = TradeHelper.getTradeListPage(0);
        //记录总条数
        totalCount = TradeHelper.getTradeListSize();
        //当前记录条数
        currentCount = tradeList.size();
        //显示第一次加载的交易记录
        mView.showTradeList(tradeList);
        if (currentCount >= totalCount) {
            mView.loadMoreEnd();
        }
    }

    @Override
    public void addTradeData(int page) {
        if (currentCount < totalCount) {
            //继续加载
            List<Trade> tempList = TradeHelper.getTradeListPage(page);
            if (tempList.size() == 0) {
                mView.loadMoreEnd();
                return;
            }
            //添加新纪录到总记录中
            tradeList.addAll(tempList);
            //更新当前位置
            currentCount = tradeList.size();
            //显示新加载的交易记录
            mView.loadMoreTrade(tempList);
        } else {
            mView.loadMoreEnd();
        }
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
        } else {
            mView.showError("该流水内无商品");
        }
    }

    @Override
    public void search(String lsNo) {
        if (tradeList.isEmpty()) {
            return;
        }
        if (TextUtils.isEmpty(lsNo)) {
            mView.updateFilterTrade(tradeList);
            mView.setEnableLoadMore(true);
        } else {
            List<Trade> filterList = new ArrayList<>();
            for (Trade trade : tradeList) {
                if (trade.getLsNo().equals(lsNo) || trade.getLsNo().contains(lsNo)
                        || lsNo.contains(trade.getLsNo())) {
                    filterList.add(trade);
                }
            }
            mView.updateFilterTrade(filterList);
            mView.setEnableLoadMore(false);
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
