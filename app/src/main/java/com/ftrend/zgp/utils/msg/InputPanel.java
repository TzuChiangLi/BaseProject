package com.ftrend.zgp.utils.msg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ftrend.zgp.utils.ZgParams;
import com.ftrend.zgp.utils.pop.CalendarSelectorDialog;
import com.ftrend.zgp.utils.pop.CommonInputDialog;
import com.ftrend.zgp.utils.pop.DateRangeInputCallback;
import com.ftrend.zgp.utils.pop.DscData;
import com.ftrend.zgp.utils.pop.DscInputCallback;
import com.ftrend.zgp.utils.pop.MoneyInputCallback;
import com.ftrend.zgp.utils.pop.PayChargeDialog;
import com.ftrend.zgp.utils.pop.PriceDscDialog;
import com.ftrend.zgp.utils.pop.ServerUrlInputCallback;
import com.ftrend.zgp.utils.pop.StringInputCallback;
import com.ftrend.zgp.utils.pop.VipMoreBtnDialog;
import com.lxj.xpopup.XPopup;

import java.util.Date;

/**
 * @author liziqiang@ftrend.cn
 */

public class InputPanel {
//-------------------------------------业务弹窗-----------------------------------------//

    /**
     * 输入数量，主要用于称重
     * @param context 上下文
     * @param callback 回调
     */
    public static void showInputNumDialog(Context context, MoneyInputCallback callback) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .autoOpenSoftInput(false)
                .asCustom(new CommonInputDialog(context, "请输入数量", "确定", "1".equals(ZgParams.getInputDecimal()),
                        callback))
                .show();
    }
    /**
     * 输入数量，主要用于称重
     * @param context 上下文
     * @param callback 回调
     */
    public static void showRtnInputNumDialog(Context context,String value, MoneyInputCallback callback) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .autoOpenSoftInput(false)
                .asCustom(new CommonInputDialog(context, String.format("请输入数量(上限：%s)",value),
                        "确定", "1".equals(ZgParams.getInputDecimal()),
                        callback))
                .show();
    }

    /**
     * @param context  上下文
     * @param def      默认值
     * @param callback 回调
     */
    public static void showServerDialog(Context context, String def, ServerUrlInputCallback callback) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .autoOpenSoftInput(true)
                .asCustom(new CommonInputDialog(context, "请输入服务地址", "连接", def, callback))
                .show();
    }

    /**
     * 会员卡：设置消费金额
     *
     * @param context 上下文
     */
    public static void showVipProdDialog(Context context, double def, MoneyInputCallback callback) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new CommonInputDialog(context, "请输入金额", "修改", def, callback))
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
    public static void showChargeDialog(Context context, double total, MoneyInputCallback callback) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new PayChargeDialog(context, total, callback))
                .show();
    }

    /**
     * 改价弹窗
     *
     * @param callback 回调
     */
    public static void showPriceChange(Context context, MoneyInputCallback callback) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new CommonInputDialog(context, "请输入修改后的商品价格：", "修改", 0, callback))
                .show();
    }

    /**
     * 会员输入弹窗
     *
     * @param callback 回调
     */
    public static void showVipMobile(Context context, StringInputCallback callback) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new CommonInputDialog(context, "请输入会员手机号：", "查询", "", 11,
                        false, true, callback))
                .show();
    }

    /**
     * 单项优惠
     *
     * @param context
     * @param data
     * @param callback
     */
    public static void showSingleDscChange(Context context, DscData data, DscInputCallback callback) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(PriceDscDialog.singleDscInput(context, data, callback))
                .show();
    }

    /**
     * 整单优惠
     *
     * @param context 上下文
     */
    public static void showWholeDscChange(Context context, DscData data, DscInputCallback callback) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(PriceDscDialog.wholeDscInput(context, data, callback))
                .show();
    }

    /**
     * 通用输入对话框
     *
     * @param context  上下文
     * @param title    对话框输入提示信息
     * @param callback 输入结果回调
     */
    public static void showInput(Context context, String title, StringInputCallback callback) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(new CommonInputDialog(context, title, "确定", "", 50, true, callback))
                .show();
    }

    /**
     * 日期选择对话框
     *
     * @param context
     * @param initDate 默认选中日期，可为null
     * @param callback
     */
    public static void showSingleCalendarSelector(@NonNull Context context,
                                                  @Nullable Date initDate,
                                                  @NonNull DateRangeInputCallback callback) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(CalendarSelectorDialog.singleSelector(context, initDate, callback))
                .show();
    }

    public static void showSingleCalendarSelector(Context context, DateRangeInputCallback callback) {
        showSingleCalendarSelector(context, null, callback);
    }

    /**
     * 日期范围选择对话框
     *
     * @param context
     * @param from     默认选中开始日期，可为null
     * @param to       默认选中结束日期，可为null
     * @param callback
     */
    public static void showMultiCalendarSelector(@NonNull Context context,
                                                 @Nullable Date from, @Nullable Date to,
                                                 @NonNull DateRangeInputCallback callback) {
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .asCustom(CalendarSelectorDialog.multiSelector(context, from, to, callback))
                .show();
    }

    public static void showMultiCalendarSelector(Context context, DateRangeInputCallback callback) {
        showMultiCalendarSelector(context, null, null, callback);
    }
}