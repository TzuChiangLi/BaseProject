package com.ftrend.zgp.model;

/**
 * 专柜商品类别
 *
 * @author liziqiang@ftrend.cn
 */
public class DepCls {
    private int ID;
    private String DepCode;
    private String ClsCode;
    private String ClsName;

    public DepCls() {
    }

    public DepCls(String depCode, String clsCode, String clsName) {
        DepCode = depCode;
        ClsCode = clsCode;
        ClsName = clsName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDepCode() {
        return DepCode;
    }

    public void setDepCode(String depCode) {
        DepCode = depCode;
    }

    public String getClsCode() {
        return ClsCode;
    }

    public void setClsCode(String clsCode) {
        ClsCode = clsCode;
    }

    public String getClsName() {
        return ClsName;
    }

    public void setClsName(String clsName) {
        ClsName = clsName;
    }
}
