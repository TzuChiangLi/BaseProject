package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.TradeProd;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public interface RtnContract {
    interface RtnProdPresenter {
        /**
         * 获取流水
         *
         * @param lsNo 流水号
         */
        void getTradeByLsNo(String lsNo);


        /**
         * 更新信息
         */
        void updateTradeInfo();

        /**
         * 退货
         */
        void rtnTrade();

        /**
         * 改商品价格
         *
         * @param index 商品索引
         * @param price 商品退货改价
         */
        void changePrice(int index, double price);


        /**
         * 更改商品数量
         *
         * @param index        商品索引
         * @param changeAmount 改变数量
         */
        void changeAmount(int index, double changeAmount);


        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface RtnProdView extends BaseView<RtnContract.RtnProdPresenter> {
        /**
         * @param hasRtned 是否已退
         */
        void showTradeFlag(boolean hasRtned);

        /**
         * 加减更改
         *
         * @param index 索引
         */
        void updateTradeProd(int index);

        /**
         * 存在此流水并显示
         *
         * @param data 流水内商品信息
         */
        void existTrade(List<TradeProd> data);

        /**
         * @param tradeTotal 订单金额
         */
        void showTradeTotal(double tradeTotal);

        /**
         * @param rtnTotal 实退金额
         */
        void showRtnTotal(double rtnTotal);

        /**
         * 支付方式类型
         *
         * @param payTypeName 名称
         * @param img         图片
         */
        void showPayTypeName(String payTypeName, int img);

        /**
         * @param info 交易时间，流水号，收款员
         */
        void showTradeInfo(String... info);

        /**
         * 异常信息
         *
         * @param msg 文本
         */
        void showError(String msg);

        /**
         * 成功信息
         *
         * @param msg 文本
         */
        void showSuccess(String msg);
    }
}
