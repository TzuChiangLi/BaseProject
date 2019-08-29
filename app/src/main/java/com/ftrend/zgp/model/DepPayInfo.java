package com.ftrend.zgp.model;

import com.ftrend.zgp.utils.db.DBHelper;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 专柜支付方式
 * Copyright (C),青岛致远方象软件科技有限公司
 *
 * @author liuhongbin@ftrend.cn
 * @since 2019/8/29
 */
@Table(database = DBHelper.class)
public class DepPayInfo extends BaseModel {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private String depCode;
    @Column
    private String payTypeCode;
    @Column
    private String payTypeName;
    @Column
    private String appPayType;

    public DepPayInfo() {
    }

    public DepPayInfo(String depCode, String payTypeCode, String payTypeName, String appPayType) {
        this.depCode = depCode;
        this.payTypeCode = payTypeCode;
        this.payTypeName = payTypeName;
        this.appPayType = appPayType;
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

    public String getPayTypeCode() {
        return payTypeCode;
    }

    public void setPayTypeCode(String payTypeCode) {
        this.payTypeCode = payTypeCode;
    }

    public String getPayTypeName() {
        return payTypeName;
    }

    public void setPayTypeName(String payTypeName) {
        this.payTypeName = payTypeName;
    }

    public String getAppPayType() {
        return appPayType;
    }

    public void setAppPayType(String appPayType) {
        this.appPayType = appPayType;
    }
}
