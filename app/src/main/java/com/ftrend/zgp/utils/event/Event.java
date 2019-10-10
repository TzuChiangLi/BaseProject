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
    /**
     * 目标界面：支付方式
     */
    public static final int TARGET_PAY_WAY = 2;
    //-----------------------------------操作命令-----------------------------------------//

    /**
     * 刷新界面
     */
    public static final int TYPE_REFRESH = 0;
    /**
     * 选择商品界面取消改价时撤销已添加的操作
     */
    public static final int TYPE_CANCEL_PRICE_CHANGE = 1;
    /**
     * 购物车----刷新整单优惠
     */
    public static final int TYPE_REFRESH_WHOLE_PRICE = 2;
    /**
     * 购物车----刷新会员信息
     */
    public static final int TYPE_REFRESH_VIP_INFO = 3;
    /**
     * 进入扫码界面
     */
    public static final int TYPE_ENTER_SCAN = 4;
    /**
     * 找零界面----结账按钮
     */
    public static final int TYPE_PAY_CASH = 5;

    /**
     * 购物车----提交优惠
     */
    public static final int TYPE_COMMIT_WHOLE_DSC = 6;

    public static final int TYPE_DIALOG_CANCEL_TRADE = 7;
    public static final int TYPE_DIALOG_VIP_DSC = 8;
    public static final int TYPE_DIALOG_WHOLE_DSC = 9;
    public static final int TYPE_DIALOG_HANG_UP = 10;

    /**
     * 提示信息
     */
    public static final int TYPE_TOAST = 700;

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
