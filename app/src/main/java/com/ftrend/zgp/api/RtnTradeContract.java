package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.TradeProd;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public interface RtnTradeContract {

    //region 错误码
    /**
     * @func getTradeByLsNo
     * @err 本地短流水号未找到该流水，需要输入完整流水号联网查询历史流水
     */
    String ERR_C2111 = "C2111";
    /**
     * @func getTradeByLsNo
     * @err 联网查询流水返回的数据结果异常
     */
    String ERR_C2112 = "C2112";
    /**
     * @func getTradeByLsNo
     * @err 指定的流水不存在，因为查询的流水不是本专柜的流水
     */
    String ERR_C2113 = "C2113";
    /**
     * @func getTradeByLsNo
     * @err 查询的交易流水内没有商品，无法退货
     */
    String ERR_C2114 = "C2114";
    /**
     * @func getTradeByLsNo
     * @err 当前未联网，无法联网查询
     */
    String ERR_C2115 = "C2115";
    /**
     * @func getTradeByLsNo
     * @err 退货流水初始化失败
     */
    String ERR_C2116 = "C2116";
    /**
     * @func changePrice
     * @err 退货单价不能大于销售原价、退货单价应大于0、退货单价修改失败
     */
    String ERR_C2121 = "C2121";
    /**
     * @func changeAmount
     * @err 商品不允许退货
     */
    String ERR_C2131 = "C2131";
    /**
     * @func printer
     * @err 打印机出现故障
     */
    String ERR_C2141 = "C2141";
    /**
     * @func doSqbPay
     * @err 本地数据库退款失败
     */
    String ERR_C2151 = "C2151";
    /**
     * @func doIcCardPay
     * @err 本地数据库退款失败
     */
    String ERR_C2161 = "C2161";
    /**
     * @func doMagCard * PayrequestCardPayResult
     * @err 本地数据库退款失败
     */
    String ERR_C2171 = "C2171";
    //endregion

    interface RtnTradePresenter {
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
         * 检查当前流水类型并确定是否显示改价键盘
         *
         * @param index 商品索引
         */
        void showInputPanel(int index);

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

    interface RtnTradeView extends BaseView<RtnTradeContract.RtnTradePresenter> {
        /**
         * 结束当前界面
         */
        void returnHomeActivity();

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
