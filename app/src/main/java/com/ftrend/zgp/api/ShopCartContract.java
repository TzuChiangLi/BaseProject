package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.DepProduct;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public interface ShopCartContract {
    interface ShopCartPresenter {
        /**
         * 刷新交易信息
         */
        void refreshTrade();

        /**
         * 加载商品信息
         */
        void initProdList();

        /**
         * 加载本次流水单号中的购物车信息
         */
        void initOrderInfo();

        /**
         * 刷新购物车信息
         */
        void updateOrderInfo();

        /**
         * 筛选商品
         *
         * @param key 筛选关键字
         */
        void searchProdList(String... key);

        /**
         * 添加到购物车
         *
         * @param depProduct 商品对象
         */
        void addToShopCart(DepProduct depProduct);

        /**
         * 添加到购物车
         *
         * @param depProduct 商品对象
         * @param price      价格
         */
        void addToShopCart(DepProduct depProduct, double price);

        /**
         * 设置交易状态
         *
         * @param status 交易状态
         */
        void setTradeStatus(String status);

        /**
         * 取消改价操作，购物车已添加的商品回滚
         *
         * @param index 索引
         */
        void cancelPriceChange(int index);

        /**
         * 更新交易信息
         */
        void updateTradeInfo();

        /**
         * 通过扫码识别并定位商品
         *
         * @param code     识别码
         * @param prodList 商品列表
         */
        void searchProdByScan(String code, List<DepProduct> prodList);

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface ShopCartView extends BaseView<ShopCartContract.ShopCartPresenter> {
        /**
         * 弹窗显示错误信息文本
         *
         * @param error 错误信息文本
         */
        void showError(String error);

        /**
         * 设置商品类别
         *
         * @param clsList 分类名称
         */
        void setClsList(List<DepCls> clsList);

        /**
         * 设置商品
         *
         * @param prodList 商品列表
         */
        void setProdList(List<DepProduct> prodList);

        /**
         * 返回过滤筛选后的商品列表
         *
         * @param prodList 商品列表
         */
        void updateProdList(List<DepProduct> prodList);


        /**
         * 更新界面购物车的数量
         *
         * @param num 购物车内的数量
         */
        void updateTradeProd(double num, double price);


        /**
         * 返回界面
         *
         * @param statusResult 状态结果
         */
        void returnHomeActivity(String statusResult);

        /**
         * 定位商品
         *
         * @param index 索引
         */
        void setScanProdPosition(int index);

        /**
         * 扫码的商品不存在
         */
        void noScanProdPosition();

        /**
         * 撤销商品添加
         *
         * @param index 索引
         */
        void cancelAddProduct(int index);

        /**
         * 刷新信息
         */
        void updateOrderInfo();
    }

}
