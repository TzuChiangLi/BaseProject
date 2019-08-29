package com.ftrend.zgp.model;


import com.ftrend.zgp.utils.db.DBHelper;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;


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
//
//    public Dep() {
//    }
//
//    public Dep(String depCode, String depName) {
//        this.depCode = depCode;
//        this.depName = depName;
//    }

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


