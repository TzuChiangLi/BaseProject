package com.ftrend.zgp.api;

import android.content.Context;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.TradeProd;
import com.ftrend.zgp.model.VipInfo;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public interface ShopListContract {
    interface ShopListPresenter {
        /**
         * 刷新交易信息
         */
        void refreshTrade();

        /**
         * 检查取消交易权限
         */
        void checkCancelTradeRight();

        /**
         * 检查是否拥有会员优惠权限并展示界面
         */
        void showVipInfo();

        /**
         * 商品是否允许优惠,弹出相应提示
         *
         * @param index 索引
         */
        void checkProdForDsc(int index);

        /**
         * 显示购物车内的所有商品
         */
        void initShopList();

        /**
         * 设置交易状态
         *
         * @param status 交易状态
         */
        void setTradeStatus(String status);


        /**
         * 更改商品数量
         *
         * @param index        商品索引
         * @param changeAmount 改变数量
         */
        void changeAmount(int index, double changeAmount);

        /**
         * 更新交易信息
         */
        void updateTradeInfo();

        /**
         * 更新列表数据
         *
         * @param index     索引
         * @param tradeProd 修改的数据
         */
        void updateTradeList(int index, TradeProd tradeProd);

        /**
         * 检查行情权限
         *
         * @param index 索引
         */
        void checkDelProdRight(int index);

        /**
         * 行清商品
         *
         * @param index 索引
         */
        void delTradeProd(int index);

        /**
         * 会员输入（刷卡或者输入手机号）
         */
        void vipInput(final Context context);

        /**
         * 查询专柜商品信息表中该商品的改价权限
         *
         * @param prodCode 商品编码，可能不唯一
         * @param barCode  商品条码，可能为空
         * @param index    商品索引
         */
        void getProdPriceFlag(String prodCode, String barCode, int index);

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface ShopListView extends BaseView<ShopListContract.ShopListPresenter> {

        /**
         * 展示会员信息
         */
        void showVipInfoOnline();

        /**
         * 检测到交易已有会员优惠，但是此时离线
         */
        void showVipInfoOffline(VipInfo vip);

        /**
         * 弹窗显示错误信息文本
         *
         * @param error 错误信息文本
         */
        void showError(String error);

        /**
         * 显示流水单内商品
         *
         * @param prodList 购物车商品信息
         */
        void showTradeProd(List<TradeProd> prodList);

        /**
         * 更新合计金额
         */
        void updateTotal(double total);

        /**
         * 更新购物车总商品数
         */
        void updateCount(double count);

        /**
         * 更新界面 - 行清
         *
         * @param index 索引
         */
        void delTradeProd(int index);

        /**
         * 加减更改
         *
         * @param index 索引
         */
        void updateTradeProd(int index);

        /**
         * 返回界面
         *
         * @param status 更改状态
         */
        void returnHomeActivity(String status);

        /**
         * 是否可以改价
         *
         * @param index 索引
         */
        void showPriceChangeDialog(int index);

        /**
         * 单项优惠
         *
         * @param index 索引
         */
        void showSingleDscDialog(int index);

        /**
         * 单项优惠
         *
         * @param msg 文本
         */
        void showNoRightDscDialog(String msg);

        /**
         * 允许行清
         *
         * @param index 索引
         */
        void hasDelProdRight(int index);

    }

}
