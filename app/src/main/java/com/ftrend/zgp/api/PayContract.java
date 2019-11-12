package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.Menu;
import com.sunmi.peripheral.printer.SunmiPrinterService;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public interface PayContract {
    interface PayPresenter {
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
         */
        boolean paySuccess(String appPayType);

        /**
         * 交易完成
         *
         * @param appPayType APP支付方式
         * @param value      实际支付金额
         */
        boolean paySuccess(String appPayType, double value);

        /**
         * 销毁，防止泄露
         */
        void onDestory();
    }

    interface PayView extends BaseView<PayContract.PayPresenter> {
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
