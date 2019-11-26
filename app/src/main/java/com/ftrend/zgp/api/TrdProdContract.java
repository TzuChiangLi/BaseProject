package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.VipInfo;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public interface TrdProdContract {
    interface TrdProdPresenter {
        /**
         * 初始化显示的流水信息
         */
        void initTradeInfo();

        /**
         * 重打小票
         */
        void print();
    }

    interface TrdProdView extends BaseView<TrdProdContract.TrdProdPresenter> {
        /**
         * @param isSale 流水类型
         */
        void showTradeFlag(boolean isSale);
        /**
         * 显示商品详情和流水信息
         */
        void showTradeProd(List<TradeProd> prodList);

        /**
         * @param vip vip信息
         */
        void showVipInfo(VipInfo vip);

        /**
         * @param info 信息
         */
        void showTradeInfo(String... info);

        /**
         * @param payTypeName 支付方式名称
         * @param img         图片资源
         */
        void showPayInfo(String payTypeName, int img);

        /**
         * 打印成功回调
         */
        void printResult();
    }
}
