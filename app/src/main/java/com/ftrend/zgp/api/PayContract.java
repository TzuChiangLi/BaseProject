package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.Menu;
import com.sunmi.peripheral.printer.SunmiPrinterService;

import java.util.List;

/**
 * 结算界面接口
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin
 * @since 2019/11/11
 */
public interface PayContract {

    /**
     * 卡查询请求
     */
    int MSG_CARD_QUERY_REQUEST = 1;
    /**
     * 卡查询结果
     */
    int MSG_CARD_QUERY_RESULT = 2;
    /**
     * 卡密码输入
     */
    int MSG_CARD_PASSWORD = 3;
    /**
     * 卡支付请求
     */
    int MSG_CARD_PAY_REQUEST = 4;
    /**
     * 卡支付结果
     */
    int MSG_CARD_PAY_RESULT = 5;
    /**
     * 手工输入卡号
     */
    int MSG_CARD_CODE_INPUT = 6;

    interface Presenter {
        //TODO 测试打印
        void getPrintData(SunmiPrinterService service);

        /**
         * 初始化界面
         */
        void initPayWay();

        /**
         * 收钱吧
         *
         * @param value 扫码结果
         */
        void payByShouQian(String value);

        /**
         * 交易完成
         *
         * @param appPayType APP支付方式
         * @param value      实际支付金额
         * @param payCode    支付账号（卡号）
         */
        boolean paySuccess(String appPayType, double value, String payCode);

        /**
         * 销毁，防止泄露
         */
        void onDestory();

        /**
         * 储值卡支付
         */
        void cardPay();

        /**
         * 储值卡支付（只支持磁卡）
         *
         * @param cardCode 磁卡卡号
         */
        void cardPay(String cardCode);

        /**
         * 校验卡支付密码
         *
         * @param pwd
         */
        void cardPayPass(String pwd);

        /**
         * 取消储值卡支付（只能取消刷卡操作）
         *
         * @return
         */
        boolean cardPayCancel();

        /**
         * 储值卡支付重试
         */
        void cardPayRetry();
    }

    interface View extends BaseView<Presenter> {
        /**
         * 显示等待消息
         *
         * @param msg
         */
        void cardPayWait(String msg);

        /**
         * 支付成功
         *
         * @param msg
         */
        void cardPaySuccess(String msg);

        /**
         * 支付失败
         *
         * @param msg
         */
        void cardPayFail(String msg);

        /**
         * 支付失败
         *
         * @param code
         * @param msg
         */
        void cardPayFail(String code, String msg);

        /**
         * 支付处理超时
         *
         * @param msg
         */
        void cardPayTimeout(String msg);

        /**
         * 输入支付密码
         */
        void cardPayPassword();


        /**
         * 界面
         *
         * @param payWay 图标、文字
         */
        void showPayway(List<Menu.MenuList> payWay);

        /**
         * 显示应收款
         *
         * @param total 订单总金额
         */
        void showTradeInfo(double total);

        /**
         * 等待付款结果
         */
        void waitPayResult();

        /**
         * 支付成功
         */
        void paySuccess();

        /**
         * 支付失败
         *
         * @param msg 错误信息
         */
        void payFail(String msg);

        /**
         * 显示错误
         *
         * @param msg 错误信息
         */
        void showError(String msg);
    }

}
