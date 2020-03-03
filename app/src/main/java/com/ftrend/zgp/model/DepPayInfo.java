package com.ftrend.zgp.model;

import com.ftrend.zgp.utils.db.ZgpDb;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = ZgpDb.class)
public class DepPayInfo extends BaseModel {
    @PrimaryKey(autoincrement = true)
    @NotNull
    private int id;
    @Column
    @NotNull
    private String depCode;
    @Column
    @NotNull
    private String payTypeCode;
    @Column
    @NotNull
    private String payTypeName;
    @Column
    @NotNull
    private String appPayType;
    @Column
//    @NotNull
    private String isScore;

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

    public String getIsScore() {
        return isScore;
    }

    public void setIsScore(String isScore) {
        this.isScore = isScore;
    }
}
