package com.ftrend.zgp.utils.event;

import org.greenrobot.eventbus.EventBus;

/**
 * EventBus传递指令
 *
 * @author liziqiang@ftrend.cn
 */
public class Event {
    //-----------------------------------目标界面-----------------------------------------//

    /**
     * 目标界面：选择商品
     */
    public static final int TARGET_SHOP_CART = 0;
    /**
     * 目标界面：购物车
     */
    public static final int TARGET_SHOP_LIST = 1;
    //-----------------------------------操作命令-----------------------------------------//

    /**
     * 刷新界面
     */
    public static final int TYPE_REFRESH = 0;

    public static final int TYPE_CANCEL_PRICE_CHANGE = 1;

    public static final int TYPE_REFRESH_WHOLE_PRICE = 2;
    //-----------------------------------简单数据-----------------------------------------//
    //一般用不到
    private Object data;
    //-----------------------------------------------------------------------------------//
    private int target, type;


    /**
     * 发送命令
     *
     * @param target 目标界面
     * @param type   操作命令
     */
    public static void sendEvent(int target, int type) {
        Event event = new Event(target, type);
        EventBus.getDefault().post(event);
    }

    /**
     * 携带简单数据的命令
     *
     * @param target 目标界面
     * @param type   操作命令
     * @param data   简单数据（索引）
     */
    public static void sendEvent(int target, int type, Object data) {
        Event event = new Event(target, type, data);
        EventBus.getDefault().post(event);
    }

    public Event(int target, int type) {
        this.target = target;
        this.type = type;
    }

    public Event(int target, int type, Object data) {
        this.data = data;
        this.target = target;
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
