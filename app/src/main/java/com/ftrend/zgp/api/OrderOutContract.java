package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.Trade;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public interface OrderOutContract {
    interface OrderOutPresenter {
        /**
         * 初始化界面数据
         */
        void initView();

        /**
         * 取单操作
         *
         * @param lsNo 流水单号
         * @return -1 - 购物车不为空，0 - 取单成功， 1 - 取单失败
         */
        int doOrderOut(String lsNo);

        /**
         * 销毁，防止泄露
         */
        void onDestory();

    }

    interface OrderOutView extends BaseView<OrderOutContract.OrderOutPresenter> {
        /**
         * 初始化界面列表数据
         *
         * @param tradeList 数据
         */
        void initOutOrder(List<Trade> tradeList);

    }

}
