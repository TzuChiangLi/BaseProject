package com.ftrend.zgp.utils;

import android.graphics.Color;

import com.blankj.utilcode.util.ToastUtils;

/**
 * 吐司工具类
 *
 * @author LZQ
 */
public class ToastUtil {
    public static int TOP = android.view.Gravity.TOP;
    public static int CENTER = android.view.Gravity.CENTER;
    public static int BOTTOM = android.view.Gravity.BOTTOM;
    public static int BACKGROUND_COLOR = 0xFEFFFFFF;
    public static int TEXT_COLOR = Color.WHITE;
    public static int LONG_TIME = 0;
    public static int SHORT_TIME = 1;
    public static int Gravity;

    /**
     * 初始化为最初风格
     */
    public static void initStyle() {
        ToastUtil.setBackgroundColor(BACKGROUND_COLOR);
        ToastUtil.setTextColor(TEXT_COLOR);
    }

    /**
     * 默认风格弹窗
     *
     * @param text
     */
    public static void show(String text) {
        ToastUtils.showShort(text);
    }

    /**
     * 设置位置、时间吐司
     *
     * @param text
     * @param gravity
     * @param time
     */
    public static void show(String text, int gravity, int time) {
        ToastUtils.setGravity(gravity, 0, 0);
        ToastUtils.setBgColor(BACKGROUND_COLOR);
        ToastUtils.setMsgColor(TEXT_COLOR);
        if (time == SHORT_TIME) {
            ToastUtils.showShort(text);
        } else {
            ToastUtils.showLong(text);
        }
    }

    /**
     * 吐司取消
     */
    public static void dismiss() {
        ToastUtils.cancel();
    }

    public static void setBackgroundColor(int backgroundColor) {
        BACKGROUND_COLOR = backgroundColor;
    }

    public static void setTextColor(int textColor) {
        TEXT_COLOR = textColor;
    }

    public void setGravity(int gravity) {
        Gravity = gravity;
    }

}
