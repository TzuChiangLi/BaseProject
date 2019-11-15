package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;

/**
 * @author liziqiang@ftrend.cn
 */

public interface TrdProdContract {
    interface TrdProdPresenter {
        /**
         * 初始化显示的流水信息
         */
        void initTradeInfo();
    }

    interface TrdProdView extends BaseView<TrdProdContract.TrdProdPresenter> {
        /**
         * 显示商品详情和流水信息
         */
        void showTradeProd();
    }
}
