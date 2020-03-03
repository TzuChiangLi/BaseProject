package com.ftrend.zgp.api;

import com.ftrend.zgp.base.BaseView;
import com.ftrend.zgp.model.Product;
import com.ftrend.zgp.model.VipInfo;

import java.util.List;

/**
 * @author liziqiang@ftrend.cn
 */

public interface VipProdContract {
    /**
     * @func readCard
     * @err 刷卡服务不可用，可能因为设备不支持刷卡功能或者非商米设备
     */
    String ERR_E2111 = "E2111";
    /**
     * @func readCard
     * @err 商品不允许退货
     */
    String ERR_E2112 = "E2112";
    /**
     * @func paySuccess
     * @err 打印机故障、数据库写入错误
     */
    String ERR_E2121 = "E2121";

    interface VipProdPresenter {
        /**
         * 销售交易完成
         *
         * @param appPayType APP支付方式
         * @param value      实际支付金额
         * @param payCode    支付账号（卡号）
         * @param balance    卡余额（储值卡和IC卡支付时有效）
         */
        boolean paySuccess(String appPayType, double value, String payCode, double balance);

        /**
         * 加载刷卡商品
         */
        void initVipProd();

        /**
         * 读卡
         *
         * @param isPay true:支付  false:读卡
         */
        void readCard(boolean isPay);

        /**
         * 显示商品弹窗
         */
        void showProdDialog();

        /**
         * @param key 关键词
         * @return 过滤商品列表
         */
        List<Product> searchDepProdList(String key, List<Product> prodList);

        /**
         * 设置刷卡商品
         *
         * @param prod 商品
         */
        void setVipProd(Product prod);

        /**
         * 储值卡支付
         *
         * @param total 金额
         */
        void pay(double total);

        /**
         * 销毁
         */
        void onDestory();

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

    interface VipProdView extends BaseView<VipProdContract.VipProdPresenter> {
        /**
         * 支付成功
         *
         * @param msg
         */
        void cardPaySuccess(String msg);

        /**
         * 显示等待消息
         *
         * @param msg
         */
        void cardPayWait(String msg);

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
         * @param mProdList 商品列表
         */
        void showProdDialog(List<Product> mProdList);

        /**
         * @param prod 显示商品
         */
        void setVipProd(String prod);

        /**
         * @param vip 会员信息
         */
        void setVipInfo(VipInfo vip);

        /**
         * @param msg 吐司
         */
        void show(String msg);

        /**
         * @param msg 错误提示
         */
        void showError(String msg);
    }
}
