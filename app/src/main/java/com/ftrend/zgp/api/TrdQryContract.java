package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.Trade;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public interface TrdQryContract {
    interface TrdQryPresenter {
        /**
         * 初始化流水列表
         */
        void initTradeList();

        /**
         * 查询单据详情
         *
         * @param index 索引
         */
        void queryTradeProd(int index);

        /**
         * 筛选
         *
         * @param lsNo 流水号
         */
        void search(String lsNo);

        /**
         * 解耦销毁防止内存泄漏
         */
        void onDestory();

    }

    interface TrdQryView extends BaseView<TrdQryContract.TrdQryPresenter> {
        /**
         * @param trdList 交易列表数据
         */
        void showTradeList(List<Trade> trdList);

        /**
         * @param trdList 过滤后的交易流水
         */
        void updateFilterTrade(List<Trade> trdList);

        /**
         * @param lsNo 流水号
         */
        void goTradeProdActivity(String lsNo);
    }
}
