package com.ftrend.zgp.utils.msg;

import android.content.Context;

import com.ftrend.zgp.utils.pop.PayChargeDialog;
import com.ftrend.zgp.utils.pop.PriceDscDialog;
import com.ftrend.zgp.utils.pop.PriceMobileDialog;
import com.ftrend.zgp.utils.pop.VipCardDialog;
import com.ftrend.zgp.utils.pop.VipMoreBtnDialog;
import com.lxj.xpopup.XPopup;

import static com.ftrend.zgp.utils.pop.PriceDscDialog.DIALOG_SINGLE_RSC;
import static com.ftrend.zgp.utils.pop.PriceDscDialog.DIALOG_WHOLE_RSC;
import static com.ftrend.zgp.utils.pop.PriceMobileDialog.DIALOG_CHANGE_PRICE;
import static com.ftrend.zgp.utils.pop.PriceMobileDialog.DIALOG_MOBILE;

/**
 * @author liziqiang@ftrend.cn
 */

public class InputPanel {
//-------------------------------------业务弹窗-----------------------------------------//
// TODO: 2019/10/16 业务弹窗移到专门的工具类，例如：InputPanel

    /**
     * 会员登录方式选择
     *
     * @param context 上下文
     */
    public static void showVipWayDialog(Context context) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new VipMoreBtnDialog(context, 0))
                .show();
    }


    /**
     * 更多功能按钮
     *
     * @param context 上下文
     */
    public static void showMoreFuncDialog(Context context) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new VipMoreBtnDialog(context, 1))
                .show();
    }


    /**
     * 现金找零
     */
    public static void showChargeDialog(Context context, double total) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new PayChargeDialog(context, total))
                .show();
    }


    /**
     * 会员卡登录
     */
    public static void showVipCard(Context context) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new VipCardDialog(context))
                .show();
    }

    /**
     * 改价弹窗
     *
     * @param index 索引
     */
    public static void showPriceChange(Context context, int index) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new PriceMobileDialog(context, DIALOG_CHANGE_PRICE, index))
                .show();
    }

    /**
     * 会员输入弹窗
     *
     * @param index 索引
     */
    public static void showVipMobile(Context context, int index) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new PriceMobileDialog(context, DIALOG_MOBILE, index))
                .show();
    }

    /**
     * 单项优惠
     *
     * @param context 上下文
     * @param index   索引
     */
    public static void showSingleDscChange(Context context, int index) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new PriceDscDialog(context, DIALOG_SINGLE_RSC, index))
                .show();
    }

    /**
     * 整单优惠
     *
     * @param context 上下文
     */
    public static void showWholeDscChange(Context context) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new PriceDscDialog(context, DIALOG_WHOLE_RSC))
                .show();
    }

    /**
     * 通用输入对话框
     *
     * @param context  上下文
     * @param title    对话框输入提示信息
     * @param callback 输入结果回调
     */
    public static void showInput(Context context, String title, PriceMobileDialog.InputCallback callback) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new PriceMobileDialog(context, title, callback))
                .show();
    }

}
