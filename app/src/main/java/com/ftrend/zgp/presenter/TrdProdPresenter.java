package com.ftrend.zgp.presenter;

import com.ftrend.zgp.api.TrdProdContract;

/**
 * @author liziqiang@ftrend.cn
 */

public class TrdProdPresenter implements TrdProdContract.TrdProdPresenter {
    private TrdProdContract.TrdProdView mView;

    private TrdProdPresenter(TrdProdContract.TrdProdView mView) {
        this.mView = mView;
    }

    public static TrdProdPresenter createPresenter(TrdProdContract.TrdProdView mView) {
        return new TrdProdPresenter(mView);
    }

    @Override
    public void initTradeInfo() {

    }
}
