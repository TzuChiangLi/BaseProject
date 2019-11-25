package com.ftrend.zgp.model;

/**
 * 参数设置(包括类型标题、标题下的功能项名称)
 *
 * @author liziqiang@ftrend.cn
 */
public class Config {
    private String cfgName;
    private boolean isType = false;
    private boolean isOn;

    public Config() {
    }


    public String getCfgName() {
        return cfgName;
    }

    public void setCfgName(String cfgName) {
        this.cfgName = cfgName;
    }

    public boolean isType() {
        return isType;
    }

    public void setType(boolean type) {
        isType = type;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }
}
