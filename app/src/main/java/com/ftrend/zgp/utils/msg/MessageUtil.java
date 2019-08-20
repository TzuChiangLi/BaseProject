package com.ftrend.zgp.utils.msg;

import android.app.Application;
import android.content.Context;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.ftrend.toast.XToast;
import com.ftrend.zgp.R;
import com.ftrend.zgp.utils.log.LogUtil;

/**
 * @author liziqiang@ftrend.cn
 */
public class MessageUtil {

//-------------------------------------模态弹窗-----------------------------------------

    //region 模态弹窗
    /**
     * 模态弹窗需要获取上下文
     */
    private static Context mContext;

    /**
     * 提示弹窗
     *
     * @param message 提示文本
     */
    public static void info(String message) {
        mContext = mContext == null ? ActivityUtils.getTopActivity() : mContext;
        DialogBuilder builder = new DialogBuilder(mContext, 1);
        builder.setContent(message);
        builder.setLeftBtn("确定");
        builder.setOnClickListener(new DialogBuilder.OnBtnClickListener() {
            @Override
            public void onLeftBtnClick(View v) {
                LogUtil.d("----你点了左边");
            }

            @Override
            public void onRightBtnClick(View v) {
                LogUtil.d("----你点了右边");
            }
        });
        DialogUtil.showTipDialog(mContext, builder);
    }

    /**
     * 警告弹窗
     *
     * @param message 警告文本
     */
    public static void warning(String message) {
        mContext = mContext == null ? ActivityUtils.getTopActivity() : mContext;
        DialogBuilder builder = new DialogBuilder(mContext, 1);
        builder.setLeftBtn("确定");
        builder.setContent(message);
        builder.setOnClickListener(new DialogBuilder.OnBtnClickListener() {
            @Override
            public void onLeftBtnClick(View v) {
                LogUtil.d("----你点了左边");
            }

            @Override
            public void onRightBtnClick(View v) {
                LogUtil.d("----你点了右边");
            }
        });
        DialogUtil.showWarningDialog(mContext, builder);
    }

    /**
     * 错误弹窗
     *
     * @param message 错误文本
     */
    public static void error(String message) {
        mContext = mContext == null ? ActivityUtils.getTopActivity() : mContext;
        DialogBuilder builder = new DialogBuilder(mContext, 1);
        builder.setLeftBtn("确定");
        builder.setContent(message);
        builder.setOnClickListener(new DialogBuilder.OnBtnClickListener() {
            @Override
            public void onLeftBtnClick(View v) {
                LogUtil.d("----你点了左边");
            }

            @Override
            public void onRightBtnClick(View v) {
                LogUtil.d("----你点了右边");
            }
        });
        DialogUtil.showErrorDialog(mContext, builder);
    }

    /**
     * 询问弹窗
     * 默认两个按钮
     *
     * @param message 询问文本
     */
    public static void question(String message) {
        mContext = mContext == null ? ActivityUtils.getTopActivity() : mContext;
        final DialogBuilder builder = new DialogBuilder(mContext, 2);
        builder.setLeftBtn("是");
        builder.setRightBtn("否");
        builder.setContent(message);
        builder.setOnClickListener(new DialogBuilder.OnBtnClickListener() {
            @Override
            public void onLeftBtnClick(View v) {
                LogUtil.d("----你点了左边");
            }

            @Override
            public void onRightBtnClick(View v) {
                LogUtil.d("----你点了右边");
                builder.dismiss();
            }
        });
        DialogUtil.showAskDialog(mContext, builder);
    }
    //endregion


//-------------------------------------吐司工具-----------------------------------------

    //region 吐司

    /**
     * 初始化
     */
    public static void init(Application app) {
        APP = app;
    }


    /**
     * 纯文字吐司
     *
     * @param msg 吐司文本
     */
    public static void show(String msg) {
        new XToast(APP)
                .setView(R.layout.toast_nolmal_hint)
                .setDuration(Duration)
                .setText(msg)
                .show();
    }


    /**
     * 成功
     */
    public static void showSuccess() {
        new XToast(APP)
                .setView(R.layout.toast_state_hint)
                .setDuration(Duration)
                .setImageDrawable(android.R.id.icon, R.drawable.toast_success)
                .setText(android.R.id.message, "成功")
                .show();
    }

    /**
     * 成功自定义文字
     *
     * @param text
     */
    public static void showSuccess(String text) {
        new XToast(APP)
                .setView(R.layout.toast_state_hint)
                .setDuration(Duration)
                .setImageDrawable(android.R.id.icon, R.drawable.toast_success)
                .setText(android.R.id.message, text)
                .show();
    }

    /**
     * 错误
     */
    public static void showError() {
        new XToast(APP)
                .setView(R.layout.toast_state_hint)
                .setDuration(Duration)
                .setImageDrawable(android.R.id.icon, R.drawable.toast_error)
                .setText(android.R.id.message, "出现错误")
                .show();
    }

    /**
     * 错误自定义文字
     *
     * @param text
     */
    public static void showError(String text) {
        new XToast(APP)
                .setView(R.layout.toast_state_hint)
                .setDuration(Duration)
                .setImageDrawable(android.R.id.icon, R.drawable.toast_error)
                .setText(android.R.id.message, text)
                .show();
    }

    /**
     * 警告
     *
     * @param text
     */
    public static void showWarning(String text) {
        new XToast(APP)
                .setView(R.layout.toast_state_hint)
                .setDuration(Duration)
                .setImageDrawable(android.R.id.icon, R.drawable.toast_warning)
                .setText(android.R.id.message, text)
                .show();
    }


    /**
     * 吐司工具初始化所需app
     */
    private static Application APP;
    /**
     * 设置吐司停留时长
     */
    private static int Duration = 1500;

    public static int getDuration() {
        return Duration;
    }

    public static void setDuration(int duration) {
        Duration = duration;
    }
    //endregion
}
