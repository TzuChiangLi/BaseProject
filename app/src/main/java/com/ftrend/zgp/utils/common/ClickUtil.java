package com.ftrend.zgp.utils.common;

/**
 * 防抖动点击
 *
 * @author liziqiang@ftrend.cn
 */
public class ClickUtil {
    /**
     * 初始值
     */
    private static long LogMillis = -1;
    /**
     * 1秒内的抖动都不计入
     */
    private static final long DELAY = 1000;


    /**
     * 检查抖动
     *
     * @return 返回false表示此次点击为单击，返回true表示抖动了
     */
    public static boolean onceClick() {
        if (LogMillis == -1) {
            LogMillis = System.currentTimeMillis();
            return false;
        }
        if (System.currentTimeMillis() - LogMillis < DELAY) {
            LogMillis = System.currentTimeMillis();
            return true;
        }
        return false;
    }
}
