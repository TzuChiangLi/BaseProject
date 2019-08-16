package com.ftrend.zgp.utils;

import android.app.Application;

import com.ftrend.toast.XToast;
import com.ftrend.zgp.R;

/**
 * 吐司工具类(在XToast库上封装)
 *
 * @author LZQ
 */
public class ToastUtil {
    private static Application APP;
    private static int Duration = 1500;

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


    public static int getDuration() {
        return Duration;
    }

    public static void setDuration(int duration) {
        Duration = duration;
    }
}
