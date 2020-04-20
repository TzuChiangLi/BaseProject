package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.Product;
import com.ftrend.zgp.model.TradeProd;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */
public interface RtnProdContract {

    //region 错误码
    /**
     * @func addRtnProd
     * @err 商品不允许退货
     */
    String ERR_C2181 = "C2181";
    /**
     * @func addRtnProd
     * @err 插入数据库商品失败
     */
    String ERR_C2182 = "C2182";
    /**
     * @func changePrice
     * @err 退货单价不能大于销售原价、退货单价应大于0、退货单价修改失败
     */
    String ERR_C2191 = "C2191";
    //endregion

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
         * 扫码录入商品
         *
         * @param code     扫描码
         * @param products 商品列表
         */
        void searchProdByScan(String code, List<Product> products);

        /**
         * 筛选商品
         *
         * @param key 关键词
         * @return 过滤商品列表
         */
        List<Product> searchDepProdList(String key, List<Product> depProdList);

        /**
         * 不按单退货添加退货商品
         *
         * @param prod 商品
         */
        void addRtnProd(Product prod);

        /**
         * @param prod  商品
         * @param price 价格
         */
        void addRtnProd(Product prod, double price);

        /**
         * 更新信息
         */
        void updateTradeInfo();


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
         * 检查是否输入数量
         *
         * @param index        商品索引
         * @param changeAmount 改变数量
         */
        void checkInputNum(int index, double changeAmount);

        /**
         * 输入数量覆盖
         *
         * @param index        商品索引
         * @param changeAmount 改变数量
         */
        void coverAmount(int index, double changeAmount);

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface RtnProdView extends BaseView<RtnProdContract.RtnProdPresenter> {
        /**
         * 显示弹窗
         *
         * @param mProdList 商品列表
         */
        void showRtnProdDialog(List<Product> mProdList);

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
         * 设置位置
         *
         * @param index 商品索引
         */
        void setScanProdPosition(int index);


        /**
         * 显示改价弹窗
         *
         * @param position 位置索引
         */
        void showInputPanel(int position);


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
         * 输入数量
         *
         * @param index 索引
         */
        void showInputNumDialog(int index);

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
