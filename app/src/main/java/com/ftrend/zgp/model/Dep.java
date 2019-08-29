package com.ftrend.zgp.model;

import com.dbflow5.annotation.Column;
import com.dbflow5.annotation.PrimaryKey;
import com.dbflow5.annotation.Table;
import com.dbflow5.structure.BaseModel;
import com.ftrend.zgp.utils.db.DBHelper;

/**
 * 可登录专柜信息
 *
 * @author liziqiang@ftrend.cn
 */
@Table(database = DBHelper.class)
public class Dep extends BaseModel {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private String depName;
    @Column
    private String depCode;

    public Dep() {
    }

    public Dep(String depCode, String depName) {
        this.depCode = depCode;
        this.depName = depName;
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

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }
}


