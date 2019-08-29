package com.ftrend.zgp.model;

import com.ftrend.zgp.utils.db.DBHelper;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 专柜商品类别
 *
 * @author liziqiang@ftrend.cn
 */
@Table(database = DBHelper.class)
public class DepCls extends BaseModel {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private String depCode;
    @Column
    private String clsCode;
    @Column
    private String clsName;

    public DepCls() {
    }

    public DepCls(String depCode, String clsCode, String clsName) {
        this.depCode = depCode;
        this.clsCode = clsCode;
        this.clsName = clsName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public String getClsCode() {
        return clsCode;
    }

    public void setClsCode(String clsCode) {
        this.clsCode = clsCode;
    }

    public String getClsName() {
        return clsName;
    }

    public void setClsName(String clsName) {
        this.clsName = clsName;
    }
}
