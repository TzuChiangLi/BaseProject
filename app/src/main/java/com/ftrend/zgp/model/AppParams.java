package com.ftrend.zgp.model;

import com.ftrend.zgp.utils.db.DBHelper;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * APP配置参数
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/8/29
 */
@Table(database = DBHelper.class)
public class AppParams extends BaseModel {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private String paramName;
    @Column
    private String paramValue;

    public AppParams() {
    }

    public AppParams(String paramName, String paramValue) {
        this.paramName = paramName;
        this.paramValue = paramValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }
}
