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
         *
         * @param page 第一页
         */
        void loadTradeList(int page);

        /**
         * 设置专柜名
         */
        void setDep();

        /**
         * 加载更多
         *
         * @param page 分页页数
         */
        void addTradeData(int page);

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
         * @param trdList 分页加载更多
         */
        void loadMoreTrade(List<Trade> trdList);

        /**
         * 加载结束
         */
        void loadMoreEnd();

        /**
         * @param trdList 过滤后的交易流水
         */
        void updateFilterTrade(List<Trade> trdList);

        /**
         * @param canLoadMore 是否可以加载更多
         */
        void setEnableLoadMore(boolean canLoadMore);

        /**
         * @param lsNo 流水号
         */
        void goTradeProdActivity(String lsNo);

        /**
         * @param msg 文本
         */
        void showError(String msg);

        /**
         * 显示专柜名
         * @param flag 是否显示
         */
        void showDepName(boolean flag);
    }
}
