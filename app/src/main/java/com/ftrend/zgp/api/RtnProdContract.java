package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.DepProduct;
import com.ftrend.zgp.model.TradeProd;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public interface RtnProdContract {
    interface RtnProdPresenter {
        /**
         * 初始化退货信息，显示不按单退货弹窗
         */
        void showRtnProdDialog();

        /**
         * 不按单退货初始化列表
         */
        void updateRtnProdList();

        /**
         * @param index 商品索引
         */
        void delRtnProd(int index);

        /**
         * @param code        扫描码
         * @param depProducts 商品列表
         */
        void searchProdByScan(String code, List<DepProduct> depProducts);

        /**
         * @param key 关键词
         * @return 过滤商品列表
         */
        List<DepProduct> searchDepProdList(String key, List<DepProduct> depProdList);

        /**
         * 不按单退货添加退货商品
         *
         * @param prod 商品
         */
        boolean addRtnProd(DepProduct prod);


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

    interface RtnProdView extends BaseView<RtnProdContract.RtnProdPresenter> {
        /**
         * 显示弹窗
         */
        void showRtnProdDialog(List<DepProduct> mProdList);

        /**
         * 不按单退货
         *
         * @param prodList 初始化商品列表
         */
        void initProdList(List<TradeProd> prodList);

        /**
         * 行清商品
         *
         * @param index 商品索引
         */
        void delTradeProd(int index);

        /**
         * @param index 商品索引
         */
        void setScanProdPosition(int index);

        /**
         * 结束当前界面
         */
        void finish();

        /**
         * 显示改价弹窗
         *
         * @param position 位置索引
         */
        void showInputPanel(int position);

        /**
         * @param rtnTotal    退货金额
         * @param payTypeName 退货来源
         */
        void showRtnInfo(double rtnTotal, String payTypeName);

        /**
         * 加减更改
         *
         * @param index 索引
         */
        void updateTradeProd(int index);

        /**
         * @param rtnTotal 实退金额
         */
        void showRtnTotal(double rtnTotal);


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
