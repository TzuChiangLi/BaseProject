package com.ftrend.zgp.model;

/**
 * 为尽量减少依赖，在更新本类时，请同步复制本文件到library库的event路径下一份
 *
 * @author liziqang@ftrend.cn
 */
public class Event {
    /**
     * 目标界面
     */
    public int target;
    /**
     * 操作指令
     */
    public int type;


    /************************************目标****************************************/
    /**
     * 目标 - 初始化界面InitActivity
     */
    public static final int TARGET_INIT = 1000;


    /************************************操作****************************************/

    /**
     * 同步完成，显示信息
     */
    public static final int TYPE_INIT_FINISH = 1001;
}
