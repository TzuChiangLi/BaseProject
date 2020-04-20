package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.DepCls;
import com.ftrend.zgp.model.Product;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public interface ShopCartContract {

    //region 错误代码
    /**
     * @func addToShopCart
     * @err 此单据保存有会员信息，但是无法联网获取会员信息
     */
    String ERR_A2111 = "A2111";
    /**
     * @func addToShopCart-TradeHelper.addProduct
     * @err 向数据库中添加商品失败，可能因为数据库存在异常，执行SQL操作出现问题
     */
    String ERR_A2112 = "A2112";
    /**
     * @func checkCancelTradeRight
     * @err 该用户无权限取消交易订单
     */
    String ERR_A2121 = "A2121";
    /**
     * @func checkProdForDsc
     * @err 该商品不允许优惠
     */
    String ERR_A2131 = "A2131";
    /**
     * @func checkDelProdRight
     * @err 该用户无行清权限
     */
    String ERR_A2141 = "A2141";
    /**
     * @func vipInput-readCard
     * @err 刷卡服务不可用，可能因为设备不支持刷卡功能或者非商米设备
     */
    String ERR_A2151 = "A2151";
    /**
     * @func queryVipInfo
     * @err 查询会员信息返回成功，但是返回的结果为空
     */
    String ERR_A2161 = "A2161";
    /**
     * @func getProdPriceFlag
     * @err 该商品不允许改价
     */
    String ERR_A2171 = "A2171";
    //endregion


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
         * @param product 商品对象
         */
        void addToShopCart(Product product);

        /**
         * 添加到购物车
         *
         * @param product 商品对象
         * @param price   价格
         */
        void addToShopCart(Product product, double price);

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
        void searchProdByScan(String code, List<Product> prodList);

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
        void setProdList(List<Product> prodList);

        /**
         * 返回过滤筛选后的商品列表
         *
         * @param prodList 商品列表
         */
        void updateProdList(List<Product> prodList);


        /**
         * 更新界面购物车的数量
         *
         * @param num 购物车内的数量
         */
        void updateTradeProd(long num, double price);


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
