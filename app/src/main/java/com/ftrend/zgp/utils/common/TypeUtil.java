package com.ftrend.zgp.utils.common;

/**
 * @author liziqiang@ftrend.cn
 */

public class TypeUtil {
    /**
     * 弹窗工具----更新类型定义
     */
    public  enum AsyncType {
        /**
         * 无
         */
        none,
        /**
         * 数据更新同步
         */
        data
    }

    /**
     * 弹窗工具----对话框类型定义
     */
    public enum DialogType {
        /**
         * 信息弹窗
         */
        info,
        /**
         * 警告弹窗
         */
        warning,
        /**
         * 错误弹窗
         */
        error,
        /**
         * 询问弹窗
         */
        question,
        /**
         * 同步弹窗
         */
        async
    }

}
