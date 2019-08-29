package com.ftrend.zgp.utils.setting;

import com.ftrend.zgp.utils.db.DBHelper;

/**
 * 全局参数工具类
 *
 * @author liziqiang@ftrend.cn
 */
public class SettingUtil {

    /**
     * App配置参数
     */
    public static class AppParams {
        /**
         * 数据库版本
         */
        public static int DATABASE_VERSION = DBHelper.DATABASE_VERSION;
        /**
         * 服务地址
         */
        public static String IP;
        /**
         * 机器编号
         */
        public static String MACHINE_ID;
        /**
         * 设备识别码
         */
        public static String MACHINE_IMEI;
        /**
         * 初始化标识位
         */
        public static String INIT_FINISHED;
        /**
         * 打印机参数
         */
        public static String PRINTER_CONFIG;
        /**
         * 读卡器参数
         */
        public static String CARD_READER_CONFIG;
        /**
         * 上次登录专柜
         */
        public static String LAST_DEP;
        /**
         * 上次登录用户
         */
        public static String LAST_USER;
        /**
         * 数据更新标志
         */
        public static String UPDATE_FLAG;
    }

    /**
     * 系统参数
     */
    public static class SysParams {
        /**
         * 会员卡类型
         */
        public static String CARD_TYPE;
        /**
         * 是否显示商品类别树
         */
        public static String SHOW_CLASSES;
        /**
         * 支付宝收款账号信息
         */
        public static String ALIPAY_INFO;
        /**
         * 微信支付收款账号信息
         */
        public static String WECHAT_INFO;
    }

}
