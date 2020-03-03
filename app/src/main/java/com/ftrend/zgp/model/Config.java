package com.ftrend.zgp.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * 参数设置(包括类型标题、标题下的功能项名称)
 *
 * @author liziqiang@ftrend.cn
 */
public class Config implements MultiItemEntity {
    /**
     * 分组标题
     */
    public static final int TYPE_TITLE = -1;
    /**
     * 切换开关
     */
    public static final int NORMAL_SWB = 0;
    /**
     * 修改
     */
    public static final int NORMAL_MOD = 1;
    /**
     * 二级页面
     */
    public static final int NORMAL_MULTI = 2;
    /**
     * 左右都是纯文本
     */
    public static final int NORMAL_TEXT = 3;
    /**
     * 类型
     */
    private int itemType;

    /**
     * 开关状态
     */
    private boolean isOn = false;

    /**
     * 文本标题(可分组标题，也可item的文本)
     */
    private String text;

    /**
     * 右侧数据填充
     */
    private String data;

    /**
     * 是否显示错误
     */
    private boolean isErr = false;

    /**
     * 是否锁定
     */
    private boolean isLock = true;

    public Config() {
    }

    public Config(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public boolean isErr() {
        return isErr;
    }

    public void setErr(boolean err) {
        isErr = err;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
